package net.coasterman10.Annihilation;

import net.coasterman10.Annihilation.bar.BarUtil;
import net.coasterman10.Annihilation.chests.ChestLocker;
import net.coasterman10.Annihilation.commands.AnnihilationCommand;
import net.coasterman10.Annihilation.maps.MapManager;
import net.coasterman10.Annihilation.maps.VotingManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Annihilation extends JavaPlugin {
    public ConfigManager config;
    public VotingManager voting;
    public MapManager maps;
    public BarUtil bar;

    private boolean ticking = false;
    private long time = -120;

    @Override
    public void onEnable() {
	SchedulerUtil.initialize(this);
	ScoreboardUtil.initialize(this);
	new AnnihilationCommand(this);
	new ChestLocker(this);
	
	new Shop(this, "Weapon", Shop.Inventories.weaponShopItems);
	new Shop(this, "Brewing", Shop.Inventories.brewingShopItems);

	config = new ConfigManager(this);
	config.loadConfigFile("config.yml");

	maps = new MapManager(getLogger(), config.getConfig("config.yml")
		.getConfigurationSection("maps"));
	voting = new VotingManager(maps);

	bar = new BarUtil(this);
    }

    @Override
    public void onDisable() {

    }

    public void startTimer() {
	voting.setCurrentForPlayers(getServer().getOnlinePlayers());
	
	for (Player p : getServer().getOnlinePlayers()) {
	    bar.setMessageAndPercent(p, ChatColor.GREEN + "Starting in 120", 0F);
	}

	ticking = true;
	SchedulerUtil.runRepeating(new Runnable() {
	    public void run() {
		onSecond();
	    }
	}, 20L, 20L);
    }

    public void startGame() {

    }

    public void advancePhase() {

    }

    public void onSecond() {
	if (ticking) {
	    time++;

	    if (time < 0L) {
		float percent = (120L + time) / 120F;
		String message = ChatColor.GREEN + "Starting in " + -time;
		for (Player p : getServer().getOnlinePlayers())
		    bar.setMessageAndPercent(p, message, percent);
	    }

	    if (time == -5L) {
		maps.selectMap(voting.getWinner());
	    }

	    if (time == 0L)
		startGame();

	    if (time > 0L) {
		long hours = time / 3600L;
		long minutes = (time - hours * 3600L) / 60L;
		long seconds = time - hours * 3600L - minutes * 60L;
		for (Player p : getServer().getOnlinePlayers())
		    bar.setMessageAndPercent(p, "Phase " + getPhase() + " | "
			    + ((hours < 10L) ? "0" + hours : hours) + ":"
			    + ((minutes < 10L) ? "0" + minutes : minutes) + ":"
			    + ((seconds < 10L) ? "0" + seconds : seconds), 1L);
		if (time % 600L == 0L) {
		    advancePhase();
		}
	    }
	}
    }

    public int getPhase() {
	if (time < 0)
	    return 0;
	if (time > 2400)
	    return 5;
	return (int) time / 600 + 1;
    }

    public long getTime() {
	return time;
    }
}

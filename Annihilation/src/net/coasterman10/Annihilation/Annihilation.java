package net.coasterman10.Annihilation;

import net.coasterman10.Annihilation.bar.BarUtil;
import net.coasterman10.Annihilation.chests.ChestLocker;
import net.coasterman10.Annihilation.commands.AnnihilationCommand;
import net.coasterman10.Annihilation.maps.MapManager;
import net.coasterman10.Annihilation.maps.VotingManager;
import net.coasterman10.Annihilation.shop.Shop;
import net.coasterman10.Annihilation.util.SchedulerUtil;
import net.coasterman10.Annihilation.util.ScoreboardUtil;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Annihilation extends JavaPlugin {
    public ConfigManager config;
    public VotingManager voting;
    public MapManager maps;
    public BarUtil bar;

    private boolean ticking = false;
    private final int phaseTime = 30;
    private final int startDelay = 10;
    private long time = Long.MIN_VALUE;

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
	voting = new VotingManager(this, maps);

	bar = new BarUtil(this);
    }

    @Override
    public void onDisable() {

    }

    public boolean startTimer() {
	if (time != Long.MIN_VALUE)
	    return false;

	voting.setCurrentForPlayers(getServer().getOnlinePlayers());

	for (Player p : getServer().getOnlinePlayers()) {
	    bar.setMessageAndPercent(p, ChatColor.GREEN + "Starting in "
		    + startDelay, 1F);
	}

	time = -startDelay;
	ticking = true;
	SchedulerUtil.runRepeating(new Runnable() {
	    public void run() {
		onSecond();
	    }
	}, 20L, 20L);
	return true;
    }

    public void startGame() {

    }

    public void advancePhase() {

    }

    public void onSecond() {
	if (ticking) {
	    time++;

	    if (time == 0L)
		startGame();

	    if (time < 0L) {
		float percent = (float) Math.abs(time) / (float) startDelay;
		String message = ChatColor.GREEN + "Starting in " + -time;
		for (Player p : getServer().getOnlinePlayers())
		    bar.setMessageAndPercent(p, message, percent);

		if (time == -5L) {
		    maps.selectMap(voting.getWinner());
		    getServer().broadcastMessage(
			    maps.getCurrentMap().getName() + ChatColor.GREEN
				    + " selected, loading...");
		    voting.end();
		}
	    }

	    if (time >= 0L) {
		String text = "Phase " + getPhase() + " | " + timeString(time);
		float percent;
		if (getPhase() == 5)
		    percent = 1;
		else {
		    int pTime = (int) time - (getPhase() - 1) * phaseTime;
		    percent = (float) pTime / (float) phaseTime;
		}
		for (Player p : getServer().getOnlinePlayers())
		    bar.setMessageAndPercent(p, text, percent);
		if (time % phaseTime == 0L) {
		    advancePhase();
		}
	    }
	}
    }

    public int getPhase() {
	if (time < 0)
	    return 0;
	if (time > 4 * phaseTime)
	    return 5;
	return (int) time / phaseTime + 1;
    }

    public long getTime() {
	return time;
    }

    private String timeString(long tSeconds) {
	long hours = tSeconds / 3600L;
	long minutes = (tSeconds - hours * 3600L) / 60L;
	long seconds = tSeconds - hours * 3600L - minutes * 60L;
	return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}

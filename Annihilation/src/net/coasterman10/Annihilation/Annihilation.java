package net.coasterman10.Annihilation;

import net.coasterman10.Annihilation.bar.BarManager;
import net.coasterman10.Annihilation.chests.ChestLocker;
import net.coasterman10.Annihilation.commands.AnnihilationCommand;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Annihilation extends JavaPlugin {
    public ConfigManager config;
    public BarManager bar;

    private boolean ticking = false;
    private long time = -120;

    @Override
    public void onEnable() {
	SchedulerUtil.initialize(this);
	new AnnihilationCommand(this);
	new ChestLocker(this);

	config = new ConfigManager(this);
	config.loadConfigFile("config.yml");

	bar = new BarManager(this);
    }

    @Override
    public void onDisable() {

    }

    public void startTimer() {
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

	    if (time < 0) {
		float percent = (120F + time) / 120F;
		String message = ChatColor.GREEN + "Starting in " + -time;
		for (Player p : getServer().getOnlinePlayers())
		    bar.setMessageAndPercent(p, message, percent);
	    }

	    if (time == 0)
		startGame();

	    if (time > 0) {
		long hours = time / 3600;
		long minutes = (time - hours * 3600) / 60;
		long seconds = time - hours * 3600 - minutes * 60;
		for (Player p : getServer().getOnlinePlayers())
		    bar.setMessageAndPercent(p, "Phase 5 | "
			    + ((hours < 10) ? "0" + hours : hours) + ":"
			    + ((minutes < 10) ? "0" + minutes : minutes) + ":"
			    + ((seconds < 10) ? "0" + seconds : seconds), 1F);
		if (time % 600 == 0) {
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
}

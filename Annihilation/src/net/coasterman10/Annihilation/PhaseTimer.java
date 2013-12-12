package net.coasterman10.Annihilation;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.coasterman10.Annihilation.bar.BarManager;
import net.coasterman10.Annihilation.util.SchedulerUtil;

public class PhaseTimer {
    private long time;
    private long startTime;
    private long phaseTime;
    private int phase;
    private boolean isRunning;

    private final Annihilation plugin;
    private final BarManager bar;

    public PhaseTimer(Annihilation plugin, long start, long period) {
	this.plugin = plugin;
	this.bar = new BarManager(plugin);
	startTime = start;
	phaseTime = period;
	phase = 0;
    }

    public void start() {
	if (!isRunning) {
	    SchedulerUtil.runRepeating(new SecondTask(), 20L, 20L);
	    isRunning = true;
	}

	time = -startTime;

	for (Player p : Bukkit.getOnlinePlayers())
	    bar.setMessageAndPercent(p, ChatColor.GREEN + "Starting in "
		    + -time, 1F);
    }

    public long getTime() {
	return time;
    }

    public long getRemainingPhaseTime() {
	if (phase == 5) {
	    return phaseTime;
	}
	if (phase >= 1) {
	    return time % phaseTime;
	}
	return -time;
    }

    public int getPhase() {
	return phase;
    }

    public boolean isRunning() {
	return isRunning;
    }

    private void onSecond() {
	time++;

	if (getRemainingPhaseTime() == 0)
	    phase++;

	float percent;
	String text;

	if (phase == 0) {
	    percent = (float) -time / (float) startTime;
	    text = ChatColor.GREEN + "Starting in " + -time;
	} else {
	    if (phase == 5)
		percent = 1F;
	    else
		percent = (float) getRemainingPhaseTime() / (float) phaseTime;
	    text = "Phase " + phase + " | " + timeString();
	}

	for (Player p : Bukkit.getOnlinePlayers())
	    bar.setMessageAndPercent(p, text, percent);

	plugin.onSecond();
    }

    private String timeString() {
	long hours = time / 3600L;
	long minutes = (time - hours * 3600L) / 60L;
	long seconds = time - hours * 3600L - minutes * 60L;
	return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private class SecondTask implements Runnable {
	@Override
	public void run() {
	    PhaseTimer.this.onSecond();
	}
    }
}

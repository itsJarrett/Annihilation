package net.coasterman10.Annihilation.util;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.scheduler.BukkitScheduler;

public class SchedulerUtil {
    private static Annihilation plugin;
    private static BukkitScheduler scheduler;
    
    public static void initialize(Annihilation plugin) {
	SchedulerUtil.plugin = plugin;
	SchedulerUtil.scheduler = plugin.getServer().getScheduler();
    }
    
    public static int run(Runnable task) {
	return scheduler.scheduleSyncDelayedTask(plugin, task);
    }
    
    public static int runDelayed(Runnable task, long delay) {
	return scheduler.scheduleSyncDelayedTask(plugin, task, delay);
    }
    
    public static int runRepeating(Runnable task, long delay, long period) {
	return scheduler.scheduleSyncRepeatingTask(plugin, task, delay, period);
    }
}

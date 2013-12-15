package net.coasterman10.Annihilation.listeners;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class WorldListener implements Listener {
    public WorldListener(Annihilation plugin) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent e) {
	if (!hasBedrock(e.getToBlock().getLocation()))
	    e.setCancelled(true);
    }
    
    private boolean hasBedrock(Location loc) {
	Location test = loc.clone();
	for (int y = 0; y < loc.getWorld().getMaxHeight(); y++) {
	    test.setY((double) y);
	    if (test.getBlock().getType().equals(Material.BEDROCK))
		return true;
	}
	return false;
    }
}

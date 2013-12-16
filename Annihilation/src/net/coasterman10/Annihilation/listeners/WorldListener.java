package net.coasterman10.Annihilation.listeners;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class WorldListener implements Listener {
    public WorldListener(Annihilation plugin) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent e) {
	if (isEmptyColumn(e.getToBlock().getLocation()))
	    e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
	if (isEmptyColumn(e.getBlock().getLocation()))
	    e.setCancelled(true);
    }

    private boolean isEmptyColumn(Location loc) {
	boolean hasBlock = false;
	Location test = loc.clone();
	for (int y = 0; y < loc.getWorld().getMaxHeight(); y++) {
	    test.setY(y);
	    if (test.getBlock().getType() != Material.AIR)
		hasBlock = true;
	}
	return !hasBlock;
    }
}

package net.coasterman10.Annihilation.listeners;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.maps.GameMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ResourceListener implements Listener {
    private static class Resource {
	public final Material drop;
	public final Integer xp;
	public final Integer delay;

	public Resource(Material drop, Integer xp, Integer delay) {
	    this.drop = drop;
	    this.xp = xp;
	    this.delay = delay;
	}
    }

    private final Annihilation plugin;
    private final HashSet<Location> queue = new HashSet<Location>();
    private final static HashMap<Material, Resource> resources = new HashMap<Material, Resource>();

    static {
	resources.put(Material.COAL_ORE, new Resource(Material.COAL, 8, 10));
	resources.put(Material.IRON_ORE,
		new Resource(Material.IRON_ORE, 10, 20));
	resources.put(Material.GOLD_ORE,
		new Resource(Material.GOLD_ORE, 10, 20));
	resources.put(Material.DIAMOND_ORE, new Resource(Material.DIAMOND, 12,
		30));
	resources.put(Material.EMERALD_ORE, new Resource(Material.EMERALD, 18,
		40));
	resources.put(Material.REDSTONE_ORE, new Resource(Material.REDSTONE,
		10, 20));
	resources.put(Material.GLOWING_REDSTONE_ORE, new Resource(
		Material.REDSTONE, 10, 20));
	resources.put(Material.LOG, new Resource(Material.LOG, 2, 10));
	resources.put(Material.GRAVEL, new Resource(null, 2, 20));
	resources
		.put(Material.MELON_BLOCK, new Resource(Material.MELON, 0, 10));
    }

    public ResourceListener(Annihilation plugin) {
	this.plugin = plugin;
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = false)
    public void onResourceBreak(BlockBreakEvent e) {
	final Material material = e.getBlock().getType();
	if (resources.containsKey(material)) {
	    Player player = e.getPlayer();
	    if (material.equals(Material.GRAVEL)) {
		Random rand = new Random();
		ItemStack arrows = new ItemStack(Material.ARROW, Math.max(
			rand.nextInt(5) - 2, 0));
		ItemStack flint = new ItemStack(Material.FLINT, Math.max(
			rand.nextInt(4) - 2, 0));
		ItemStack feathers = new ItemStack(Material.FEATHER, Math.max(
			rand.nextInt(4) - 2, 0));
		ItemStack string = new ItemStack(Material.STRING, Math.max(
			rand.nextInt(5) - 3, 0));
		ItemStack bones = new ItemStack(Material.BONE, Math.max(
			rand.nextInt(4) - 2, 0));
		ItemStack[] stacks = new ItemStack[] { arrows, flint, feathers,
			string, bones };
		for (ItemStack stack : stacks)
		    if (stack.getAmount() > 0)
			player.getInventory().addItem(stack);
	    } else {
		int qty = 1;
		if (material.equals(Material.REDSTONE_ORE)
			|| material.equals(Material.GLOWING_REDSTONE_ORE)) {
		    qty = 4 + (new Random().nextBoolean() ? 1 : 0);
		}
		if (material.equals(Material.MELON_BLOCK)) {
		    qty = 3 + new Random().nextInt(4);
		}
		player.getInventory().addItem(
			new ItemStack(resources.get(material).drop, qty));
	    }
	    e.setCancelled(true);
	    player.giveExp(resources.get(material).xp);
	    if (resources.get(material).xp > 0)
		player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F,
			(new Random().nextFloat() * 0.2F) + 0.9F);
	    queueRespawn(material, e.getBlock());
	} else if (queue.contains(e.getBlock().getLocation())) {
	    e.setCancelled(true);
	}
    }

    @EventHandler(ignoreCancelled = false)
    public void placeResource(BlockPlaceEvent e) {
	if (resources.containsKey(e.getBlock().getType())) {
	    e.setCancelled(true);
	}
    }

    public void queueDiamondSpawn() {
	GameMap map = plugin.getMapManager().getCurrentMap();
	if (map == null)
	    return;

	Set<Location> diamondLocations = map.getDiamondLocations();
	for (Location loc : diamondLocations) {
	    final Block block = loc.getBlock();
	    if (block.getType().equals(Material.DIAMOND_ORE)) {
		block.setType(Material.AIR);
		queue.add(block.getLocation());
		plugin.getServer().getScheduler()
			.runTaskLater(plugin, new Runnable() {
			    public void run() {
				block.setType(Material.DIAMOND_ORE);
				queue.remove(block.getLocation());
			    }
			}, 2 * plugin.getPhaseDelay() * 20L);
	    }
	}
    }

    private void queueRespawn(final Material material, final Block block) {
	if (material.equals(Material.LOG)
		|| material.equals(Material.MELON_BLOCK)) {
	    block.setType(Material.AIR);
	} else {
	    block.setType(Material.COBBLESTONE);
	}
	queue.add(block.getLocation());
	plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
	    @Override
	    public void run() {
		block.setType(material);
		queue.remove(block.getLocation());
	    }
	}, resources.get(material).delay * 20L);
    }
}

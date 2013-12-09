package net.coasterman10.Annihilation.chests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ChestLocker implements Listener, CommandExecutor {
    private final HashMap<Block, String> chests = new HashMap<Block, String>();
    private final HashSet<BlockFace> faces = new HashSet<BlockFace>();

    public ChestLocker(Annihilation plugin) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
	plugin.getCommand("unlock").setExecutor(this);
	faces.add(BlockFace.NORTH);
	faces.add(BlockFace.SOUTH);
	faces.add(BlockFace.EAST);
	faces.add(BlockFace.WEST);
    };

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
	Block block = e.getBlock();
	if (chests.containsKey(block)) {
	    String owner = chests.get(block);

	    // TODO Also make sure the player not the owner's enemy
	    if (owner != e.getPlayer().getName()) {
		e.setCancelled(true);
		e.getPlayer()
			.sendMessage(
				ChatColor.GOLD
					+ "You can't break this chest, it is locked by "
					+ owner + ".");
	    } else {
		chests.remove(block);
		e.getPlayer()
			.sendMessage(
				ChatColor.GOLD
					+ "You have broken your locked chest, you may now lock another chest.");
	    }
	}
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
	Block block = e.getBlock();
	Block below = block.getRelative(BlockFace.DOWN);
	if (chests.containsKey(below)) {
	    // TODO Also make sure the player is not the owner's enemy
	    if (chests.get(below) != e.getPlayer().getName()) {
		e.setCancelled(true);
		String owner = chests.get(below);
		e.getPlayer().sendMessage(
			ChatColor.GOLD + "You can't block " + owner
				+ "'s locked chest.");
		return;
	    }
	}

	if (block.getType() == Material.CHEST) {
	    for (BlockFace face : faces) {
		Block neighbor = block.getRelative(face);
		if (chests.containsKey(neighbor)
			&& block.getType() == Material.CHEST) {
		    e.setCancelled(true);
		    String owner = chests.get(neighbor);
		    if (e.getPlayer().getName() == owner)
			e.getPlayer()
				.sendMessage(
					ChatColor.GOLD
						+ "You can't convert your locked chest to a double chest.");
		    else
			e.getPlayer().sendMessage(
				ChatColor.GOLD + "You can't convert " + owner
					+ "'s locked chest to a double chest.");
		    return;
		}
	    }

	    if (getChest(e.getPlayer().getName()) != null) {
		Block chest = getChest(e.getPlayer().getName());
		String locText = "(" + chest.getX() + "," + chest.getY() + ","
			+ chest.getZ() + ")";
		e.getPlayer()
			.sendMessage(
				ChatColor.GOLD
					+ "You already have a locked chest at "
					+ locText
					+ ", this chest will not be locked. You can unlock it with /unlock.");
	    } else {
		chests.put(block, e.getPlayer().getName());
		e.getPlayer()
			.sendMessage(
				ChatColor.GOLD
					+ "You have created a locked chest. If you would like your teammates to be able to access it, unlock it with /unlock.");
	    }
	}
    }

    public void onChestOpen(InventoryOpenEvent e) {
	if (e.getInventory().getHolder() instanceof Chest) {
	    Block chest = (Block) e.getInventory().getHolder();
	    String owner = chests.get(chest);

	    // TODO Also check that the player trying to open the chest is not
	    // the owner's enemy
	    if (owner != e.getPlayer().getName()) {
		e.setCancelled(true);
		((Player) e.getPlayer()).sendMessage(ChatColor.GOLD
			+ "You can't open this chest, it is locked by " + owner
			+ ".");
	    }
	}
    }

    private Block getChest(String name) {
	for (Entry<Block, String> e : chests.entrySet()) {
	    if (e.getValue() == name)
		return e.getKey();
	}
	return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
	    String[] args) {
	if (sender instanceof Player) {
	    Block chest = getChest(sender.getName());
	    if (chest != null) {
		chests.remove(chest);
		sender.sendMessage(ChatColor.GOLD
			+ "You have unlocked your chest, teammates can now acces it and you may place another locked chest.");
	    } else {
		sender.sendMessage(ChatColor.GOLD
			+ "You don't have any locked chests to unlock.");
	    }
	} else {
	    sender.sendMessage(ChatColor.GOLD
		    + "Only players can unlock chests.");
	}
	return true;
    }
}

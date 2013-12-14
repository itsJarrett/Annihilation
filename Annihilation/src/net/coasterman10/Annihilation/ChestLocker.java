package net.coasterman10.Annihilation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import net.coasterman10.Annihilation.commands.UnlockCommand;
import net.coasterman10.Annihilation.teams.TeamManager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ChestLocker implements Listener {
    private final TeamManager teamManager;
    private final HashMap<Block, String> chests = new HashMap<Block, String>();
    private final static HashSet<BlockFace> faces = new HashSet<BlockFace>();

    static {
	faces.add(BlockFace.NORTH);
	faces.add(BlockFace.SOUTH);
	faces.add(BlockFace.EAST);
	faces.add(BlockFace.WEST);
    };

    public ChestLocker(Annihilation plugin) {
	plugin.getServer().getPluginManager().registerEvents(this, plugin);
	plugin.getCommand("unlock").setExecutor(new UnlockCommand(this));
	teamManager = plugin.getTeamManager();
    };

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
	Block block = e.getBlock();
	Player player = e.getPlayer();
	if (chests.containsKey(block)) {
	    String owner = chests.get(block);
	    if (!teamManager.areFriendly(owner, player.getName()))
		return;
	    if (owner != player.getName()) {
		e.setCancelled(true);
		player.sendMessage(ChatColor.GOLD
			+ "You can't break this chest, it is locked by "
			+ owner + ".");
	    } else {
		chests.remove(block);
		player.sendMessage(ChatColor.GOLD
			+ "You have broken your locked chest, you may now lock another chest.");
	    }
	}
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
	Player player = e.getPlayer();
	Block block = e.getBlock();
	Block below = block.getRelative(BlockFace.DOWN);

	if (chests.containsKey(below)) {
	    if (chests.get(below) != player.getName()
		    && !teamManager.areFriendly(chests.get(below),
			    player.getName())) {
		e.setCancelled(true);
		String owner = chests.get(below);
		player.sendMessage(ChatColor.GOLD + "You can't block " + owner
			+ "'s locked chest.");
		return;
	    }
	}

	if (block.getType() == Material.CHEST) {
	    Block neighbor = getNeighboringChest(block);
	    if (neighbor != null) {
		e.setCancelled(true);
		String owner = chests.get(neighbor);
		if (player.getName() == owner)
		    player.sendMessage(ChatColor.GOLD
			    + "You can't convert your locked chest to a double chest.");
		else
		    player.sendMessage(ChatColor.GOLD + "You can't convert "
			    + owner + "'s locked chest to a double chest.");
		return;
	    }
	    createChest(block, player);
	}
    }

    @EventHandler(ignoreCancelled = true)
    public void onChestOpen(InventoryOpenEvent e) {
	if (e.getInventory().getHolder() instanceof Chest) {
	    Block chest = (Block) e.getInventory().getHolder();
	    Player player = (Player) e.getPlayer();
	    String owner = chests.get(chest);
	    if (!teamManager.areFriendly(owner, player.getName()))
		return;
	    if (owner != player.getName()) {
		e.setCancelled(true);
		(player).sendMessage(ChatColor.GOLD
			+ "You can't open this chest, it is locked by " + owner
			+ ".");
	    }
	}
    }

    public Block getChest(String name) {
	for (Entry<Block, String> e : chests.entrySet()) {
	    if (e.getValue() == name)
		return e.getKey();
	}
	return null;
    }

    public void createChest(Block chest, Player player) {
	if (!chests.containsKey(chest)) {
	    chests.put(chest, player.getName());
	    player.sendMessage(ChatColor.GOLD
		    + "You have created a locked chest. Teammates may not access it.");
	} else {
	    Block existing = getChest(player.getName());
	    player.sendMessage(ChatColor.GOLD
		    + "You already have a locked chest at "
		    + getCoordText(existing)
		    + ", this chest will not be locked.");
	}
    }

    public void unlockChest(Block chest, Player player) {
	if (chests.get(chest) != null) {
	    chests.remove(chest);
	    player.sendMessage(ChatColor.GOLD
		    + "You have unlocked your chest at " + getCoordText(chest)
		    + ". Teammates can now access it.");
	} else {
	    player.sendMessage(ChatColor.GOLD
		    + "You don't have any chests to unlock.");
	}
    }

    private String getCoordText(Block block) {
	if (block != null) {
	    int x = block.getX();
	    int y = block.getY();
	    int z = block.getZ();
	    return String.format("(%d,%d,%d)", x, y, z);
	} else
	    return null;
    }

    private Block getNeighboringChest(Block block) {
	for (BlockFace face : faces)
	    if (chests.containsKey(block.getRelative(face)))
		return block.getRelative(face);
	return null;
    }
}

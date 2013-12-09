package net.coasterman10.Annihilation.commands;

import net.coasterman10.Annihilation.chests.ChestLocker;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnlockCommand implements CommandExecutor {
    private final ChestLocker cl;

    public UnlockCommand(ChestLocker cl) {
	this.cl = cl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
	    String[] args) {
	if (sender instanceof Player) {
	    Block chest = cl.getChest(sender.getName());
	    cl.unlockChest(chest, (Player) sender);
	} else {
	    sender.sendMessage(ChatColor.GOLD
		    + "Only players can unlock chests.");
	}
	return true;
    }
}

package net.coasterman10.Annihilation.commands;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AnnihilationCommand implements CommandExecutor {
    private Annihilation plugin;

    public AnnihilationCommand(Annihilation plugin) {
	this.plugin = plugin;
	plugin.getCommand("annihilation").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
	    String[] args) {
	if (args.length == 1) {
	    if (args[0].equalsIgnoreCase("start")) {
		if (!plugin.startTimer()) {
		    sender.sendMessage(ChatColor.RED
			    + "The game has already started");
		}
	    }
	}
	return false;
    }
}

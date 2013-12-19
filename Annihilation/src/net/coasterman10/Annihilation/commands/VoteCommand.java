package net.coasterman10.Annihilation.commands;

import net.coasterman10.Annihilation.maps.VotingManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class VoteCommand implements CommandExecutor {
    private final VotingManager manager;

    public VoteCommand(VotingManager manager) {
	this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
	    String[] args) {
	if (args.length == 0)
	    listMaps(sender);
	else if (!manager.vote(sender, args[0]))
		listMaps(sender);
	return true;
    }

    private void listMaps(CommandSender sender) {
	sender.sendMessage("§bMaps up for voting:");
	for (String map : manager.getMaps())
	    sender.sendMessage(" §8- §7" + map);
    }
}

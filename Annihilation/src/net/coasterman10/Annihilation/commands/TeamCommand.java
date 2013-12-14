package net.coasterman10.Annihilation.commands;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.teams.Team;
import net.coasterman10.Annihilation.teams.TeamManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {
    private TeamManager teamManager;

    public TeamCommand(Annihilation plugin, TeamManager teamManager) {
	plugin.getCommand("team").setExecutor(this);
	this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label,
	    String[] args) {
	if (args.length == 0)
	    listTeams(sender);
	else {
	    if (!(sender instanceof Player)) {
		sender.sendMessage("Only players can join teams");
	    } else {
		joinTeam((Player) sender, args[0]);
	    }
	}
	return true;
    }

    private void joinTeam(Player player, String team) {
	if (!teamManager.inLobby(player.getName())) {
	    Team currentTeam = teamManager.getTeamWithPlayer(player.getName());
	    player.sendMessage(ChatColor.RED + "You are already on "
		    + currentTeam.getFullName() + "!");
	    return;
	}

	Team target = teamManager.getTeam(team);
	if (target == null) {
	    player.sendMessage(ChatColor.RED + "\"" + team
		    + "\" is not a valid team name!");
	    listTeams(player);
	    return;
	}

	target.addPlayer(player.getName());
	player.sendMessage(ChatColor.DARK_AQUA + "You joined "
		+ target.getFullName());
    }

    private void listTeams(CommandSender sender) {
	sender.sendMessage(ChatColor.GRAY + "============[ "
		+ ChatColor.DARK_AQUA + "Teams" + ChatColor.GRAY
		+ " ]============");
	for (Team t : teamManager.getTeams()) {
	    if (t.getSize() != 1) {
		sender.sendMessage(t.getFullName() + " - " + t.getSize()
			+ " players");
	    } else {
		sender.sendMessage(t.getFullName() + " - " + t.getSize()
			+ " player");
	    }
	}
	sender.sendMessage(ChatColor.GRAY + "===============================");
    }
}

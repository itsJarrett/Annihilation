package net.coasterman10.Annihilation.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.commands.TeamCommand;

public class TeamManager {
    private final List<Team> teams = new ArrayList<Team>();
    private final Team lobby = new Team("lobby",
	    ChatColor.DARK_PURPLE.toString());

    public TeamManager(Annihilation plugin) {
	new TeamCommand(plugin, this);
	teams.add(new Team("red", ChatColor.RED.toString()));
	teams.add(new Team("blue", ChatColor.BLUE.toString()));
	teams.add(new Team("green", ChatColor.GREEN.toString()));
	teams.add(new Team("yellow", ChatColor.YELLOW.toString()));
    }

    public boolean areFriendly(String p1, String p2) {
	if (inLobby(p1) || inLobby(p2))
	    return false;
	return getTeamWithPlayer(p1) == getTeamWithPlayer(p2);
    }

    public boolean inLobby(String player) {
	return getTeamWithPlayer(player) == lobby;
    }

    public Team getTeamWithPlayer(String player) {
	for (Team t : teams) {
	    if (t.hasPlayer(player))
		return t;
	}
	return lobby;
    }

    public Team getTeam(String name) {
	for (Team t : teams) {
	    if (t.getName().equalsIgnoreCase(name))
		return t;
	}
	if (name.equalsIgnoreCase("lobby"))
	    return lobby;
	return null;
    }

    public List<Team> getTeams() {
	return teams;
    }
}

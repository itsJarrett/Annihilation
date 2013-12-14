package net.coasterman10.Annihilation.teams;

import java.util.ArrayList;
import java.util.List;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.commands.TeamCommand;

public class TeamManager {
    private final List<Team> teams = new ArrayList<Team>();
    private final Team lobby = new Team("lobby");

    public TeamManager(Annihilation plugin) {
	new TeamCommand(plugin, this);
	teams.add(new Team("red"));
	teams.add(new Team("blue"));
	teams.add(new Team("green"));
	teams.add(new Team("yellow"));
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

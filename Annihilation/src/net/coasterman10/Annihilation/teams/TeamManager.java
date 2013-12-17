package net.coasterman10.Annihilation.teams;

import java.util.ArrayList;
import java.util.List;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.commands.TeamCommand;

public class TeamManager {
	private final List<Team> teams = new ArrayList<Team>();

	public TeamManager(Annihilation plugin) {
		new TeamCommand(plugin, this);
		teams.add(new Team(TeamName.RED));
		teams.add(new Team(TeamName.YELLOW));
		teams.add(new Team(TeamName.GREEN));
		teams.add(new Team(TeamName.BLUE));
	}

	public boolean areFriendly(String p1, String p2) {
		if (inLobby(p1) || inLobby(p2))
			return false;
		return getTeamWithPlayer(p1) == getTeamWithPlayer(p2);
	}

	public boolean inLobby(String player) {
		return getTeamWithPlayer(player) != null;
	}

	public Team getTeamWithPlayer(String player) {
		for (Team t : teams) {
			if (t.hasPlayer(player))
				return t;
		}
		return null;
	}

	public Team getTeam(TeamName name) {
		for (Team t : teams) {
			if (t.getName() == name)
				return t;
		}
		return null;
	}

	public List<Team> getTeams() {
		return teams;
	}
}

package net.coasterman10.Annihilation.util;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class TeamUtil {
    private static final Scoreboard board = Bukkit.getScoreboardManager()
	    .getNewScoreboard();
    private static final HashSet<String> teams = new HashSet<String>();

    public static void registerTeam(String team, String prefix) {
	if (!teams.contains(team)) {
	    teams.add(team);
	    Team t = board.registerNewTeam(team);
	    t.setAllowFriendlyFire(false);
	    t.setPrefix(prefix);
	    t.setCanSeeFriendlyInvisibles(true);
	}
    }

    public static void addPlayerToTeam(String username, String team) {
	if (teams.contains(team)) {
	    board.getTeam(team).addPlayer(Bukkit.getOfflinePlayer(username));
	}
    }

    public static int getPlayerCount(String team) {
	if (teams.contains(team))
	    return board.getTeam(team).getPlayers().size();
	else
	    return 0;
    }

    public static String getTeamWithPlayer(String username) {
	for (String team : teams) {
	    if (board.getTeam(team).getPlayers()
		    .contains(Bukkit.getOfflinePlayer(username)))
		return team;
	}
	return null;
    }

    public static boolean onSameTeam(String username1, String username2) {
	return getTeamWithPlayer(username1) == getTeamWithPlayer(username2);
    }
}

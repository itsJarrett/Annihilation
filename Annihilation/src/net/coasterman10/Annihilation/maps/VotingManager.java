package net.coasterman10.Annihilation.maps;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.coasterman10.Annihilation.Annihilation;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class VotingManager {
    private final Scoreboard board;
    private final HashMap<String, Integer> votes = new HashMap<String, Integer>();
    private int time = 120;
    private int taskID;

    public VotingManager(MapManager mapManager) {
	board = Bukkit.getScoreboardManager().getNewScoreboard();
	Objective o = board.registerNewObjective("votes", "dummy");
	o.setDisplaySlot(DisplaySlot.SIDEBAR);
	for (GameMap map : mapManager.getRandomMaps()) {
	    votes.put(map.getName(), 0);
	    updateVotes(map.getName());
	}
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.setScoreboard(board);
	}
    }

    public void setCurrentForPlayers(Player... players) {
	for (Player p : players)
	    p.setScoreboard(board);
    }

    public void startTimer(final Annihilation plugin) {
	final Objective o = board.getObjective("votes");
	final String prefix = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD;
	o.setDisplaySlot(DisplaySlot.SIDEBAR);
	o.setDisplayName(prefix + "Starting in " + time);
	taskID = plugin.getServer().getScheduler()
		.scheduleSyncRepeatingTask(plugin, new Runnable() {
		    public void run() {
			time--;
			o.setDisplayName(prefix + "Starting in " + time);
			if (time == 0) {
			    plugin.startGame();
			    plugin.getServer().getScheduler()
				    .cancelTask(taskID);
			}
		    }
		}, 20L, 20L);
    }

    public void updateVotes(String map) {
	if (votes.containsKey(map)) {
	    Score s = board.getObjective("votes").getScore(
		    Bukkit.getOfflinePlayer(WordUtils.capitalize(map)));
	    s.setScore(votes.get(map));
	}
    }

    public boolean vote(String map) {
	if (votes.containsKey(map)) {
	    votes.put(map, votes.get(map) + 1);
	    updateVotes(map);
	    return true;
	}
	return false;
    }

    public boolean unvote(String map) {
	if (votes.containsKey(map)) {
	    votes.put(map, votes.get(map) + 1);
	    updateVotes(map);
	    return true;
	}
	return false;
    }

    public String getWinner() {
	String winner = null;
	Integer highest = 0;
	for (Entry<String, Integer> e : votes.entrySet()) {
	    if (e.getValue() > highest) {
		winner = e.getKey();
		highest = e.getValue();
	    }
	    if (e.getValue() == highest && e.getValue() > 0) {
		return (new Random().nextBoolean()) ? winner : e.getKey();
	    }
	}
	return winner;
    }
}

package net.coasterman10.Annihilation.maps;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.coasterman10.Annihilation.ScoreboardUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class VotingManager {
    private final String boardName = "voting";
    private final HashMap<String, Integer> votes = new HashMap<String, Integer>();

    public VotingManager(MapManager mapManager) {
	for (GameMap map : mapManager.getRandomMaps()) {
	    votes.put(map.getName(), 0);
	    updateVotes(map.getName());
	}
	ScoreboardUtil.setTitle(boardName, ChatColor.DARK_AQUA + ""
		+ ChatColor.BOLD + "Voting");
	setCurrentForPlayers(Bukkit.getOnlinePlayers());
    }

    public void setCurrentForPlayers(Player... players) {
	for (Player p : players)
	    ScoreboardUtil.setBoard(p, boardName);
    }

    public void updateVotes(String map) {
	if (votes.containsKey(map)) {
	    ScoreboardUtil.setScore(boardName, map, votes.get(map));
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
	    votes.put(map, votes.get(map) - 1);
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
    
    public void end() {
	ScoreboardUtil.clear(boardName);
    }
}

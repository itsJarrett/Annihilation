package net.coasterman10.Annihilation.maps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.commands.VoteCommand;
import net.coasterman10.Annihilation.util.ScoreboardUtil;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VotingManager {
    private final String boardName = "voting";
    private final HashSet<String> maps = new HashSet<String>();
    private final HashMap<String, String> votes = new HashMap<String, String>();

    public VotingManager(Annihilation plugin, MapManager mapManager) {
	plugin.getCommand("vote").setExecutor(new VoteCommand(this));

	ScoreboardUtil.registerBoard(boardName);
	ScoreboardUtil.setTitle(boardName, ChatColor.DARK_AQUA + ""
		+ ChatColor.BOLD + "Voting");
	for (GameMap map : mapManager.getRandomMaps()) {
	    maps.add(map.getName());
	    updateVotes(map.getName());
	}
    }

    public void setCurrentForPlayers(Player... players) {
	for (Player p : players)
	    ScoreboardUtil.setBoard(p, boardName);
    }

    public boolean vote(CommandSender voter, String vote) {
	for (String map : maps) {
	    if (vote.equalsIgnoreCase(map)) {
		votes.put(voter.getName(), map);
		updateVotes(map);
		voter.sendMessage(ChatColor.GOLD + "You voted for "
			+ ChatColor.WHITE + map);
		return true;
	    }
	}
	voter.sendMessage(vote + ChatColor.RED + " is not a valid map");
	return false;
    }

    public String getWinner() {
	String winner = null;
	Integer highest = -1;
	for (String map : maps) {
	    int totalVotes = countVotes(map);
	    if (totalVotes > highest) {
		winner = map;
		highest = totalVotes;
	    }
	}
	return winner;
    }

    public void end() {
	ScoreboardUtil.clear(boardName);
    }

    public Set<String> getMaps() {
	return maps;
    }

    private void updateVotes(String map) {
	int totalVotes = countVotes(map);
	ScoreboardUtil.setScore(boardName, map, totalVotes);
    }

    private int countVotes(String map) {
	int total = 0;
	for (String vote : votes.values())
	    if (vote.equals(map))
		total++;
	return total;
    }
}

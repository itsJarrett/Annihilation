package net.coasterman10.Annihilation.maps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.StatBoard;
import net.coasterman10.Annihilation.commands.VoteCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class VotingManager implements Listener {
    private final StatBoard statBoard;
    private final HashSet<String> maps = new HashSet<String>();
    private final HashMap<String, String> votes = new HashMap<String, String>();

    public VotingManager(Annihilation plugin, MapManager mapManager) {
	plugin.getCommand("vote").setExecutor(new VoteCommand(this));

	String title = ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Voting";
	statBoard = new StatBoard(plugin.getServer().getScoreboardManager());
	statBoard.setTitle(title);

	for (GameMap map : mapManager.getRandomMaps()) {
	    maps.add(map.getName());
	    statBoard.setScore(map.getName(), 0);
	}
    }
    
    public void setCurrentForPlayers(Player... players) {
	statBoard.showForPlayers(players);
    }

    public boolean vote(CommandSender voter, String vote) {
	for (String map : maps) {
	    if (vote.equalsIgnoreCase(map)) {
		votes.put(voter.getName(), map);
		statBoard.setScore(map, countVotes(map));
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
	statBoard.hide();
    }

    public Set<String> getMaps() {
	return maps;
    }

    private int countVotes(String map) {
	int total = 0;
	for (String vote : votes.values())
	    if (vote.equals(map))
		total++;
	return total;
    }
}

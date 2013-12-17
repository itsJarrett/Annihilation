package net.coasterman10.Annihilation.teams;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Team {
	private final TeamName name;
	private final Set<String> playerNames = new HashSet<String>();
	private boolean alive;

	public Team(TeamName name) {
		this.name = name;
		alive = true;
	}

	public void addPlayer(String name) {
		playerNames.add(name);
	}

	public void message(String message) {
		for (Player p : getPlayers())
			p.sendMessage(message);
	}

	public int getSize() {
		return playerNames.size();
	}

	public Set<String> getPlayerNames() {
		return playerNames;
	}

	public Set<Player> getPlayers() {
		Set<Player> players = new HashSet<Player>();
		for (String name : playerNames)
			players.add(Bukkit.getPlayer(name));
		return players;
	}

	public boolean hasPlayer(String name) {
		return playerNames.contains(name);
	}

	public TeamName getName() {
		return name;
	}

	public String getPrefix() {
		return ChatColor.valueOf(name.name()).toString();
	}

	public String getFullName() {
		return getPrefix() + WordUtils.capitalize(name.toString()) + " Team";
	}

	public boolean isAlive() {
		return alive;
	}
}

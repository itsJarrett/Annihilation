package net.coasterman10.Annihilation.teams;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Team {
    private final String name;
    private final String prefix;
    private final Set<String> playerNames = new HashSet<String>();
    private boolean alive;

    public Team(String name, String prefix) {
	this.name = name;
	this.prefix = prefix;
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

    public String getName() {
	return name;
    }

    public String getPrefix() {
	return prefix;
    }
    
    public String getFullName() {
	return prefix + WordUtils.capitalize(name) + " Team";
    }

    public boolean isAlive() {
	return alive;
    }
}

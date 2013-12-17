package net.coasterman10.Annihilation.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.coasterman10.Annihilation.Annihilation;
import net.coasterman10.Annihilation.teams.TeamName;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.Configuration;

public class MapManager {
	private final ArrayList<String> maps = new ArrayList<String>();
	private GameMap currentMap = null;
	private Location lobbySpawn;
	private MapLoader mapLoader;
	private Configuration config;

	public MapManager(Annihilation plugin, Configuration config) {
		mapLoader = new MapLoader(plugin.getLogger(), plugin.getDataFolder());
		this.config = config;

		for (String s : config.getKeys(false)) {
			if (!s.equalsIgnoreCase("lobby"))
				maps.add(s);
		}

		WorldCreator wc = new WorldCreator("lobby");
		wc.generator(new VoidGenerator());
		Bukkit.createWorld(wc);

		lobbySpawn = parseLocation(config.getString("lobby.spawn"));
	}

	private Location parseLocation(String in) {
		String[] params = in.split(",");
		if (params.length == 3 || params.length == 5) {
			double x = Double.parseDouble(params[0]);
			double y = Double.parseDouble(params[1]);
			double z = Double.parseDouble(params[2]);
			Location loc = new Location(Bukkit.getWorld("lobby"), x, y, z);
			if (params.length == 5) {
				loc.setYaw(Float.parseFloat(params[3]));
				loc.setPitch(Float.parseFloat(params[4]));
			}
			return loc;
		}
		return null;
	}

	public boolean selectMap(String mapName) {
		currentMap = new GameMap(mapLoader,
				config.getConfigurationSection(mapName));
		return currentMap.loadIntoGame(mapName);
	}

	public boolean mapSelected() {
		return currentMap != null;
	}

	public GameMap getCurrentMap() {
		return currentMap;
	}

	public Location getSpawnPoint(TeamName teamName) {
		return getCurrentMap().getSpawnPoint(teamName);
	}

	public Location getNexus(TeamName team) {
		return currentMap.getNexusLocation(team);
	}

	public Location getLobbySpawnPoint() {
		return lobbySpawn;
	}

	public List<String> getRandomMaps() {
		LinkedList<String> shuffledMaps = new LinkedList<String>(maps);
		Collections.shuffle(shuffledMaps);
		return shuffledMaps.subList(0, Math.min(3, shuffledMaps.size()));
	}
}

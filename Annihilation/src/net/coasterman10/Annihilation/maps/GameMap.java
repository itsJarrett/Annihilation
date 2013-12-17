package net.coasterman10.Annihilation.maps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.coasterman10.Annihilation.maps.MapLoader;
import net.coasterman10.Annihilation.teams.TeamName;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;

public class GameMap {
	private World world;
	private Map<TeamName, List<Location>> spawns;
	private Map<TeamName, Location> nexuses;
	private Set<Location> diamonds;
	private MapLoader mapLoader;
	private ConfigurationSection config;

	public GameMap(MapLoader mapLoader, ConfigurationSection config) {
		spawns = new EnumMap<TeamName, List<Location>>(TeamName.class);
		nexuses = new EnumMap<TeamName, Location>(TeamName.class);
		this.mapLoader = mapLoader;
		this.config = config;
	}

	public boolean loadIntoGame(String worldName) {
		if (config == null)
			return false;

		mapLoader.loadMap(worldName);

		WorldCreator wc = new WorldCreator(worldName);
		wc.generator(new VoidGenerator());
		world = Bukkit.createWorld(wc);

		if (!loadConfig())
			return false;

		for (TeamName name : TeamName.values())
			if (!spawns.containsKey(name) || !nexuses.containsKey(name))
				return false;

		return true;
	}

	private boolean loadConfig() {
		ConfigurationSection spawns = config.getConfigurationSection("spawns");
		ConfigurationSection nexuses = config
				.getConfigurationSection("nexuses");
		if (spawns == null || nexuses == null)
			return false;
		if (!loadSpawns() || !loadNexuses())
			return false;

		loadDiamondLocations();

		return true;
	}

	private boolean loadSpawns() {
		ConfigurationSection spawnConfig = config
				.getConfigurationSection("spawns");
		if (spawnConfig == null)
			return false;

		for (String key : spawnConfig.getKeys(false)) {
			List<String> spawnStrings = spawnConfig.getStringList(key);
			List<Location> spawnLocations = new ArrayList<Location>();
			for (String spawn : spawnStrings) {
				Location loc = parseLocation(spawn);
				if (loc != null)
					spawnLocations.add(loc);
			}
			if (spawnLocations.isEmpty())
				return false;
			if (TeamName.valueOf(key.toUpperCase()) != null)
				spawns.put(TeamName.valueOf(key.toUpperCase()), spawnLocations);
		}
		return true;
	}

	private boolean loadNexuses() {
		ConfigurationSection nexusConfig = config
				.getConfigurationSection("nexuses");
		if (nexusConfig == null)
			return false;

		for (String key : nexusConfig.getKeys(false)) {
			if (nexusConfig.contains(key)) {
				Location loc = parseLocation(nexusConfig.getString(key));
				if (loc != null)
					if (TeamName.valueOf(key.toUpperCase()) != null)
						nexuses.put(TeamName.valueOf(key.toUpperCase()), loc);
					else
						return false;
			} else
				return false;
		}
		return true;
	}

	private void loadDiamondLocations() {
		if (diamonds == null)
			diamonds = new HashSet<Location>();

		for (String s : config.getStringList("diamonds")) {
			Location loc = parseLocation(s);
			if (loc != null)
				diamonds.add(loc);
		}
	}

	public Location getSpawnPoint(TeamName teamName) {
		List<Location> spawnList = new ArrayList<Location>(spawns.get(teamName));
		Collections.shuffle(spawnList);
		return spawnList.get(0);
	}

	public Location getNexusLocation(TeamName team) {
		return nexuses.get(team);
	}

	public String getName() {
		return world.getName();
	}

	public World getWorld() {
		return world;
	}

	private Location parseLocation(String in) {
		String[] params = in.split(",");
		if (params.length == 3 || params.length == 5) {
			double x = Double.parseDouble(params[0]);
			double y = Double.parseDouble(params[1]);
			double z = Double.parseDouble(params[2]);
			Location loc = new Location(world, x, y, z);
			if (params.length == 5) {
				loc.setYaw(Float.parseFloat(params[4]));
				loc.setPitch(Float.parseFloat(params[5]));
			}
			return loc;
		}
		return null;
	}

	public Set<Location> getDiamondLocations() {
		return diamonds;
	}
}

package net.coasterman10.Annihilation.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.coasterman10.Annihilation.maps.MapLoader;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;

public class GameMap {
    private World world;
    private Map<String, List<Location>> spawns;
    private Map<String, Location> nexuses;
    private Set<Location> diamonds;
    private MapLoader mapLoader;
    private ConfigurationSection config;

    private static final String[] teams = { "red", "yellow", "green", "blue" };

    public GameMap(MapLoader mapLoader) {
	spawns = new HashMap<String, List<Location>>();
	nexuses = new HashMap<String, Location>();
	this.mapLoader = mapLoader;
    }

    public boolean loadIntoGame(ConfigurationSection config, String worldName) {
	if (config == null)
	    return false;
	
	mapLoader.loadMap(worldName);
	
	WorldCreator wc = new WorldCreator(worldName);
	wc.generator(new VoidGenerator());
	world = Bukkit.createWorld(wc);
	
	if (!loadConfig())
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
	ConfigurationSection spawnConfig = config.getConfigurationSection("spawns");
	if (spawnConfig == null)
	    return false;
	
	for (String team : teams) {
	    if (spawnConfig.contains(team)) {
		List<String> spawnStrings = spawnConfig.getStringList(team);
		List<Location> spawnLocations = new ArrayList<Location>();
		for (String spawn : spawnStrings) {
		    Location loc = parseLocation(spawn);
		    if (loc != null)
			spawnLocations.add(loc);
		}
		if (spawnLocations.isEmpty())
		    return false;
		spawns.put(team, spawnLocations);
	    } else
		return false;
	}
	return true;
    }

    private boolean loadNexuses() {
	ConfigurationSection nexusConfig = config.getConfigurationSection("nexuses");
	if (nexusConfig == null)
	    return false;
	
	for (String team : teams) {
	    if (nexusConfig.contains(team)) {
		Location loc = parseLocation(nexusConfig.getString(team));
		if (loc != null)
		    nexuses.put(team, loc);
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

    public Location getSpawnPoint(String team) {
	List<Location> spawnList = spawns.get(team);
	return spawnList.get(new Random().nextInt(spawnList.size() - 1));
    }

    public Location getNexusLocation(String team) {
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

package net.coasterman10.Annihilation.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.coasterman10.Annihilation.BadConfigException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;

public class GameMap {
    private final String name;
    private final World world;
    private final Map<String, List<Location>> spawnPoints = new HashMap<String, List<Location>>();
    private final Map<String, Location> nexusLocations = new HashMap<String, Location>();

    public GameMap(ConfigurationSection config) throws BadConfigException {
	name = config.getName();

	WorldCreator wc = new WorldCreator(name);
	wc.generator(new VoidGenerator());
	world = Bukkit.createWorld(wc);

	ConfigurationSection spawnConfig = config
		.getConfigurationSection("spawns");
	ConfigurationSection nexusConfig = config
		.getConfigurationSection("nexuses");

	if (spawnConfig == null)
	    throw new BadConfigException(
		    "Missing configuration section for spawns");
	if (nexusConfig == null)
	    throw new BadConfigException(
		    "Missing configuration section for nexuses");

	for (String team : Arrays.asList("red", "yellow", "green", "blue")) {
	    if (!spawnConfig.contains(team))
		throw new BadConfigException("Missing spawn points for " + team
			+ " team");
	    if (!nexusConfig.contains(team))
		throw new BadConfigException("Missing nexus data for " + team
			+ " team");

	    List<Location> spawns = new ArrayList<Location>();
	    for (String spawn : spawnConfig.getStringList(team)) {
		Location loc = parseSpawnLocation(spawn);
		if (loc != null)
		    spawns.add(loc);
	    }
	    if (!spawns.isEmpty())
		spawnPoints.put(team, spawns);
	    else
		throw new BadConfigException("Invalid configuration for "
			+ team + " spawns");

	    Location nexus = parseLocation(nexusConfig.getString(team));
	    if (nexus != null)
		nexusLocations.put(team, nexus);
	    else
		throw new BadConfigException("Invalid configuration for "
			+ team + " nexus");
	}
    }

    public Location getSpawnPoint(String team) {
	List<Location> spawns = spawnPoints.get(team);
	if (spawns == null)
	    return world.getSpawnLocation();
	else {
	    Location loc = spawns.get(new Random().nextInt(spawns.size()));
	    loc.setY(world.getHighestBlockYAt(loc));
	    return loc;
	}
    }

    public Location getNexusLocation(String team) {
	return nexusLocations.get(team);
    }

    public String getName() {
	return world.getName();
    }

    private Location parseLocation(String s) {
	String[] params = s.split(",");
	if (params.length >= 3) {
	    double x = Double.valueOf(params[0]);
	    double y = Double.valueOf(params[1]);
	    double z = Double.valueOf(params[2]);
	    return new Location(world, x, y, z);
	} else
	    return null;
    }

    private Location parseSpawnLocation(String s) {
	Location loc = parseLocation(s);
	if (loc != null) {
	    String[] params = s.split(",");
	    if (params.length >= 5) {
		loc.setYaw(Float.valueOf(params[3]));
		loc.setPitch(Float.valueOf(params[4]));
	    }
	    return loc;
	} else
	    return null;
    }
}
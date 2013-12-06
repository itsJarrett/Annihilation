package net.coasterman10.Annihilation.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.coasterman10.Annihilation.BadConfigException;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class MapManager {
    private final Logger log;
    private final Map<String, GameMap> maps = new HashMap<String, GameMap>();
    private String currentMap;

    public MapManager(Logger log, ConfigurationSection config) {
	this.log = log;
	for (String name : config.getKeys(false)) {
	    try
	    {
		GameMap map = new GameMap(config.getConfigurationSection(name));
		maps.put(name, map);
		    log.info("Added map " + name);
	    }
	    catch (BadConfigException e)
	    {
		log.warning("Could not add map " + name + " due to bad configuration");
		e.printStackTrace();
	    }
	}
    }

    public boolean selectMap(String mapName) {
	if (maps.containsKey(mapName)) {
	    currentMap = mapName;
	    log.info("Selected map " + mapName);
	    return true;
	} else {
	    log.severe("Unable to select map" + mapName);
	    return false;
	}
    }

    public boolean mapSelected() {
	return currentMap != null;
    }

    public GameMap getCurrentMap() {
	return maps.get(currentMap);
    }

    public Location getSpawnPoint(String team) {
	return getCurrentMap().getSpawnPoint(team);
    }

    public Location getNexus(String team) {
	return maps.get(currentMap).getNexusLocation(team);
    }
}

package net.coasterman10.Annihilation.maps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;

public class LobbyMap {
    private final String name;
    private final World world;
    private final Location spawn;

    public LobbyMap(ConfigurationSection config) {
	name = config.getName();

	WorldCreator wc = new WorldCreator(name);
	wc.generator(new VoidGenerator());
	world = Bukkit.createWorld(wc);
	
	spawn = parseLocation(config.getString("spawn"));
    }

    public Location getSpawnPoint() {
	return spawn;
    }

    public String getName() {
	return world.getName();
    }
    
    private Location parseLocation(String s) {
	String[] params = s.split(",");
	Location loc = null;
	if (params.length >= 3) {
	    double x = Double.valueOf(params[0]);
	    double y = Double.valueOf(params[1]);
	    double z = Double.valueOf(params[2]);
	    loc = new Location(world, x, y, z);
	    if (params.length >= 5) {
		loc.setYaw(Float.valueOf(params[3]));
		loc.setPitch(Float.valueOf(params[4]));
	    }
	}
	return loc;
    }
}

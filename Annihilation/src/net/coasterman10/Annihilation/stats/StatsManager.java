package net.coasterman10.Annihilation.stats;

import java.io.IOException;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class StatsManager {
	
	private Annihilation plugin;
	private YamlConfiguration yml;
	
	public StatsManager(Annihilation instance) {
		this.plugin = instance;
		yml = plugin.getConfigManager().getConfig("stats.yml");
	}
	
	public int getStat(StatType s, Player p) {
		return yml.getInt(p.getName() + "." + s.name());
	}
	
	public void setValue(StatType s, Player p,int value) throws IOException {
		yml.set(p.getName() + "." + s.name(), value);
		plugin.getConfigManager().save("stats.yml");
	}
}
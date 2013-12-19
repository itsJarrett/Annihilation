package net.coasterman10.Annihilation.stats;

import java.io.IOException;
import java.sql.SQLException;

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
		if (!plugin.useMysql) {
			return yml.getInt(p.getName() + "." + s.name());
		} else {
			try {
				return plugin.getDatabaseHandler().query("SELECT * FROM `annihilation` WHERE `username`='" + p.getName() + "'").getResultSet().getInt(s.name().toLowerCase());
			} catch (SQLException ex) {
				ex.printStackTrace();
				return -5;
			}
		}
	}
	
	public void setValue(StatType s, Player p, int value) throws IOException {
		if (!plugin.useMysql) {
			yml.set(p.getName() + "." + s.name(), value);
			plugin.getConfigManager().save("stats.yml");
		} else {
			plugin.getDatabaseHandler().query("UPDATE `" + s.name().toLowerCase() + "`='" + value + "' WHERE `username`='" + p.getName() + "'");
		}
	}
}
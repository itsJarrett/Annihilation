package net.coasterman10.Annihilation;

import java.util.logging.Level;

import net.coasterman10.Annihilation.commands.AnnihilationCommand;
import net.coasterman10.Annihilation.listeners.ChatListener;
import net.coasterman10.Annihilation.listeners.PlayerListener;
import net.coasterman10.Annihilation.listeners.ResourceListener;
import net.coasterman10.Annihilation.listeners.WorldListener;
import net.coasterman10.Annihilation.maps.MapManager;
import net.coasterman10.Annihilation.maps.VotingManager;
import net.coasterman10.Annihilation.stats.DatabaseHandler;
import net.coasterman10.Annihilation.stats.StatsManager;
import net.coasterman10.Annihilation.teams.Team;
import net.coasterman10.Annihilation.teams.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Annihilation extends JavaPlugin {
	private ConfigManager configManager;
	private VotingManager voting;
	private MapManager maps;
	private TeamManager teams;
	private PhaseTimer timer;
	private ResourceListener resources;
	private StatsManager stats;
	private DatabaseHandler db;
	public Boolean useMysql = false;
	
	@Override
	public void onEnable() {
		configManager = new ConfigManager(this);
		configManager.loadConfigFiles("config.yml", "maps.yml", "shops.yml");

		maps = new MapManager(this, configManager.getConfig("maps.yml"));
		teams = new TeamManager(this);

		Configuration shops = configManager.getConfig("shops.yml");
		new Shop(this, "Weapon", shops);
		new Shop(this, "Brewing", shops);

		new AnnihilationCommand(this);
		new ChestLocker(this);
		new ChatListener(this);
		new PlayerListener(this);
		new WorldListener(this);
		stats = new StatsManager(this);
		resources = new ResourceListener(this);
		
		Configuration config = configManager.getConfig("config.yml");
		timer = new PhaseTimer(this, config);
		voting = new VotingManager(this);

		voting.setCurrentForPlayers(getServer().getOnlinePlayers());
		
		if (useMysql) {
			db = new DatabaseHandler(this.getConfig().getString("MySQL.host"),
					this.getConfig().getInt("MySQL.port"),
					this.getConfig().getString("MySQL.name"),
					this.getConfig().getString("MySQL.user"),
					this.getConfig().getString("MySQL.pass"),
					this);
			
			db.query("CREATE TABLE IF NOT EXISTS `annihilation` ( `username` varchar(16) NOT NULL, `kills` int(16) NOT NULL, `deaths` int(16) NOT NULL, `wins` int(16) NOT NULL, `losses` int(16) NOT NULL, `nexus_damage` int(16) NOT NULL, UNIQUE KEY `username` (`username`) ) ENGINE=InnoDB DEFAULT CHARSET=latin1;");
		} else db = new DatabaseHandler(this);
	}

	public boolean startTimer() {
		if (timer.isRunning())
			return false;

		timer.start();
		voting.setCurrentForPlayers(getServer().getOnlinePlayers());

		return true;
	}

	public void startGame() {
		for (Team t : teams.getTeams()) {
			for (Player p : t.getPlayers()) {
				Location spawn = maps.getSpawnPoint(t.getName());
				p.teleport(spawn);
			}
		}

		resources.queueDiamondSpawn();
	}

	public void advancePhase() {

	}

	public void onSecond() {
		long time = timer.getTime();

		if (time == -5L) {
			if (maps.selectMap(voting.getWinner()))
				getServer().broadcastMessage(
						voting.getWinner() + " selected, loading...");
			else
				getServer().broadcastMessage(
						"Could not load " + voting.getWinner());
			voting.end();
		}

		if (time == 0L)
			startGame();
	}

	public int getPhase() {
		return timer.getPhase();
	}

	public TeamManager getTeamManager() {
		return teams;
	}

	public MapManager getMapManager() {
		return maps;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public StatsManager getStatsManager() {
		return stats;
	}
	
	public DatabaseHandler getDatabaseHandler() {
		return db;
	}

	public int getPhaseDelay() {
		return configManager.getConfig("config.yml").getInt("phase-period");
	}

	public void log(String m, Level l) {
		Bukkit.getLogger().log(l, m);
	}
}

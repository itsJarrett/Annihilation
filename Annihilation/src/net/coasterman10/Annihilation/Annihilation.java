package net.coasterman10.Annihilation;

import net.coasterman10.Annihilation.maps.MapManager;
import net.coasterman10.Annihilation.maps.VotingManager;

import org.bukkit.plugin.java.JavaPlugin;

public final class Annihilation extends JavaPlugin {
    MapManager mapManager;
    VotingManager votingManager;
    ConfigManager config;

    @Override
    public void onEnable() {
	config = new ConfigManager(this);
	config.loadConfigFile("config.yml");
	mapManager = new MapManager(getLogger(), config.getConfig("config.yml")
		.getConfigurationSection("maps"));
	votingManager = new VotingManager(mapManager);
	votingManager.startTimer(this);
	votingManager.setCurrentForPlayers(getServer().getOnlinePlayers());
    }

    public void startGame() {
	getServer().broadcastMessage(votingManager.getWinner());
    }
}

package net.coasterman10.Annihilation;

import net.coasterman10.Annihilation.commands.AnnihilationCommand;
import net.coasterman10.Annihilation.maps.MapManager;
import net.coasterman10.Annihilation.maps.VotingManager;
import net.coasterman10.Annihilation.teams.TeamManager;

import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Annihilation extends JavaPlugin {
    private ConfigManager configManager;
    private VotingManager voting;
    private MapManager maps;
    private TeamManager teams;

    private PhaseTimer timer;

    @Override
    public void onEnable() {
	configManager = new ConfigManager(this);
	configManager.loadConfigFiles("config.yml", "maps.yml", "shops.yml");

	maps = new MapManager(getLogger(), configManager.getConfig("maps.yml"));
	teams = new TeamManager(this);

	Configuration shops = configManager.getConfig("shops.yml");
	new Shop(this, "Weapon", shops);
	new Shop(this, "Brewing", shops);

	new AnnihilationCommand(this);
	new ChestLocker(this, teams);
	new ChatListener(this, teams);

	Configuration config = configManager.getConfig("config.yml");
	timer = new PhaseTimer(this, config);
	voting = new VotingManager(this, maps);
    }

    @Override
    public void onDisable() {

    }

    public boolean startTimer() {
	if (timer.isRunning())
	    return false;

	timer.start();
	voting.setCurrentForPlayers(getServer().getOnlinePlayers());

	return true;
    }

    public void startGame() {

    }

    public void advancePhase() {

    }

    public void onSecond() {
	long time = timer.getTime();

	if (time == -5L) {
	    maps.selectMap(voting.getWinner());
	    voting.end();
	}

	if (time == 0L)
	    startGame();
    }
}

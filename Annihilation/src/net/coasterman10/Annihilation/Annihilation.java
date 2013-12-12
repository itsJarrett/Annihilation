package net.coasterman10.Annihilation;

import net.coasterman10.Annihilation.chests.ChestLocker;
import net.coasterman10.Annihilation.commands.AnnihilationCommand;
import net.coasterman10.Annihilation.maps.MapManager;
import net.coasterman10.Annihilation.maps.VotingManager;
import net.coasterman10.Annihilation.shop.Shop;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class Annihilation extends JavaPlugin {
    private ConfigManager configManager;
    private VotingManager voting;
    private MapManager maps;

    private PhaseTimer timer;

    @Override
    public void onEnable() {
	new AnnihilationCommand(this);
	new ChestLocker(this);
	new Shop(this, "Weapon", Shop.Inventories.weaponShopItems);
	new Shop(this, "Brewing", Shop.Inventories.brewingShopItems);

	configManager = new ConfigManager(this);
	configManager.loadConfigFile("config.yml");
	
	timer = new PhaseTimer(this, 120L, 600L);

	FileConfiguration config = configManager.getConfig("config.yml");
	maps = new MapManager(getLogger(),
		config.getConfigurationSection("maps"));
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

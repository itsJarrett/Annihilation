package net.coasterman10.Annihilation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.TreeMap;

import net.coasterman10.Annihilation.Annihilation;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigManager {
    private static class Configuration {
	private final File configFile;
	private YamlConfiguration config;

	public Configuration(File configFile) {
	    this.configFile = configFile;
	    config = new YamlConfiguration();
	}

	public YamlConfiguration getConfig() {
	    return config;
	}

	public void load() throws IOException, InvalidConfigurationException {
	    config.load(configFile);
	}

	public void save() throws IOException {
	    config.save(configFile);
	}
    }

    private final Annihilation plugin;
    private final File configFolder;
    private final TreeMap<String, Configuration> configs = new TreeMap<String, Configuration>(
	    String.CASE_INSENSITIVE_ORDER);

    public ConfigManager(Annihilation plugin) {
	this.plugin = plugin;
	configFolder = plugin.getDataFolder();
	if (!configFolder.exists()) {
	    configFolder.mkdirs();
	}
    }

    public void loadConfigFile(String filename) {
	loadConfigFiles(filename);
    }

    public void loadConfigFiles(String... filenames) {
	for (String filename : filenames) {
	    File configFile = new File(configFolder, filename);
	    Configuration config;
	    try {
		if (!configFile.exists()) {
		    configFile.createNewFile();
		    InputStream stream = plugin.getResource(filename);
		    if (stream != null) {
			YamlConfiguration temp = YamlConfiguration
				.loadConfiguration(stream);
			temp.save(configFile);
		    } else {
			plugin.getLogger().warning(
				"Default configuration for " + filename
					+ " missing");
		    }
		}
		config = new Configuration(configFile);
		config.load();
		configs.put(filename, config);
	    } catch (IOException e) {
		e.printStackTrace();
	    } catch (InvalidConfigurationException e) {
		e.printStackTrace();
	    }
	}
    }

    public void save(String filename) {
	if (configs.containsKey(filename)) {
	    try {
		configs.get(filename).save();
	    } catch (Exception e) {
		printException(e, filename);
	    }
	}
    }

    public void reload(String filename) {
	if (configs.containsKey(filename)) {
	    try {
		configs.get(filename).load();
	    } catch (Exception e) {
		printException(e, filename);
	    }
	}
    }

    public YamlConfiguration getConfig(String filename) {
	if (configs.containsKey(filename))
	    return configs.get(filename).getConfig();
	else
	    return null;
    }

    private void printException(Exception e, String filename) {
	if (e instanceof IOException) {
	    plugin.getLogger().severe(
		    "I/O exception while handling " + filename);
	} else if (e instanceof InvalidConfigurationException) {
	    plugin.getLogger().severe("Invalid configuration in " + filename);
	}
	e.printStackTrace();
    }
}

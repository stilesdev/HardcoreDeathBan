package com.mstiles92.hardcoredeathban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Bans {
	
	private final HardcoreDeathBanPlugin plugin;
	private YamlConfiguration banConfig;
	private File configFile;
	private boolean loaded;
	
	public Bans(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
		loaded = false;
	}

	public void load(String filename) {
		configFile = new File(plugin.getDataFolder(), filename);
		
		if (configFile.exists()) {
			banConfig = new YamlConfiguration();
			
			try {
				banConfig.load(configFile);
			}
			catch (FileNotFoundException e) {
				// TODO Handle catching exception
			}
			catch (IOException e) {
				// TODO Handle catching exception
			}
			catch (InvalidConfigurationException e) {
				// TODO Handle catching exception
			}
			loaded = true;
		} else {
			try {
				configFile.createNewFile();
				banConfig = new YamlConfiguration();
				banConfig.load(configFile);
			}
			catch (IOException e) {
				// TODO Handle catching exception
			}
			catch (InvalidConfigurationException e) {
				// TODO Handle catching exception
			}
		}
	}
	
	public void save() {
		try {
			banConfig.save(configFile);
		}
		catch (IOException e) {
			// TODO Handle catching exception
		}
	}
	
	public File getFile() {
		return configFile;
	}
	
	public YamlConfiguration getConfig() {
		if (!loaded) {
			load("bans.yml");
		}
		return banConfig;
	}
}

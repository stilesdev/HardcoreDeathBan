package com.mstiles92.hardcoredeathban;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class RevivalCredits {
	
	private final HardcoreDeathBanPlugin plugin;
	private YamlConfiguration tokenConfig;
	private File configFile;
	private boolean loaded = false;
	private String filename;
	
	public RevivalCredits(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}
	
	public void loadConfig(String filename) {
		
		this.filename = filename;
		configFile = new File(plugin.getDataFolder(), filename);
		
		if (configFile.exists()) {
			tokenConfig = new YamlConfiguration();
			try {
				tokenConfig.load(configFile);
			}
			catch (FileNotFoundException e) {
				
			}
			catch (IOException e) {
				
			}
			catch (InvalidConfigurationException e) {
				
			}
			loaded = true;
		} else {
			try {
				configFile.createNewFile();
				tokenConfig = new YamlConfiguration();
				tokenConfig.load(configFile);
			}
			catch (IOException e) {
				
			}
			catch (InvalidConfigurationException e) {
				
			}
		}	
	}
	
	public void saveConfig() {
		try {
			tokenConfig.save(configFile);
		}
		catch (IOException e) {
			
		}
	}
}

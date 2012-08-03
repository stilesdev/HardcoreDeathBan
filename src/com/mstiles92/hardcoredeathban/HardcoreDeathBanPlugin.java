package com.mstiles92.hardcoredeathban;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class HardcoreDeathBanPlugin extends JavaPlugin {

	public Logger log;
	public PluginDescriptionFile pdf;
	public FileConfiguration config;
	
	private final SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm a z");
	private final SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private final RevivalTokens tokens = new RevivalTokens();
	
	public void onEnable() {
		log = this.getLogger();
		pdf = this.getDescription();
		config = this.getConfig();
		this.getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		this.getServer().getPluginManager().registerEvents(new LoginEvent(this), this);
		
		config.options().copyDefaults(true);
		this.saveConfig();
	}
	
	public void onDisable() {
		
	}
}

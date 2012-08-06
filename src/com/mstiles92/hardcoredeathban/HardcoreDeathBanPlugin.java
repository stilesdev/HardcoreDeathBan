package com.mstiles92.hardcoredeathban;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.mstiles92.hardcoredeathban.commands.*;
import com.mstiles92.hardcoredeathban.events.*;

public class HardcoreDeathBanPlugin extends JavaPlugin {

	public Logger log;
	public PluginDescriptionFile pdf;
	public FileConfiguration config;
	
	private final SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm a z");
	private final SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private final RevivalCredits credits = new RevivalCredits(this);
	private final Bans bans = new Bans(this);
	public ArrayList<String> banList = new ArrayList<String>();
	public boolean enabled =  true;
	
	public void onEnable() {
		log = this.getLogger();
		pdf = this.getDescription();
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		credits.load("credits.yml");
		bans.load("bans.yml");
		
		this.getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		this.getServer().getPluginManager().registerEvents(new LoginEvent(this), this);
		
		this.getCommand("deathban").setExecutor(new DeathbanCommand(this));
		this.getCommand("credits").setExecutor(new CreditsCommand(this));
		
	}
	
	public void onDisable() {
		this.saveConfig();
		credits.save();
		bans.save();
	}
	
	public Calendar getUnbanDate(String player) {
		if (player == null) return null;
		
		Calendar c = Calendar.getInstance();
		Object o = bans.getConfig().get(player.toLowerCase());
		if (o == null) return null;
		
		long ms = (Long)o;
		c.setTimeInMillis(ms);
		return c;
	}
	
	public boolean isBanned(String player) {
		Calendar unbanDate = getUnbanDate(player);
		if (unbanDate != null) {
		
			Calendar now = Calendar.getInstance();
			if (unbanDate.after(now)) {
				return true;
			}
			removeFromBan(player);
		}
		return false;
	}
	
	public void removeFromBan(String player) {
		bans.getConfig().set(player.toLowerCase(), null);
	}
	
	public void setBanned(String player) {
		this.setBanned(player, config.getString("Ban-Time"));
	}
	
	public void setBanned(String player, String time) {
		Player p = this.getServer().getPlayerExact(player);
		try {
			Calendar unbanDate = parseBanTime(time);
			
			if (p != null) {			// Player is online
				if (!p.hasPermission("deathban.ban.exempt")) {
					if (getCredits(player) > 1) {
						giveCredits(player, -1);
						log.info("Player " + player + " used a revival credit. Remaining: " + getCredits(player));
					} else {
						bans.getConfig().set(player.toLowerCase(), unbanDate.getTimeInMillis());
						bans.save();
						banList.add(player);
						this.getServer().getScheduler().scheduleSyncDelayedTask(this, new KickRunnable(this, player), this.getConfig().getInt("Tick-Delay"));
					}
				}
			} else {					// Player is offline
				bans.getConfig().set(player.toLowerCase(), unbanDate.getTimeInMillis());
				bans.save();
			}
		}
		catch (NumberFormatException e) {
			// TODO Handle exception
		}
	}
	
	public Calendar parseBanTime(String banTime) throws NumberFormatException {
		Calendar c = Calendar.getInstance();
		
		int dayIndex = banTime.indexOf("d");
		int hourIndex = banTime.indexOf("h");
		int minIndex = banTime.indexOf("m");
		int days = 0, hours = 0, minutes = 0;
		
		if (dayIndex != -1 && hourIndex != -1 && minIndex != -1) {                  // dhm
			days = Integer.parseInt(banTime.substring(0, dayIndex));
			hours = Integer.parseInt(banTime.substring(dayIndex + 1, hourIndex));
			minutes = Integer.parseInt(banTime.substring(hourIndex + 1, minIndex));
		} else if (dayIndex == -1) {
			if (hourIndex != -1 && minIndex != -1) {								// hm
				days = 0;
				hours = Integer.parseInt(banTime.substring(0, hourIndex));
				minutes = Integer.parseInt(banTime.substring(hourIndex + 1, minIndex));
			} else if (hourIndex == -1 && minIndex != -1) {							// m
				days = 0;
				hours = 0;
				minutes = Integer.parseInt(banTime.substring(0, minIndex));
			} else if (hourIndex != -1 && minIndex == -1) {							// h
				days = 0;
				hours = Integer.parseInt(banTime.substring(0, hourIndex));
				minutes = 0;
			}
		} else if (hourIndex == -1)  {
			if (dayIndex != -1 && minIndex != -1) {									// dm
				days = Integer.parseInt(banTime.substring(0, dayIndex));
				hours = 0;
				minutes = Integer.parseInt(banTime.substring(dayIndex + 1, minIndex));
			} else if (dayIndex != -1 && minIndex == -1) {							// d
				days = Integer.parseInt(banTime.substring(0, dayIndex));
				hours = 0;
				minutes = 0;
			}
		} else if (minIndex == -1) {
			if (dayIndex != -1 && hourIndex != -1) {								// dh
				days = Integer.parseInt(banTime.substring(0, dayIndex));
				hours = Integer.parseInt(banTime.substring(dayIndex + 1, hourIndex));
				minutes = 0;
			}
		}
		
		c.add(Calendar.DATE, days);
		c.add(Calendar.HOUR, hours);
		c.add(Calendar.MINUTE, minutes);
		
		return c;
	}
	
	public String replaceVariables(String msg, String name) {
		msg = msg.replaceAll("%server%", this.getServer().getServerName());
		if (name != null) msg = msg.replaceAll("%player%", name);
		Calendar now = Calendar.getInstance();
		msg = msg.replaceAll("%currenttime%", TimeFormat.format(now.getTime()));
		msg = msg.replaceAll("%currentdate%", DateFormat.format(now.getTime()));
		Calendar unbanTime = getUnbanDate(name);
		if (unbanTime != null) {
			msg = msg.replaceAll("%unbantime%", TimeFormat.format(unbanTime.getTime()));
			msg = msg.replaceAll("%unbandate%", DateFormat.format(unbanTime.getTime()));
		}
		
		return msg;
	}
	
	public int getCredits(String player) {
		Object o = credits.getConfig().get(player.toLowerCase());
		if (o != null) {
			return (Integer)o;
		}
		return 0;
	}
	
	public void giveCredits(String player, int amount) {
		int credit = getCredits(player);
		credit += amount;
		credits.getConfig().set(player.toLowerCase(), credit);
		credits.save();
	}
}

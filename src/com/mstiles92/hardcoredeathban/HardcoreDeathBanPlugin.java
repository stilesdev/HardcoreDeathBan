package com.mstiles92.hardcoredeathban;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import com.mstiles92.hardcoredeathban.commands.*;
import com.mstiles92.hardcoredeathban.events.*;

public class HardcoreDeathBanPlugin extends JavaPlugin {

	public Logger log;
	public FileConfiguration config;
	public Set<String> deathClasses;
	
	private final SimpleDateFormat TimeFormat = new SimpleDateFormat("hh:mm a z");
	private final SimpleDateFormat DateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private final RevivalCredits credits = new RevivalCredits(this);
	private final Bans bans = new Bans(this);
	
	public void onEnable() {
		log = this.getLogger();
		config = this.getConfig();
		config.options().copyDefaults(true);
		this.saveConfig();
		credits.load("credits.yml");
		bans.load("bans.yml");
		
		this.getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
		this.getServer().getPluginManager().registerEvents(new LoginEvent(this), this);
		
		this.getCommand("deathban").setExecutor(new DeathbanCommand(this));
		this.getCommand("credits").setExecutor(new CreditsCommand(this));
		
		this.deathClasses = config.getConfigurationSection("Death-Classes").getKeys(false);
		if (this.deathClasses.size() == 0) {
			this.log("No death classes found.");
		} else {
			for (String s : this.deathClasses) {
				this.log("Death class loaded: " + s);
			}
		}
	}
	
	public void onDisable() {
		credits.save();
		bans.save();
	}
	
	public void log(String message) {
		if (this.getConfig().getBoolean("Verbose")) {
			this.getLogger().info(message);
		}
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
		this.log("Player unbanned: " + player);
		bans.getConfig().set(player.toLowerCase(), null);
	}
	
	public void setBanned(String player) {
		Player p = this.getServer().getPlayerExact(player);
		if (p != null) {
			for (String s : this.deathClasses) {
				Permission perm = new Permission("deathban.class." + s);
				perm.setDefault(PermissionDefault.FALSE);
				if (p.hasPermission(perm)) {
					this.setBanned(player, config.getString("Death-Classes." + s + ".Ban-Time"));
					this.log("Death class " + s + " detected for " + player);
					return;
				}
			}
		}
		this.log("No death class detected for " + player);
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
						this.log("Player used a revival credit: " + player + ", Remaining: " + getCredits(player));
					} else {
						bans.getConfig().set(player.toLowerCase(), unbanDate.getTimeInMillis());
						bans.save();
						this.log("Player added to ban list: " + player);
						this.getServer().getScheduler().scheduleSyncDelayedTask(this, new KickRunnable(this, player), this.getConfig().getInt("Tick-Delay"));
					}
				}
			} else {					// Player is offline
				bans.getConfig().set(player.toLowerCase(), unbanDate.getTimeInMillis());
				bans.save();
				this.log("Offline player added to ban list: " + player);
			}
		}
		catch (Exception e) {
			// TODO Handle exception
		}
	}
	
	public static long parseBanTimeMS(String banTime) throws Exception {
		return parseBanTime(banTime).getTimeInMillis();
	}
	
	public static Calendar parseBanTime(String banTime) throws Exception {
		Pattern p = Pattern.compile(
				"(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
				+ "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
		int years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
		boolean match = false;
		Matcher m = p.matcher(banTime);
		while (m.find()) {
			if (m.group() == null || m.group().isEmpty()) continue;
			for (int i = 0; i < m.groupCount(); i++) {
				if (m.group(i) != null && !m.group(i).isEmpty()) {
					match = true;
					break;
				}
			}
			
			if (match)
			{
				if (m.group(1) != null && !m.group(1).isEmpty())
				{
					years = Integer.parseInt(m.group(1));
				}
				if (m.group(2) != null && !m.group(2).isEmpty())
				{
					months = Integer.parseInt(m.group(2));
				}
				if (m.group(3) != null && !m.group(3).isEmpty())
				{
					weeks = Integer.parseInt(m.group(3));
				}
				if (m.group(4) != null && !m.group(4).isEmpty())
				{
					days = Integer.parseInt(m.group(4));
				}
				if (m.group(5) != null && !m.group(5).isEmpty())
				{
					hours = Integer.parseInt(m.group(5));
				}
				if (m.group(6) != null && !m.group(6).isEmpty())
				{
					minutes = Integer.parseInt(m.group(6));
				}
				if (m.group(7) != null && !m.group(7).isEmpty())
				{
					seconds = Integer.parseInt(m.group(7));
				}
				break;
			}
		}
		if (!match) {
			throw new Exception("Unable to parse time string.");
		}
		
		Calendar c = new GregorianCalendar();
		
		if (years > 0) c.add(Calendar.YEAR, years);
		if (months > 0) c.add(Calendar.MONTH, months);
		if (weeks > 0) c.add(Calendar.WEEK_OF_YEAR, weeks);
		if (days > 0) c.add(Calendar.DAY_OF_YEAR, days);
		if (hours > 0) c.add(Calendar.HOUR_OF_DAY, hours);
		if (minutes > 0) c.add(Calendar.MINUTE, minutes);
		if (seconds > 0) c.add(Calendar.SECOND, seconds);
		
		return c;
	}
	
	public static String buildTimeDifference(long ms) {
		Calendar now = new GregorianCalendar();
		Calendar then = new GregorianCalendar();
		then.setTimeInMillis(ms);
		return buildTimeDifference(now, then);
	}
	
	public static String buildTimeDifference(Calendar first, Calendar second) {
		if (first.equals(second)) return "now";
		StringBuilder s = new StringBuilder();
		int[] calendarTypes = new int[] {
				Calendar.YEAR,
				Calendar.MONTH,
				Calendar.DAY_OF_MONTH,
				Calendar.HOUR_OF_DAY,
				Calendar.MINUTE,
				Calendar.SECOND };
		String[] names = new String[] { "year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds" };
		
		for (int i = 0; i < calendarTypes.length; i++) {
			int difference = getTypeDifference(calendarTypes[i], first, second);
			if (difference > 0) {
				s.append(" ").append(difference).append(" ").append(names[i * 2 + (difference > 1 ? 1 : 0)]);
			}
		}
		
		if (s.length() == 0) return "now";
		
		return s.toString();
	}
	
	private static int getTypeDifference(int type, Calendar first, Calendar second) {
		int difference = 0;
		long save = first.getTimeInMillis();
		while (!first.after(second)) {
			save = first.getTimeInMillis();
			first.add(type, 1);
			difference++;
		}
		difference--;
		first.setTimeInMillis(save);
		return difference;
	}
	
	public String replaceVariables(String msg, String name) {
		msg = msg.replaceAll("%server%", this.getServer().getServerName());
		if (name != null) msg = msg.replaceAll("%player%", name);
		Calendar now = new GregorianCalendar();
		msg = msg.replaceAll("%currenttime%", TimeFormat.format(now.getTime()));
		msg = msg.replaceAll("%currentdate%", DateFormat.format(now.getTime()));
		Calendar unbanTime = getUnbanDate(name);
		if (unbanTime != null) {
			msg = msg.replaceAll("%unbantime%", TimeFormat.format(unbanTime.getTime()));
			msg = msg.replaceAll("%unbandate%", DateFormat.format(unbanTime.getTime()));
			msg = msg.replaceAll("%bantimeleft%", buildTimeDifference(now, unbanTime));
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
	
	public boolean playerHasJoined(String player) {
		Object o = credits.getConfig().get(player.toLowerCase());
		if (o != null) {
			return true;
		}
		return false;
	}
}

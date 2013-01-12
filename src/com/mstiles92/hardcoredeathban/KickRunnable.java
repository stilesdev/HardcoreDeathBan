package com.mstiles92.hardcoredeathban;

import java.util.Calendar;

import org.bukkit.entity.Player;

/**
 * KickRunnable is a class that implements the Runnable interface, used to
 * kick the player after a short delay when getting banned after death.
 * 
 * @author mstiles92
 */
public class KickRunnable implements Runnable {
	private final HardcoreDeathBanPlugin plugin;
	private final String playerName;
	
	/**
	 * The main constructor for this class.
	 * 
	 * @param plugin the instance of the plugin
	 * @param playerName the name of the player to kick
	 */
	public KickRunnable(HardcoreDeathBanPlugin plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
	}

	@Override
	public void run() {
		Calendar unbanDate = plugin.bans.getUnbanCalendar(playerName);
		if (unbanDate != null) {
			Player p = plugin.getServer().getPlayerExact(playerName);
			String kickMessage = plugin.getConfig().getString("Death-Message");
			if (p != null) {
				for (String s : plugin.bans.deathClasses) {
					if (p.hasPermission("deathban.class." + s)) {
						kickMessage = plugin.getConfig().getString("Death-Classes." + s + ".Death-Message");
						break;
					}
				}
				p.kickPlayer(plugin.replaceVariables(kickMessage, p.getName()));
				plugin.log("[KickRunnable] Player " + playerName + " kicked.");
			} else {
				plugin.log("[KickRunnable] Player " + playerName + " is offline.");
			}
		} else {
			plugin.log("[KickRunnable] Failed to store ban for " + playerName);
		}
	}
}

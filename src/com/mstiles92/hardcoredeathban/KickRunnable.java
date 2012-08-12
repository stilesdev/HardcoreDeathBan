package com.mstiles92.hardcoredeathban;

import java.util.Calendar;

import org.bukkit.entity.Player;

public class KickRunnable implements Runnable {
	
	private final HardcoreDeathBanPlugin plugin;
	private final String playerName;
	
	public KickRunnable(HardcoreDeathBanPlugin plugin, String playerName) {
		this.plugin = plugin;
		this.playerName = playerName;
	}

	@Override
	public void run() {
		Calendar unbanDate = plugin.getUnbanDate(playerName);
		if (unbanDate != null) {
			Player p = plugin.getServer().getPlayerExact(playerName);
			String kickMessage = plugin.config.getString("Death-Message");
			if (p != null) {
				for (String s : plugin.deathClasses) {
					if (p.hasPermission("deathban.class." + s)) {
						kickMessage = plugin.config.getString("Death-Classes." + s + ".Death-Message");
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

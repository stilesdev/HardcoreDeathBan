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
			if (p != null) {
				String kickMessage = plugin.getConfig().getString("Death-Message");
				p.kickPlayer(plugin.replaceVariables(kickMessage, p.getName()));
				plugin.log.info("[KickRunnable] Player " + playerName + " kicked.");
			} else {
				plugin.log.info("[KickRunnable] Player " + playerName + " is offline.");
			}
		} else {
			plugin.log.info("[KickRunnable] Failed to store ban for " + playerName);
		}
		plugin.banList.clear();
	}

}

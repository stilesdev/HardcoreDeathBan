package com.mstiles92.hardcoredeathban.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;

import com.mstiles92.hardcoredeathban.HardcoreDeathBanPlugin;

public class LoginEvent implements Listener {
	
	private final HardcoreDeathBanPlugin plugin;
	
	public LoginEvent(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLogin(PlayerPreLoginEvent e) {
		if (plugin.config.getBoolean("Enabled")) {
			if (plugin.isBanned(e.getName())) {
				if (plugin.getCredits(e.getName()) < 1) {
					String s = plugin.config.getString("Banned-Message");
					e.disallow(Result.KICK_BANNED, plugin.replaceVariables(s, e.getName()));
				} else {
					plugin.giveCredits(e.getName(), -1);
					plugin.removeFromBan(e.getName());
					e.allow();
				}
			} else {
				e.allow();
			}
		}
	}

}

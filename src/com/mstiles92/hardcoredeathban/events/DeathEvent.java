package com.mstiles92.hardcoredeathban.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.mstiles92.hardcoredeathban.HardcoreDeathBanPlugin;

public class DeathEvent implements Listener {
	
	private final HardcoreDeathBanPlugin plugin;
	
	public DeathEvent(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (plugin.config.getBoolean("Enabled")) {
			plugin.log.info("Player death: " + e.getEntity().getName());
			plugin.setBanned(e.getEntity().getName());
		}
	}
	
}

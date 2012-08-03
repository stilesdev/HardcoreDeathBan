package com.mstiles92.hardcoredeathban;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;

public class LoginEvent implements Listener {
	
	private final HardcoreDeathBanPlugin plugin;
	
	public LoginEvent(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLogin(PlayerPreLoginEvent e) {
		
	}

}

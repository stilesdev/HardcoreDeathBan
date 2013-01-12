package com.mstiles92.hardcoredeathban;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

/**
 * HardcoreDeathBanListener is the class used to register the event handlers
 * needed for this plugin's operation.
 * 
 * @author mstiles92
 */
public class HardcoreDeathBanListener implements Listener {
	private final HardcoreDeathBanPlugin plugin;
	
	/**
	 * The main constructor used for this class.
	 * 
	 * @param plugin the instance of the plugin
	 */
	public HardcoreDeathBanListener(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (plugin.getConfig().getBoolean("Enabled") && !(e.getEntity().hasPermission("deathban.ban.exempt"))) {
			plugin.log("Player death: " + e.getEntity().getName());
			plugin.bans.banPlayer(e.getEntity().getName());
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerLogin(AsyncPlayerPreLoginEvent e) {
		if (plugin.getConfig().getBoolean("Enabled")) {
			if (plugin.bans.checkPlayerIsBanned(e.getName())) {
				if (plugin.credits.getPlayerCredits(e.getName()) < 1) {
					plugin.log("Banned player denied login: " + e.getName());
					String s = plugin.getConfig().getString("Early-Message");
					e.disallow(Result.KICK_BANNED, plugin.replaceVariables(s, e.getName()));
				} else {
					plugin.log("Banned player redeemed 1 revival credit upon login: " + e.getName());
					plugin.credits.givePlayerCredits(e.getName(), -1);
					plugin.bans.unbanPlayer(e.getName());
					e.allow();
				}
			} else if(!plugin.credits.checkPlayerHasPlayedBefore(e.getName())) {
				int startingCredits = plugin.getConfig().getInt("Starting-Credits");
				plugin.log("New player recieved " + 
					    Integer.toString(startingCredits) +
					    " revival credits upon their first login: " + e.getName());	//TODO: remove give of credits on first join
				plugin.credits.givePlayerCredits(e.getName(), startingCredits);
				e.allow();
			} else {
				e.allow();
			}
		}
	}
}

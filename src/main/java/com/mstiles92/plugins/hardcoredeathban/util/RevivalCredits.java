/**
 * This document is a part of the source code and related artifacts for
 * HardcoreDeathBan, an open source Bukkit plugin for hardcore-type servers
 * where players are temporarily banned upon death.
 *
 * http://dev.bukkit.org/bukkit-plugins/hardcoredeathban/
 * http://github.com/mstiles92/HardcoreDeathBan
 *
 * Copyright (c) 2014 Matthew Stiles (mstiles92)
 *
 * Licensed under the Common Development and Distribution License Version 1.0
 * You may not use this file except in compliance with this License.
 *
 * You may obtain a copy of the CDDL-1.0 License at
 * http://opensource.org/licenses/CDDL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the license.
 */

package com.mstiles92.plugins.hardcoredeathban.util;

import java.io.File;
import java.io.IOException;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * RevivalCredits is the class used to store the amount of credits each player
 * currently holds, as well as methods to modify the credits of each player.
 * 
 * @author mstiles92
 */
public class RevivalCredits {
	
	private final HardcoreDeathBan plugin;
	private YamlConfiguration config;
	private File file;
	
	/**
	 * The main constructor to be used with this class.
	 * @param plugin instance of the plugin
	 * @param filename name of the file to save to disk
	 * @throws Exception if there is an error while opening or creating the file
	 */
	public RevivalCredits(HardcoreDeathBan plugin, String filename) throws Exception {
		this.plugin = plugin;
		load(filename);
	}
	
	private void load(String filename) throws Exception {
		file = new File(plugin.getDataFolder(), filename);
		config = new YamlConfiguration();
		
		if (!file.exists()) {
			file.createNewFile();
		}
		config.load(file);
	}
	
	/**
	 * Save the config to a file.
	 */
	public void save() {
		try {
			config.save(file);
		}
		catch (IOException e) {
			plugin.getLogger().warning(ChatColor.RED + "Error occurred while saving credits config file.");
		}
	}
	
	/**
	 * Get the number of credits a player currently holds.
	 * @param player the name of the player to check
	 * @return the number of credits the player holds
	 */
	public int getPlayerCredits(String player) {
		if (!config.contains(player.toLowerCase())) {
			config.set(player.toLowerCase(), plugin.getConfig().getInt("Starting-Credits"));
			save();
		}
		return config.getInt(player.toLowerCase());
	}
	
	/**
	 * Give credits to a player.
	 * @param player the player to give the credits to
	 * @param amount the amount of credits to give
	 */
	public void givePlayerCredits(String player, int amount) {
		if (!config.contains(player.toLowerCase())) {
			config.set(player.toLowerCase(), plugin.getConfig().getInt("Starting-Credits"));
		}
		config.set(player.toLowerCase(), amount + config.getInt(player.toLowerCase()));
		save();
	}
	
	/**
	 * Give credits to all registered players.
	 * @param amount the amount of credits to give
	 */
	public void giveAllPlayersCredits(int amount) {
		for (String player : config.getKeys(false)) {
			givePlayerCredits(player, amount);
		}
	}
	
	/**
	 * Set the amount of credits a player currently holds.
	 * @param player the player to set the credits for
	 * @param amount the amount of credits the player should have
	 */
	public void setPlayerCredits(String player, int amount) {
		config.set(player.toLowerCase(), amount);
		save();
	}
	
	/**
	 * Reset all players' credits to the amount recieved when starting out.
	 */
	public void resetAllPlayersCredits() {
		final int startingAmount = plugin.getConfig().getInt("Starting-Credits");
		for (String player : config.getKeys(false)) {
			setPlayerCredits(player, startingAmount);
		}
	}
	
	/**
	 * Check if a player has been seen by this plugin before.
	 * @param player the name of the player to check
	 * @return true if they are registered with this plugin, false otherwise
	 */
	public boolean checkPlayerHasPlayedBefore(String player) {
		return config.contains(player.toLowerCase());
	}
}

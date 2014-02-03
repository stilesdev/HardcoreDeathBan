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

package com.mstiles92.plugins.hardcoredeathban.tasks;

import java.util.Calendar;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import org.bukkit.entity.Player;

/**
 * KickRunnable is a class that implements the Runnable interface, used to
 * kick the player after a short delay when getting banned after death.
 * 
 * @author mstiles92
 */
public class KickRunnable implements Runnable {
	private final HardcoreDeathBan plugin;
	private final String playerName;
	
	/**
	 * The main constructor for this class.
	 * 
	 * @param plugin the instance of the plugin
	 * @param playerName the name of the player to kick
	 */
	public KickRunnable(HardcoreDeathBan plugin, String playerName) {
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

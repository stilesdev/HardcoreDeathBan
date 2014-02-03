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

package com.mstiles92.plugins.hardcoredeathban.listeners;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * PlayerListener is the class used to register the event handlers
 * needed for this plugin's operation.
 *
 * @author mstiles92
 */
public class PlayerListener implements Listener {
    private final HardcoreDeathBan plugin;

    /**
     * The main constructor used for this class.
     *
     * @param plugin the instance of the plugin
     */
    public PlayerListener(HardcoreDeathBan plugin) {
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
            } else if (!plugin.credits.checkPlayerHasPlayedBefore(e.getName())) {
                int startingCredits = plugin.getConfig().getInt("Starting-Credits");
                plugin.log("New player recieved " +
                        Integer.toString(startingCredits) +
                        " revival credits upon their first login: " + e.getName());    //TODO: remove give of credits on first join
                plugin.credits.givePlayerCredits(e.getName(), startingCredits);
                e.allow();
            } else {
                e.allow();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.updateAvailable && e.getPlayer().hasPermission("deathban.receivealerts")) {
            e.getPlayer().sendMessage(ChatColor.GREEN + "[HardcoreDeathBan] New version available! See http://dev.bukkit.org/bukkit-plugins/hardcoredeathban/ for more information.");
            e.getPlayer().sendMessage(ChatColor.GREEN + "[HardcoreDeathBan] Current version: " + ChatColor.BLUE + plugin.getDescription().getVersion() + ChatColor.GREEN + ", New version: " + ChatColor.BLUE + plugin.latestKnownVersion);
            e.getPlayer().sendMessage(ChatColor.GREEN + "[HardcoreDeathBan] Changes in this version: " + ChatColor.BLUE + plugin.changes);
        }
    }
}

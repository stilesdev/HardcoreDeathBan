/*
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

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.data.PlayerData;
import com.mstiles92.plugins.hardcoredeathban.util.Log;
import com.mstiles92.plugins.hardcoredeathban.util.Utils;

/**
 * PlayerListener is the class used to register the event handlers needed for this plugin's operation.
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (HardcoreDeathBan.getConfigObject().isEnabled() && !event.getEntity().hasPermission("deathban.ban.exempt")) {
            Log.verbose("Player death: " + event.getEntity().getName());
            Utils.banPlayer(event.getEntity());
        }
    }

    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        PlayerData playerData = PlayerData.get(event.getUniqueId());

        if (playerData != null) {
            if (HardcoreDeathBan.getConfigObject().isEnabled() && Utils.checkPlayerBanned(event.getUniqueId())) {
                if (playerData.getRevivalCredits() > 0) {
                    Log.verbose("Banned player redeemed 1 revival credit upon login: " + event.getName());
                    playerData.addRevivalCredits(-1);
                    Utils.unbanPlayer(event.getUniqueId());
                    event.allow();
                } else {
                    Log.verbose("Banned player denied login: " + event.getName());
                    String earlyMessage = Utils.replaceMessageVariables(HardcoreDeathBan.getConfigObject().getEarlyMessage(), event.getUniqueId());
                    event.disallow(Result.KICK_BANNED, earlyMessage);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (HardcoreDeathBan.getInstance().getUpdateChecker().isUpdateAvailable() && event.getPlayer().hasPermission("deathban.receivealerts")) {
            String tag = ChatColor.BLUE + "[HardcoreDeathBan] " + ChatColor.GREEN;
            String currentVersion = HardcoreDeathBan.getInstance().getDescription().getVersion();
            String newVersion = HardcoreDeathBan.getInstance().getUpdateChecker().getNewVersion();

            event.getPlayer().sendMessage(tag + "New version available! See http://dev.bukkit.org/bukkit-plugins/hardcoredeathban/ for more information.");
            event.getPlayer().sendMessage(tag + "Current version: " + ChatColor.BLUE + currentVersion + ChatColor.GREEN + ", New version: " + ChatColor.BLUE + newVersion);
        }
    }
}

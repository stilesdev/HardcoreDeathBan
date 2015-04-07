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

package com.mstiles92.plugins.hardcoredeathban.util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.data.DeathClass;
import com.mstiles92.plugins.hardcoredeathban.tasks.KickRunnable;
import com.mstiles92.plugins.stileslib.calendar.CalendarUtils;

/**
 * Bans is a class used to store and modify the ban length of each player.
 *
 * @author mstiles92
 */
public class Bans {
    private final HardcoreDeathBan plugin;
    private YamlConfiguration config;
    private File file;

    /**
     * The main constructor to be used with this class.
     *
     * @param plugin   the instance of the plugin
     * @param filename name of the file to save to disk
     * @throws Exception if there is an error while opening or creating the file
     */
    public Bans(HardcoreDeathBan plugin, String filename) throws Exception {
        this.plugin = plugin;
        load(filename);

        if (HardcoreDeathBan.getConfigObject().getDeathClasses().size() == 0) {
            Log.verbose("No death classes found.");
        } else {
            for (DeathClass deathClass : HardcoreDeathBan.getConfigObject().getDeathClasses()) {
                Log.verbose("Death class loaded: " + deathClass.getName());
            }
        }
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
        } catch (IOException e) {
            Log.warning(ChatColor.RED + "Error occurred while saving bans config file.");
        }
    }

    /**
     * Get the date and time that the specified player is unbanned after.
     *
     * @param player name of the player to check
     * @return a Calendar object that specifies the date and time when the
     * player's ban is over, or null if the player is not banned
     */
    public Calendar getUnbanCalendar(String player) {
        if (player == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        final long ms = config.getLong(player.toLowerCase(), 0);
        if (ms == 0) {
            return null;
        }
        calendar.setTimeInMillis(ms);
        return calendar;
    }

    /**
     * Check if the specified player is currently banned.
     *
     * @param player the name of the player to check
     * @return true if the player is currently banned, false otherwise
     */
    public boolean checkPlayerIsBanned(String player) {
        final Calendar unban = getUnbanCalendar(player);
        final Calendar now = Calendar.getInstance();
        if (unban != null) {
            if (unban.after(now)) {
                return true;
            }
            unbanPlayer(player);
        }
        return false;
    }

    /**
     * Unban the specified player.
     *
     * @param player name of the player to be unbanned
     */
    public void unbanPlayer(String player) {
        Log.verbose("Player unbanned: " + player);
        config.set(player.toLowerCase(), null);
    }

    /**
     * Ban a player for their default time, taking possible death classes into account.
     *
     * @param player the player to ban
     */
    public void banPlayer(String player) {
        final Player p = plugin.getServer().getPlayerExact(player);
        if (p != null) {
            for (DeathClass deathClass : HardcoreDeathBan.getConfigObject().getDeathClasses()) {
                if (p.hasPermission(deathClass.getPermission())) {
                    banPlayer(player, deathClass.getBanTime());
                    Log.verbose("Death class " + deathClass.getName() + " detected for " + player);
                    return;
                }
            }
        }

        Log.verbose("No death class detected for " + player);
        banPlayer(player, HardcoreDeathBan.getConfigObject().getBanTime());
    }

    /**
     * Ban a player for a specified time.
     *
     * @param player the player to ban
     * @param time   the amount of time the player will be banned
     */
    public void banPlayer(String player, String time) {
        final Player p = plugin.getServer().getPlayerExact(player);
        try {
            final Calendar unbanDate = CalendarUtils.parseTimeDifference(time);

            if (p != null) {            // Player is online
                if (!p.hasPermission("deathban.ban.exempt")) {
                    config.set(player.toLowerCase(), unbanDate.getTimeInMillis());
                    save();
                    Log.verbose("Player added to ban list: " + player);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new KickRunnable(p.getUniqueId()), HardcoreDeathBan.getConfigObject().getTickDelay());
                }
            } else {                    // Player is offline
                config.set(player.toLowerCase(), unbanDate.getTimeInMillis());
                save();
                Log.verbose("Offline player added to ban list: " + player);
            }
        } catch (Exception e) {
            Log.verbose("Error occurred while banning player: " + player);
        }
    }
}

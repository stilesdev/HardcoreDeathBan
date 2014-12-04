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

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.data.DeathClass;
import com.mstiles92.plugins.hardcoredeathban.data.PlayerData;
import com.mstiles92.plugins.hardcoredeathban.tasks.KickRunnable;
import com.mstiles92.plugins.stileslib.calendar.CalendarUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

public class Utils {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("hh:mm a z");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    /**
     * Replace variables in admin-defined messages to be passed on to Players.
     *
     * @param message the message that potentially contains variables to be replaced
     * @param playerUUID the UUID of the Player who will be recieving this message
     * @return the altered message with all of the variables filled in
     */
    public static String replaceMessageVariables(String message, UUID playerUUID) {
        PlayerData playerData = PlayerData.get(playerUUID);

        if (playerData != null) {
            Calendar now = Calendar.getInstance();
            Calendar unbanTime = playerData.getUnbanTimeCalendar();

            message = message.replaceAll("%player%", playerData.getLastSeenName());

            message = message.replaceAll("%currenttime%", TIME_FORMAT.format(now.getTime()));
            message = message.replaceAll("%currentdate%", DATE_FORMAT.format(now.getTime()));

            if (unbanTime != null) {
                message = message.replaceAll("%unbantime%", TIME_FORMAT.format(unbanTime.getTime()));
                message = message.replaceAll("%unbandate%", DATE_FORMAT.format(unbanTime.getTime()));
                message = message.replaceAll("%bantimeleft%", CalendarUtils.buildTimeDifference(now, unbanTime));
            }
        }

        message = message.replaceAll("%server%", Bukkit.getServerName());

        return message;
    }

    /**
     * Ban the specified Player from the server. They will be banned for the amount of time specified by their death
     * class if they have one, or for the amount of time specified in the plugin's config if they do not.
     *
     * @param player the Player who should be banned
     */
    public static void banPlayer(Player player) {
        DeathClass deathClass = getDeathClass(player);

        if (deathClass == null) {
            banPlayer(player, HardcoreDeathBan.getConfigObject().getBanTime());
        } else {
            banPlayer(player, deathClass.getBanTime());
        }
    }

    /**
     * Ban the specified Player from the server for a specific amount of time.
     *
     * @param player the Player who should be banned
     * @param banTime the amount of time the Player should be banned for
     */
    public static void banPlayer(Player player, String banTime) {
        if (!player.hasPermission("deathban.ban.exempt")) {
            Calendar unbanDate = CalendarUtils.parseTimeDifference(banTime);
            PlayerData.get(player).setUnbanTimeInMillis(unbanDate.getTimeInMillis());

            if (player.isOnline()) {
                KickRunnable runnable = new KickRunnable(player.getUniqueId());
                runnable.runTaskLater(HardcoreDeathBan.getInstance(), HardcoreDeathBan.getConfigObject().getTickDelay());
            }
        }
    }

    /**
     * Unban the specified Player.
     *
     * @param player the Player who should be unbanned
     */
    public static void unbanPlayer(Player player) {
        PlayerData.get(player).setUnbanTimeInMillis(-1);
    }

    public static void unbanPlayer(UUID playerUUID) {
        PlayerData playerData = PlayerData.get(playerUUID);

        if (playerData != null) {
            playerData.setUnbanTimeInMillis(-1);
        }
    }

    /**
     * Get the death class for the Player specified by UUID.
     *
     * @param playerUUID the UUID of the Player whose death class should be found
     * @return the DeathClass for the Player if they have one, null if they do not
     */
    public static DeathClass getDeathClass(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        return (player == null) ? null : getDeathClass(player);
    }

    /**
     * Get the death class for the specified Player.
     *
     * @param player the Player whose death class should be found
     * @return the DeathClass for the Player if they have one, null if they do not
     */
    public static DeathClass getDeathClass(Player player) {
        List<DeathClass> deathClasses = HardcoreDeathBan.getConfigObject().getDeathClasses();

        for (DeathClass deathClass : deathClasses) {
            if (player.hasPermission(deathClass.getPermission())) {
                return deathClass;
            }
        }

        return null;
    }

    /**
     * Check if the specified Player is currently banned.
     *
     * @param playerUUID the UUID of the Player whose ban status should be checked
     * @return true if the Player is banned, false if they are not
     */
    public static boolean checkPlayerBanned(UUID playerUUID) {
        PlayerData playerData = PlayerData.get(playerUUID);

        if (playerData != null) {
            Calendar unbanTime = playerData.getUnbanTimeCalendar();
            Calendar now = Calendar.getInstance();

            if (unbanTime != null) {
                if (unbanTime.after(now)) {
                    return true;
                } else {
                    unbanPlayer(playerUUID);
                }
            }
        }

        return false;
    }
}

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

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.tasks.KickRunnable;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bans is a class used to store and modify the ban length of each player.
 *
 * @author mstiles92
 */
public class Bans {
    public Set<String> deathClasses;

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

        this.deathClasses = plugin.getConfig().getConfigurationSection("Death-Classes").getKeys(false);
        if (this.deathClasses.size() == 0) {
            plugin.log("No death classes found.");
        } else {
            for (String s : this.deathClasses) {
                plugin.log("Death class loaded: " + s);
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
            plugin.getLogger().warning(ChatColor.RED + "Error occurred while saving bans config file.");
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
        plugin.log("Player unbanned: " + player);
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
            for (String s : this.deathClasses) {
                Permission perm = new Permission("deathban.class." + s);
                perm.setDefault(PermissionDefault.FALSE);
                if (p.hasPermission(perm)) {
                    banPlayer(player, plugin.getConfig().getString("Death-Classes." + s + ".Ban-Time"));
                    plugin.log("Death class " + s + " detected for " + player);
                    return;
                }
            }
        }
        plugin.log("No death class detected for " + player);
        banPlayer(player, plugin.getConfig().getString("Ban-Time"));
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
            final Calendar unbanDate = parseBanTime(time);

            if (p != null) {            // Player is online
                if (!p.hasPermission("deathban.ban.exempt")) {
                    config.set(player.toLowerCase(), unbanDate.getTimeInMillis());
                    save();
                    plugin.log("Player added to ban list: " + player);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new KickRunnable(plugin, player), plugin.getConfig().getInt("Tick-Delay"));
                }
            } else {                    // Player is offline
                config.set(player.toLowerCase(), unbanDate.getTimeInMillis());
                save();
                plugin.log("Offline player added to ban list: " + player);
            }
        } catch (Exception e) {
            plugin.log("Error occurred while banning player: " + player);
        }
    }

    /**
     * Parse the ban time from the given string.
     *
     * @param banString the String representing the amount of ban time to be parsed
     * @return a Calendar object representing the current date and time, plus
     * the time parsed from the ban string
     * @throws Exception if the ban string is unable to be parsed
     */
    public static Calendar parseBanTime(String banString) throws Exception {
        final Pattern p = Pattern.compile(
                "(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?"
                        + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
        int years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
        boolean match = false;
        Matcher m = p.matcher(banString);

        while (m.find()) {
            if (m.group() == null || m.group().isEmpty()) {
                continue;
            }
            for (int i = 0; i < m.groupCount(); i++) {
                if (m.group(i) != null && !m.group(i).isEmpty()) {
                    match = true;
                    break;
                }
            }

            if (match) {
                if (m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }
                if (m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }
                if (m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }
                if (m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }
                if (m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }
                if (m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }
                if (m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }
        if (!match) {
            throw new Exception("Unable to parse time string.");
        }

        Calendar c = new GregorianCalendar();

        if (years > 0) c.add(Calendar.YEAR, years);
        if (months > 0) c.add(Calendar.MONTH, months);
        if (weeks > 0) c.add(Calendar.WEEK_OF_YEAR, weeks);
        if (days > 0) c.add(Calendar.DAY_OF_YEAR, days);
        if (hours > 0) c.add(Calendar.HOUR_OF_DAY, hours);
        if (minutes > 0) c.add(Calendar.MINUTE, minutes);
        if (seconds > 0) c.add(Calendar.SECOND, seconds);

        return c;
    }

    /**
     * Build a string representation of the time between now and the given
     * milliseconds since the epoch.
     *
     * @param millis the milliseconds since the epoch to compare the current time to
     * @return a String representation of the time difference
     */
    public static String buildTimeDifference(long millis) {
        final Calendar now = new GregorianCalendar();
        Calendar then = new GregorianCalendar();
        then.setTimeInMillis(millis);
        return buildTimeDifference(now, then);
    }

    /**
     * Build a string representation of the time difference between two Calendar objects.
     *
     * @param first  the first Calendar to compare
     * @param second the second Calendar to compare
     * @return a String representation of the time difference
     */
    public static String buildTimeDifference(Calendar first, Calendar second) {
        if (first.equals(second)) {
            return "now";
        }
        StringBuilder s = new StringBuilder();
        final int[] calendarTypes = new int[]{
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH,
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                Calendar.SECOND};
        final String[] names = new String[]{"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};

        for (int i = 0; i < calendarTypes.length; i++) {
            final int difference = getTypeDifference(calendarTypes[i], first, second);
            if (difference > 0) {
                s.append(" ").append(difference).append(" ").append(names[i * 2 + (difference > 1 ? 1 : 0)]);
            }
        }

        if (s.length() == 0) {
            return "now";
        }
        return s.toString();
    }

    private static int getTypeDifference(int type, Calendar first, Calendar second) {
        int difference = 0;
        long save = first.getTimeInMillis();
        while (!first.after(second)) {
            save = first.getTimeInMillis();
            first.add(type, 1);
            difference++;
        }
        difference--;
        first.setTimeInMillis(save);
        return difference;
    }
}

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

import com.mstiles92.plugins.stileslib.calendar.CalendarUtils;
import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

/**
 * DeathClasses is a class used to store information about various Death
 * Classes that will be used to control the amount of time a player is
 * banned, how many Revival Credits they start out with, and any other various
 * attributes assigned to players by their death class.
 *
 * @author mstiles92
 */
public class DeathClasses {
    private final HardcoreDeathBan plugin;
    private File file;
    private YamlConfiguration config;
    private Set<String> classes;

    /**
     * The main constructor for this class.
     *
     * @param plugin   the instance of the plugin
     * @param filename the name of the file to save to disk
     * @throws Exception if there is an error while opening or creating the file
     */
    public DeathClasses(HardcoreDeathBan plugin, String filename) throws Exception {
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

        classes = config.getConfigurationSection("Classes").getKeys(false);
        plugin.log("Death classes loaded: " + classes.size());
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
     * Get the name of the death class the player belongs to.
     *
     * @param player the name of the player to check
     * @return the name of the death class if the player has one, null if they do not
     */
    public String getPlayerDeathClass(String player) {
        return config.getString("Players." + player.toLowerCase());
    }

    /**
     * Get the amount of time for the player to be banned upon death.
     *
     * @param player the player to check
     * @return a Calendar object representing the time that the player will
     * be unbanned, or null if they do not have a death class
     */
    public Calendar getPlayerUnbanTime(String player) {
        return getClassUnbanTime(getPlayerDeathClass(player));
    }

    /**
     * Get the amount of time a player would be banned if they have the
     * specified death class.
     *
     * @param deathClass the death class to look up
     * @return a Calendar object representing the time that a player would be
     * unbanned if they had this class, or null if the death class
     * could not be found, or the ban time was not able to be parsed
     */
    public Calendar getClassUnbanTime(String deathClass) {
        if (deathClass == null) {
            return null;
        }
        final String unbanTime = config.getString("Classes." + deathClass + ".Ban-Time");
        if (unbanTime == null) {
            return null;
        }
        try {
            return CalendarUtils.parseTimeDifference(unbanTime);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the amount of starting credits a player will recieve.
     *
     * @param player the player to check for
     * @return the number of credits the player will recieve, or -1 if their
     * class could not be found
     */
    public int getPlayerStartingCredits(String player) {
        return getClassStartingCredits(getPlayerDeathClass(player));
    }

    /**
     * Get the amount of starting credits a player would recieve if they have
     * the specified death class.
     *
     * @param deathClass the death class to look up
     * @return the number of starting credits the player would recieve, or -1
     * if their class could not be found
     */
    public int getClassStartingCredits(String deathClass) {
        if (deathClass == null) {
            return -1;
        }
        return config.getInt("Classes." + deathClass + ".Starting-Credits", -1);
    }
}

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

package com.mstiles92.plugins.hardcoredeathban.config;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.data.DeathClass;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private FileConfiguration config;
    private boolean enabled;
    private String banTime;
    private String deathMessage;
    private String earlyMessage;
    private int tickDelay;
    private int startingCredits;
    private boolean verboseLoggingEnabled;
    private boolean updateCheckingEnabled;
    private List<DeathClass> deathClasses = new ArrayList<>();

    public Config() {
        HardcoreDeathBan.getInstance().saveDefaultConfig();
        config = HardcoreDeathBan.getInstance().getConfig();

        load();
    }

    public void load() {
        enabled = config.getBoolean("Enabled", true);
        banTime = config.getString("Ban-Time", "12h");
        deathMessage = config.getString("Death-Message", "You have died! You are now banned for %bantimeleft%.");
        earlyMessage = config.getString("Early-Message", "Your ban is not up for another %bantimeleft%.");
        tickDelay = config.getInt("Tick-Delay", 15);
        startingCredits = config.getInt("Starting-Credits", 0);
        verboseLoggingEnabled = config.getBoolean("Verbose", false);
        updateCheckingEnabled = config.getBoolean("Check-for-Updates", true);

        ConfigurationSection section = config.getConfigurationSection("Death-Classes");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection s = section.getConfigurationSection(key);
                deathClasses.add(new DeathClass(key, s.getString("Ban-Time"), s.getString("Death-Message")));
            }
        }
    }

    public void save() {
        config.set("Enabled", enabled);

        HardcoreDeathBan.getInstance().saveConfig();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getBanTime() {
        return banTime;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public String getEarlyMessage() {
        return earlyMessage;
    }

    public int getTickDelay() {
        return tickDelay;
    }

    public int getStartingCredits() {
        return startingCredits;
    }

    public boolean shouldLogVerbose() {
        return verboseLoggingEnabled;
    }

    public boolean shouldCheckForUpdates() {
        return updateCheckingEnabled;
    }

    public List<DeathClass> getDeathClasses() {
        return deathClasses;
    }
}

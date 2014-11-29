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
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Set;

public class Config {
    private FileConfiguration config;

    public Config() {
        HardcoreDeathBan.getInstance().saveDefaultConfig();
        config = HardcoreDeathBan.getInstance().getConfig();
    }

    public boolean isEnabled() {
        return config.getBoolean("Enabled", true);
    }

    public void setEnabled(boolean enabled) {
        config.set("Enabled", enabled);
        HardcoreDeathBan.getInstance().saveConfig();
    }

    public String getBanTime() {
        return config.getString("Ban-Time", "12h");
    }

    public String getDeathMessage() {
        return config.getString("Death-Message", "You have died! You are now banned for %bantimeleft%.");
    }

    public String getEarlyMessage() {
        return config.getString("Early-Message", "Your ban is not up for another %bantimeleft%.");
    }

    public int getTickDelay() {
        return config.getInt("Tick-Delay", 15);
    }

    public int getStartingCredits() {
        return config.getInt("Starting-Credits", 0);
    }

    public boolean shouldLogVerbose() {
        return config.getBoolean("Verbose", false);
    }

    public boolean shouldCheckForUpdates() {
        return config.getBoolean("Check-for-Updates", true);
    }

    public Set<String> getDeathClasses() {
        return config.getConfigurationSection("Classes").getKeys(false);
    }

    public String getDeathClassBanTime(String deathClass) {
        return config.getString("Death-Classes." + deathClass + ".Ban-Time");
    }

    public String getDeathClassDeathMessage(String deathClass) {
        return config.getString("Death-Classes." + deathClass + ".Death-Message");
    }
}

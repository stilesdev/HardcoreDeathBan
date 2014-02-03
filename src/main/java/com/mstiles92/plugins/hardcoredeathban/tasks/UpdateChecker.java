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

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * UpdateChecker is a class that checks the current version of the plugin
 * against the latest available version. If there is an update available,
 * it sets the plugin to notify staff of the update, as well as the changes
 * in the new version.
 * 
 * @author mstiles92
 */
public class UpdateChecker implements Runnable {
    private static String latestVersionFound;

    @Override
    public void run() {
        if (HardcoreDeathBan.getInstance().getDescription().getVersion().contains("SNAPSHOT")) {
            return;
        }

        try {
            URLConnection connection = new URL("http://api.bukget.org/3/plugins/bukkit/hardcoredeathban/latest").openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            JSONObject root = (JSONObject) new JSONParser().parse(new InputStreamReader(connection.getInputStream()));
            latestVersionFound = getVersion(root);
        } catch (IOException e) {
            HardcoreDeathBan.getInstance().getLogger().info("Error checking for update - BukGet may be experiencing issues. Will try again later.");
        } catch (ParseException e) {
            HardcoreDeathBan.getInstance().getLogger().warning("Error parsing json from BukGet. Please open a bug report at https://github.com/mstiles92/HardcoreDeathBan/issues");
        }
    }

    private String getVersion(JSONObject root) {
        JSONArray versions = (JSONArray) root.get("versions");
        JSONObject latestVersion = (JSONObject) versions.get(0);
        return (String) latestVersion.get("dbo_version");
    }

    public static String getLatestVersionFound() {
        return latestVersionFound;
    }

    public static boolean isUpdateAvailable() {
        String currentVersion = HardcoreDeathBan.getInstance().getDescription().getVersion();

        if (currentVersion.contains("SNAPSHOT")) {
            return false;
        }

        if (latestVersionFound == null) {
            return false;
        }

        return latestVersionFound != currentVersion; //TODO: Better to check that latestVersionFound > currentVersion
    }
}

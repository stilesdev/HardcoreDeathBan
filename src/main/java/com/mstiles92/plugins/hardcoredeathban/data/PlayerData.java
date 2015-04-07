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

package com.mstiles92.plugins.hardcoredeathban.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.stream.JsonGenerator;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.util.Log;

public class PlayerData {
    private static File file;
    private static Map<UUID, PlayerData> instances = new HashMap<>();

	private transient UUID uuid;
    private String lastSeenName;
    private long unbanTimeInMillis;
    private int revivalCredits;

    public static void init(File jsonFile) {
        file = jsonFile;
        try {
            if (file.exists()) {
                if (file.length() > 0) {
                    load();
                }
            } else {
                if (!file.createNewFile()) {
                    throw new IOException("Unable to create the new JSON file.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void load() {
        try {
            JsonReader reader = Json.createReader(new FileInputStream(file));
            JsonObject jsonRoot = reader.readObject();
            reader.close();

            deserialize(jsonRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put(JsonGenerator.PRETTY_PRINTING, true);

            JsonObject serialized = serialize();
            JsonWriter writer = Json.createWriterFactory(config).createWriter(new FileOutputStream(file));
            writer.write(serialized);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static void startAutosaveTask() {
		final int delay = HardcoreDeathBan.getConfigObject().playerDataAutosaveSeconds() * 20;
		new BukkitRunnable() {
			@Override
			public void run() {
				save();
				Log.info("Player data has been automatically saved.");
			}
		}.runTaskTimer(HardcoreDeathBan.getInstance(), delay, delay);
	}

    private static void deserialize(JsonObject json) {
        instances.clear();

        for (Map.Entry<String, JsonValue> entry : json.entrySet()) {
        	UUID playerUuid = UUID.fromString(entry.getKey());
            instances.put(playerUuid, new PlayerData((JsonObject) entry.getValue(), playerUuid));
        }
    }

    private static JsonObject serialize() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (Map.Entry<UUID, PlayerData> entry : instances.entrySet()) {
            builder.add(entry.getKey().toString(), entry.getValue().toJsonObject());
        }
        return builder.build();
    }

    private PlayerData(JsonObject json, UUID playerUuid) {
    	this.uuid = playerUuid;
        lastSeenName = json.getString("lastSeenName");
        unbanTimeInMillis = json.getJsonNumber("unbanTimeInMillis").longValueExact();
        revivalCredits = json.getInt("revivalCredits");
    }

    private PlayerData(Player player) {
    	this.uuid = player.getUniqueId();
        lastSeenName = player.getName();
        unbanTimeInMillis = -1;
        revivalCredits = HardcoreDeathBan.getConfigObject().getStartingCredits(); //TODO: check for death classes as well
    }

    private JsonObject toJsonObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("lastSeenName", lastSeenName);
        builder.add("unbanTimeInMillis", new BigDecimal(unbanTimeInMillis));
        builder.add("revivalCredits", revivalCredits);
        return builder.build();
    }

    /**
     * Get or create a PlayerData object for the specified player.
     *
     * @param player the Player who the PlayerData object will represent
     * @return the PlayerData object corresponding to the Player, or a new one if they do not have one yet
     */
    public static PlayerData get(Player player) {
        if (instances.containsKey(player.getUniqueId())) {
            PlayerData data = instances.get(player.getUniqueId());
            data.lastSeenName = player.getName();
            return data;
        } else {
            return create(player);
        }
    }

    /**
     * Get the PlayerData object for the player with the specified UUID.
     *
     * @param playerUUID the UUID of the player who the PlayerData object will represent
     * @return the PlayerData object corresponding to the Player, or null if they do not have one yet
     */
    public static PlayerData get(UUID playerUUID) {
        return instances.get(playerUUID);
    }

    /**
     * Get the PlayerData object for the player with the specified name.
     *
     * @deprecated this is a slow lookup only to be used when both the player's UUID and Player object are not available
     * @param playerName the name of the player who the PlayerData object will represent
     * @return the PlayerData object corresponding to the Player, or null if one is not found for the given name
     */
    @Deprecated
    public static PlayerData get(String playerName) {
        for (PlayerData playerData : instances.values()) {
            if (playerData.getLastSeenName().equals(playerName)) {
                return playerData;
            }
        }

        return null;
    }

    private static PlayerData create(Player player) {
        PlayerData data = new PlayerData(player);
        instances.put(player.getUniqueId(), data);
        return data;
    }

    public String getLastSeenName() {
        return lastSeenName;
    }

    public void setLastSeenName(String lastSeenName) {
        this.lastSeenName = lastSeenName;
    }

    public long getUnbanTimeInMillis() {
        return unbanTimeInMillis;
    }

    public void setUnbanTimeInMillis(long unbanTimeInMillis) {
        this.unbanTimeInMillis = unbanTimeInMillis;
    }

    public Calendar getUnbanTimeCalendar() {
        if (unbanTimeInMillis < 0) {
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(unbanTimeInMillis);
        return calendar;
    }

    public int getRevivalCredits() {
        return revivalCredits;
    }

    public void setRevivalCredits(int amount) {
        revivalCredits = amount;
    }

    public void addRevivalCredits(int amount) {
        revivalCredits += amount;
    }

    public boolean removeRevivalCredits(int amount) {
        if (revivalCredits >= amount) {
            revivalCredits -= amount;
            return true;
        } else {
            return false;
        }
    }

	public UUID getPlayerUUID() {
		return this.uuid;
	}
}

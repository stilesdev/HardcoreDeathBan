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

package com.mstiles92.plugins.hardcoredeathban.tasks;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.data.DeathClass;
import com.mstiles92.plugins.hardcoredeathban.util.Log;
import com.mstiles92.plugins.hardcoredeathban.util.Utils;

/**
 * KickRunnable is a BukkitRunnable used to kick a Player after a short delay when getting banned after death.
 */
public class KickRunnable extends BukkitRunnable {
    private UUID playerUUID;

    public KickRunnable(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    @Override
    public void run() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            DeathClass deathClass = Utils.getDeathClass(player);
            String kickMessage = (deathClass == null) ? HardcoreDeathBan.getConfigObject().getDeathMessage() : deathClass.getDeathMessage();
            player.kickPlayer(Utils.replaceMessageVariables(kickMessage, player.getUniqueId()));
            Log.verbose("Player " + player.getName() + " kicked successfully.");
        }

    }
}

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

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class DeathClass {
    private String name;
    private String banTime;
    private String deathMessage;

    public DeathClass(String name, String banTime, String deathMessage) {
        this.name = name;
        this.banTime = banTime;
        this.deathMessage = deathMessage;
    }

    public String getName() {
        return name;
    }

    public String getBanTime() {
        return banTime;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public Permission getPermission() {
        Permission permission = new Permission("deathban.class." + name);
        permission.setDefault(PermissionDefault.FALSE);
        return permission;
    }
}

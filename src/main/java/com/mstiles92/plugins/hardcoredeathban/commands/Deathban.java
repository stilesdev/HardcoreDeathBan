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

package com.mstiles92.plugins.hardcoredeathban.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import com.mstiles92.plugins.hardcoredeathban.data.PlayerData;
import com.mstiles92.plugins.hardcoredeathban.util.Utils;
import com.mstiles92.plugins.stileslib.commands.Arguments;
import com.mstiles92.plugins.stileslib.commands.CommandHandler;
import com.mstiles92.plugins.stileslib.commands.annotations.Command;

/**
 * Deathban is the CommandExecutor that handles all commands dealing
 * with bans for this plugin.
 *
 * @author mstiles92
 */
public class Deathban implements CommandHandler {
    private final String tag = ChatColor.BLUE + "[HardcoreDeathBan] " + ChatColor.RESET;

    @Command(name = "deathban", aliases = {"db", "hdb"}, permission = "deathban.display")
    public void deathban(Arguments args) {
        args.getSender().sendMessage(ChatColor.GREEN + "====[HardcoreDeathBan Help]====");
        args.getSender().sendMessage(ChatColor.GREEN + "<x> " + ChatColor.DARK_GREEN + "specifies a required parameter, while " + ChatColor.GREEN + "[x] " + ChatColor.DARK_GREEN + "is an optional parameter.");
        args.getSender().sendMessage(ChatColor.GREEN + "hdb" + ChatColor.DARK_GREEN + " or " + ChatColor.GREEN + "db " + ChatColor.DARK_GREEN + "may be used in place of " + ChatColor.GREEN + "deathban" + ChatColor.DARK_GREEN + " in the commands below.");
        args.getSender().sendMessage(ChatColor.GREEN + "/deathban enable " + ChatColor.DARK_GREEN + "Enable the plugin server-wide.");
        args.getSender().sendMessage(ChatColor.GREEN + "/deathban disable " + ChatColor.DARK_GREEN + "Disable the plugin server-wide.");
        args.getSender().sendMessage(ChatColor.GREEN + "/deathban ban <player> [time] " + ChatColor.DARK_GREEN + "Manually ban a player. Uses default time value if none specified.");
        args.getSender().sendMessage(ChatColor.GREEN + "/deathban unban <player> " + ChatColor.DARK_GREEN + "Manually unban a banned player.");
        args.getSender().sendMessage(ChatColor.GREEN + "/deathban status <player> " + ChatColor.DARK_GREEN + "Check the ban status of a player.");
        args.getSender().sendMessage(ChatColor.GREEN + "/credits [player] " + ChatColor.DARK_GREEN + "Check your own or another player's revival credits.");
        args.getSender().sendMessage(ChatColor.GREEN + "/credits send <player> <amount> " + ChatColor.DARK_GREEN + "Send some of your own revival credits to another player.");
        args.getSender().sendMessage(ChatColor.GREEN + "/credits give <player> <amount> " + ChatColor.DARK_GREEN + "Give a player a certain amount of revival credits.");
        args.getSender().sendMessage(ChatColor.GREEN + "/credits take <player> <amount> " + ChatColor.DARK_GREEN + "Take a certain amount of credits from another player.");
    }

    @Command(name = "deathban.enable", aliases = {"db.enable", "hdb.enable"}, permission = "deathban.enable")
    public void enable(Arguments args) {
        HardcoreDeathBan.getConfigObject().setEnabled(true);
        args.getSender().sendMessage(tag + "Enabled!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Utils.checkPlayerBanned(player.getUniqueId())) {
                player.kickPlayer(Utils.replaceMessageVariables(HardcoreDeathBan.getConfigObject().getEarlyMessage(), player.getUniqueId()));
            }
        }
    }

    @Command(name = "deathban.disable", aliases = {"db.disable", "hdb.disable"}, permission = "deathban.enable")
    public void disable(Arguments args) {
        HardcoreDeathBan.getConfigObject().setEnabled(false);
        args.getSender().sendMessage(tag + "Disabled!");
    }

    @Command(name = "deathban.ban", aliases = {"db.ban", "hdb.ban"}, permission = "deathban.ban")
    public void ban(Arguments args) {
        if (args.getArgs().length < 1) {
            args.getSender().sendMessage(tag + ChatColor.RED + "You must specify a player.");
            return;
        }

        Player player = Bukkit.getPlayer(args.getArgs()[0]);

        if (player == null) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The specified player could not be found.");
            return;
        }

        if (player.hasPermission("deathban.ban.exempt")) {
            args.getSender().sendMessage(tag + ChatColor.RED + "This player can not be banned!");
            return;
        }

        if (args.getArgs().length < 2) {
            Utils.banPlayer(player);
        } else {
            Utils.banPlayer(player, args.getArgs()[1]);
        }

        args.getSender().sendMessage(tag + Utils.replaceMessageVariables("%player% is now banned until %unbantime% %unbandate%", player.getUniqueId()));
    }

    @Command(name = "deathban.unban", aliases = {"db.unban", "hdb.unban"}, permission = "deathban.unban")
    public void unban(Arguments args) {
        if (args.getArgs().length < 1) {
            args.getSender().sendMessage(tag + ChatColor.RED + "You must specify a player.");
            return;
        }

        PlayerData playerData = PlayerData.get(args.getArgs()[0]);

        if (playerData == null) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The specified player could not be found.");
            return;
        }

        if (Utils.checkPlayerBanned(playerData.getPlayerUUID())) {
            Utils.unbanPlayer(playerData.getPlayerUUID());
            args.getSender().sendMessage(tag + playerData.getLastSeenName() + " has been unbanned.");
        } else {
            args.getSender().sendMessage(tag + playerData.getLastSeenName() + " is not currently banned.");
        }
    }

    @Command(name = "deathban.status", aliases = {"db.status", "hdb.status"}, permission = "deathban.status")
    public void status(Arguments args) {
        if (args.getArgs().length < 1) {
            args.getSender().sendMessage(tag + ChatColor.RED + "You must specify a player.");
            return;
        }

        PlayerData playerData = PlayerData.get(args.getArgs()[0]);

        if (playerData == null) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The specified player could not be found.");
            return;
        }

        if (Utils.checkPlayerBanned(playerData.getPlayerUUID())) {
            args.getSender().sendMessage(tag + Utils.replaceMessageVariables("%player% is banned until %unbantime% %unbandate%", playerData.getPlayerUUID()));
        } else {
            args.getSender().sendMessage(tag + playerData.getLastSeenName() + " is not currently banned.");
        }
    }
}

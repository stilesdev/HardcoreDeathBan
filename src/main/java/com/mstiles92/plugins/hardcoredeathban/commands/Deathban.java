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

package com.mstiles92.plugins.hardcoredeathban.commands;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Deathban is the CommandExecutor that handles all commands dealing
 * with bans for this plugin.
 *
 * @author mstiles92
 */
public class Deathban implements CommandExecutor {
    private final HardcoreDeathBan plugin;
    private final String tag = ChatColor.GREEN + "[HardcoreDeathBan] ";
    private final String perm = ChatColor.DARK_RED + "You do not have permission to perform this command.";

    /**
     * The main constructor for this class.
     *
     * @param plugin the instance of the plugin
     */
    public Deathban(HardcoreDeathBan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            if (cs.hasPermission("deathban.display")) {
                plugin.log("[" + cs.getName() + "] Player command: /deathban");

                cs.sendMessage(ChatColor.GREEN + "====[HardcoreDeathBan Help]====");
                cs.sendMessage(ChatColor.GREEN + "<x> " + ChatColor.DARK_GREEN + "specifies a required parameter, while " + ChatColor.GREEN + "[x] " + ChatColor.DARK_GREEN + "is an optional parameter.");
                cs.sendMessage(ChatColor.GREEN + "hdb" + ChatColor.DARK_GREEN + " or " + ChatColor.GREEN + "db " + ChatColor.DARK_GREEN + "may be used in place of " + ChatColor.GREEN + "deathban" + ChatColor.DARK_GREEN + " in the commands below.");
                cs.sendMessage(ChatColor.GREEN + "/deathban enable " + ChatColor.DARK_GREEN + "Enable the plugin server-wide.");
                cs.sendMessage(ChatColor.GREEN + "/deathban disable " + ChatColor.DARK_GREEN + "Disable the plugin server-wide.");
                cs.sendMessage(ChatColor.GREEN + "/deathban ban <player> [time] " + ChatColor.DARK_GREEN + "Manually ban a player. Uses default time value if none specified.");
                cs.sendMessage(ChatColor.GREEN + "/deathban unban <player> " + ChatColor.DARK_GREEN + "Manually unban a banned player.");
                cs.sendMessage(ChatColor.GREEN + "/deathban status <player> " + ChatColor.DARK_GREEN + "Check the ban status of a player.");
                cs.sendMessage(ChatColor.GREEN + "/credits [player] " + ChatColor.DARK_GREEN + "Check your own or another player's revival credits.");
                cs.sendMessage(ChatColor.GREEN + "/credits send <player> <amount> " + ChatColor.DARK_GREEN + "Send some of your own revival credits to another player.");
                cs.sendMessage(ChatColor.GREEN + "/credits give <player> <amount> " + ChatColor.DARK_GREEN + "Give a player a certain amount of revival credits.");
                cs.sendMessage(ChatColor.GREEN + "/credits take <player> <amount> " + ChatColor.DARK_GREEN + "Take a certain amount of credits from another player.");
            } else {
                cs.sendMessage(perm);
                plugin.log("Player " + cs.getName() + " denied access to command: /deathban");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("enable")) {
            if (cs.hasPermission("deathban.enable")) {
                plugin.log("[" + cs.getName() + "] Player command: /deathban enable");
                plugin.getConfig().set("Enabled", true);
                plugin.saveConfig();
                cs.sendMessage(tag + "Enabled!");

                Player[] plist = plugin.getServer().getOnlinePlayers();
                for (Player p : plist) {
                    if (plugin.bans.checkPlayerIsBanned(p.getName())) {
                        p.kickPlayer(plugin.replaceVariables(plugin.getConfig().getString("Banned-Message"), p.getName()));
                    }
                }
            } else {
                cs.sendMessage(perm);
                plugin.log("Player " + cs.getName() + " denied access to command: /deathban enable");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("disable")) {
            if (cs.hasPermission("deathban.enable")) {
                plugin.log("[" + cs.getName() + "] Player command: /deathban disable");
                plugin.getConfig().set("Enabled", false);
                plugin.saveConfig();
                cs.sendMessage(tag + "Disabled!");
            } else {
                cs.sendMessage(perm);
                plugin.log("Player " + cs.getName() + " denied access to command: /deathban disable");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("ban")) {
            if (cs.hasPermission("deathban.ban")) {
                if (args.length < 2) {
                    cs.sendMessage(tag + ChatColor.RED + "You must specify a player.");
                    return true;
                }
                plugin.log("[" + cs.getName() + "] Player command: /deathban ban " + args[1]);
                Player p = plugin.getServer().getPlayerExact(args[1]);
                if (p != null) {
                    if (p.hasPermission("deathban.ban.exempt")) {
                        cs.sendMessage(tag + ChatColor.RED + "This player can not be banned!");
                        return true;
                    }
                }

                if (args.length == 3) {
                    plugin.bans.banPlayer(args[1], args[2]);
                } else {
                    plugin.bans.banPlayer(args[1]);
                }
                String s = "%player% is now banned until %unbantime% %unbandate%";
                cs.sendMessage(tag + plugin.replaceVariables(s, args[1]));
            } else {
                cs.sendMessage(perm);
                plugin.log("Player " + cs.getName() + " denied access to command: /deathban ban " + args[1]);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("unban")) {
            if (cs.hasPermission("deathban.unban")) {
                if (args.length < 2) {
                    cs.sendMessage(tag + ChatColor.RED + "You must specify a player.");
                    return true;
                }
                plugin.log("[" + cs.getName() + "] Player command: /deathban unban " + args[1]);
                if (plugin.bans.checkPlayerIsBanned(args[1])) {
                    plugin.bans.unbanPlayer(args[1]);
                    cs.sendMessage(tag + args[1] + " has been unbanned.");
                } else {
                    cs.sendMessage(tag + args[1] + " is not currently banned.");
                }
            } else {
                cs.sendMessage(perm);
                plugin.log("Player " + cs.getName() + " denied access to command: /deathban unban " + args[1]);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            if (cs.hasPermission("deathban.status")) {
                if (args.length < 2) {
                    cs.sendMessage(tag + ChatColor.RED + "You must specify a player.");
                    return true;
                }
                plugin.log("[" + cs.getName() + "] Player command: /deathban status " + args[1]);
                if (plugin.bans.checkPlayerIsBanned(args[1])) {
                    String s = "%player% is banned until %unbantime% %unbandate%";
                    cs.sendMessage(tag + plugin.replaceVariables(s, args[1]));
                } else {
                    cs.sendMessage(tag + args[1] + " is not currently banned.");
                }
            } else {
                cs.sendMessage(perm);
                plugin.log("Player " + cs.getName() + " denied access to command: /deathban status " + args[1]);
            }
            return true;
        }
        return false;
    }
}

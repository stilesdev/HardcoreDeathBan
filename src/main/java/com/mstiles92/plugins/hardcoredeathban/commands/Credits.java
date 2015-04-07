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

import org.bukkit.ChatColor;

import com.mstiles92.plugins.hardcoredeathban.data.PlayerData;
import com.mstiles92.plugins.stileslib.commands.Arguments;
import com.mstiles92.plugins.stileslib.commands.CommandHandler;
import com.mstiles92.plugins.stileslib.commands.annotations.Command;

/**
 * Credits is the CommandExecutor that handles all commands dealing
 * with credits for this plugin.
 *
 * @author mstiles92
 */
public class Credits implements CommandHandler {
    private final String tag = ChatColor.BLUE + "[HardcoreDeathBan] " + ChatColor.RESET;

    @Command(name = "credits", aliases = {"cr"}, permission = "deathban.credits.check")
    public void credit(Arguments args) {
        if (args.getArgs().length < 1) {
            if (args.isPlayer()) {
                args.getPlayer().sendMessage(tag + "Revival credits: " + PlayerData.get(args.getPlayer()).getRevivalCredits());
            } else {
                args.getSender().sendMessage(tag + ChatColor.RED + "This command can not be run from the console unless a player is specified.");
            }
        } else {
            if (args.getSender().hasPermission("deathban.credits.check.others")) {
                PlayerData otherPlayerData = PlayerData.get(args.getArgs()[0]);

                if (otherPlayerData == null) {
                    args.getSender().sendMessage(tag + ChatColor.RED + "The specified player could not be found.");
                } else {
                    args.getSender().sendMessage(tag + otherPlayerData.getLastSeenName() + "'s revival credits: " + otherPlayerData.getRevivalCredits());
                }
            } else {
                args.getSender().sendMessage(ChatColor.RED + "You do not have permission to perform this command.");
            }
        }
    }

    @Command(name = "credits.send", aliases = {"cr.send"}, permission = "deathban.credits.send", playerOnly = true)
    public void send(Arguments args) {
        if (args.getArgs().length < 2) {
            args.getPlayer().sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to send that player.");
            return;
        }

        int creditsArg = tryParseInt(args.getArgs()[1]);

        if (creditsArg < 1) {
            args.getPlayer().sendMessage(tag + ChatColor.RED + "The amount of credits specified must be a positive integer value.");
            return;
        }

        PlayerData playerData = PlayerData.get(args.getPlayer());
        PlayerData otherPlayerData = PlayerData.get(args.getArgs()[0]);

        if (otherPlayerData == null) {
            args.getPlayer().sendMessage(tag + ChatColor.RED + "The specified player could not be found.");
        } else {
            if (playerData.removeRevivalCredits(creditsArg)) {
                otherPlayerData.addRevivalCredits(creditsArg);
                args.getPlayer().sendMessage(tag + "You have successfully sent " + otherPlayerData.getLastSeenName() + " " + creditsArg + " revival credits.");
            } else {
                args.getPlayer().sendMessage(tag + ChatColor.RED + "You do not have enough revival credits.");
            }
        }
    }

    @Command(name = "credits.give", aliases = {"cr.give"}, permission = "deathban.credits.give")
    public void give(Arguments args) {
        if (args.getArgs().length < 2) {
            args.getSender().sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to give that player.");
            return;
        }

        int creditsArg = tryParseInt(args.getArgs()[1]);

        if (creditsArg < 1) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The amount of credits specified must be a positive integer value.");
            return;
        }

        PlayerData otherPlayerData = PlayerData.get(args.getArgs()[0]);

        if (otherPlayerData == null) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The specified player could not be found.");
        } else {
            otherPlayerData.addRevivalCredits(creditsArg);
            args.getSender().sendMessage(tag + "You have successfully given " + otherPlayerData.getLastSeenName() + " " + creditsArg + " revival credits.");
        }
    }

    @Command(name = "credits.take", aliases = {"cr.take"}, permission = "deathban.credits.take")
    public void take(Arguments args) {
        if (args.getArgs().length < 2) {
            args.getSender().sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to take from that player.");
            return;
        }

        int creditsArg = tryParseInt(args.getArgs()[1]);

        if (creditsArg < 1) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The amount of credits specified must be a positive integer value.");
            return;
        }

        PlayerData otherPlayerData = PlayerData.get(args.getArgs()[0]);

        if (otherPlayerData == null) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The specified player could not be found.");
        } else {
            if (otherPlayerData.removeRevivalCredits(creditsArg)) {
                args.getSender().sendMessage(tag + "You have successfully taken " + creditsArg + " revival credits from " + otherPlayerData.getLastSeenName() + ".");
            } else {
                args.getSender().sendMessage(tag + ChatColor.RED + "Player " + otherPlayerData.getLastSeenName() + " only has " + otherPlayerData.getRevivalCredits() + " revival credits remaining.");
            }
        }
    }

    /**
     * Attempt to parse a String into an int value.
     *
     * @param toParse the String to parse
     * @return the parsed int value of the String, or -1 if the String does not represent a parsable int value
     */
    private int tryParseInt(String toParse) {
        try {
            return Integer.parseInt(toParse);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

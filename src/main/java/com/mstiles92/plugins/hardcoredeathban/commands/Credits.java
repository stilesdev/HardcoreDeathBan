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

import com.mstiles92.plugins.commonutils.commands.Arguments;
import com.mstiles92.plugins.commonutils.commands.CommandHandler;
import com.mstiles92.plugins.commonutils.commands.annotations.Command;
import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBan;
import org.bukkit.ChatColor;

/**
 * Credits is the CommandExecutor that handles all commands dealing
 * with credits for this plugin.
 *
 * @author mstiles92
 */
public class Credits implements CommandHandler {
    private final HardcoreDeathBan plugin;
    private final String tag = ChatColor.GREEN + "[HardcoreDeathBan] ";

    /**
     * The main constructor for this class.
     */
    public Credits() {
        this.plugin = HardcoreDeathBan.getInstance();
    }


    @Command(name = "credits", aliases = {"cr"}, permission = "deathban.credits.check")
    public void credit(Arguments args) {
        if (args.getArgs().length < 1) {
            if (args.isPlayer()) {
                args.getPlayer().sendMessage(tag + "Revival credits: " + plugin.credits.getPlayerCredits(args.getPlayer().getName()));
            } else {
                args.getSender().sendMessage(tag + ChatColor.RED + "This command can not be run from the console unless a player is specified.");
            }
        } else {
            if (args.getSender().hasPermission("deathban.credits.check.others")) {
                args.getSender().sendMessage(tag + args.getArgs()[0] + "'s revival credits: " + plugin.credits.getPlayerCredits(args.getArgs()[0]));
            } else {
                args.getSender().sendMessage(ChatColor.RED + "You do not have permission to perform this command.");
            }
        }
    }

    @Command(name = "credits.send", aliases = {"cr.send"}, permission = "deathban.credits.send", playerOnly = true)
    public void send(Arguments args) {
        if (args.getArgs().length < 3) {
            args.getPlayer().sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to send that player.");
            return;
        }

        try {
            if (Integer.parseInt(args.getArgs()[1]) < 1) throw new NumberFormatException();
            if (plugin.credits.getPlayerCredits(args.getPlayer().getName()) >= Integer.parseInt(args.getArgs()[1])) {
                plugin.credits.givePlayerCredits(args.getPlayer().getName(), Integer.parseInt(args.getArgs()[1]) * -1);
                plugin.credits.givePlayerCredits(args.getArgs()[0], Integer.parseInt(args.getArgs()[1]));
                args.getPlayer().sendMessage(tag + "You have successfully sent " + args.getArgs()[0] + " " + args.getArgs()[1] + " revival credits.");
            } else {
                args.getPlayer().sendMessage(tag + ChatColor.RED + "You do not have enough revival credits.");
            }
        } catch (NumberFormatException e) {
            args.getPlayer().sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
        }
    }

    @Command(name = "credits.give", aliases = {"cr.give"}, permission = "deathban.credits.give")
    public void give(Arguments args) {
        if (args.getArgs().length < 2) {
            args.getSender().sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to give that player.");
            return;
        }

        try {
            if (Integer.parseInt(args.getArgs()[1]) < 1) throw new NumberFormatException();
            plugin.credits.givePlayerCredits(args.getArgs()[0], Integer.parseInt(args.getArgs()[1]));
            args.getSender().sendMessage(tag + "You have successfully given " + args.getArgs()[0] + " " + args.getArgs()[1] + " revival credits.");
        } catch (NumberFormatException e) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
        }
    }

    @Command(name = "credits.take", aliases = {"cr.take"}, permission = "deathban.credits.take")
    public void take(Arguments args) {
        if (args.getArgs().length < 2) {
            args.getSender().sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to give that player.");
            return;
        }

        try {
            if (Integer.parseInt(args.getArgs()[1]) < 1) throw new NumberFormatException();
            plugin.credits.givePlayerCredits(args.getArgs()[0], Integer.parseInt(args.getArgs()[1]) * -1);
            args.getSender().sendMessage(tag + "You have successfully taken " + args.getArgs()[1] + " revival credits from " + args.getArgs()[0] + ".");
        } catch (NumberFormatException e) {
            args.getSender().sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
        }
    }
}

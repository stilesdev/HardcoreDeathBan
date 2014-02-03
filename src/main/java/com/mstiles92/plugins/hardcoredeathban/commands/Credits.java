/**
 * This document is a part of the source code and related artifacts for
 * HardcoreDeathBan, an open source Bukkit plugin for hardcore-type servers
 * where players are temporarily banned upon death.
 *
 * http://dev.bukkit.org/server-mods/plugins/
 * http://github.com/mstiles92/HardcoreDeathBan
 *
 * Copyright � 2013 Matthew Stiles (mstiles92)
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mstiles92.plugins.hardcoredeathban.HardcoreDeathBanPlugin;

/**
 * Credits is the CommandExecutor that handles all commands dealing
 * with credits for this plugin.
 * 
 * @author mstiles92
 */
public class Credits implements CommandExecutor {
	private final HardcoreDeathBanPlugin plugin;
	private final String tag = ChatColor.GREEN + "[HardcoreDeathBan] ";
	private final String perm = ChatColor.DARK_RED + "You do not have permission to perform this command.";
	
	/**
	 * The main constructor for this class.
	 * 
	 * @param plugin the instance of the plugin
	 */
	public Credits(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			if (cs instanceof Player) {
				if (cs.hasPermission("deathban.credits.check")) {
					plugin.log("[" + cs.getName() + "] Player command: /credits");
					cs.sendMessage(tag + "Revival credits: " + plugin.credits.getPlayerCredits(cs.getName()));
				} else {
					cs.sendMessage(perm);
					plugin.log("Player " + cs.getName() + " denied access to command: /credits");
				}
			} else {
				cs.sendMessage(tag + ChatColor.RED + "This command can not be run from the console unless a player is specified.");
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("send")) {
			if (cs.hasPermission("deathban.credits.send")) {
				if (args.length < 3) {
					cs.sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to send that player.");
					return true;
				}
				
				if (!(cs instanceof Player)) {
					cs.sendMessage(tag + ChatColor.RED + "This command can only be run as a player.");
					return true;
				}
				plugin.log("[" + cs.getName() + "] Player command: /credits send " + args[1] + args[2]);
				try {
					if (Integer.parseInt(args[2]) < 1) throw new NumberFormatException();
					if (plugin.credits.getPlayerCredits(cs.getName()) >= Integer.parseInt(args[2])) {
						plugin.credits.givePlayerCredits(cs.getName(), Integer.parseInt(args[2]) * -1);
						plugin.credits.givePlayerCredits(args[1], Integer.parseInt(args[2]));
						cs.sendMessage(tag + "You have successfully sent " + args[1] + " " + args[2] + " revival credits.");
					} else {
						cs.sendMessage(tag + ChatColor.RED + "You do not have enough revival credits.");
					}
				}
				catch (NumberFormatException e) {
					cs.sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
				}
			} else {
				cs.sendMessage(perm);
				plugin.log("Player " + cs.getName() + " denied access to command: /credits send " + args[1] + " " + args[2]);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("give")) {
			if (cs.hasPermission("deathban.credits.give")) {
				if (args.length < 3) {
					cs.sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to give that player.");
					return true;
				}
				plugin.log("[" + cs.getName() + "] Player command: /credits give " + args[1] + args[2]);
				try {
					if (Integer.parseInt(args[2]) < 1) throw new NumberFormatException();
					plugin.credits.givePlayerCredits(args[1], Integer.parseInt(args[2]));
					cs.sendMessage(tag + "You have successfully given " + args[1] + " " + args[2] + " revival credits.");
				}
				catch (NumberFormatException e) {
					cs.sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
				}
			} else {
				cs.sendMessage(perm);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("take")) {
			if (cs.hasPermission("deathban.credits.take")) {
				if (args.length < 3) {
					cs.sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to give that player.");
					return true;
				}
				plugin.log("[" + cs.getName() + "] Player command: /credits take " + args[1] + args[2]);
				try {
					if (Integer.parseInt(args[2]) < 1) throw new NumberFormatException();
					plugin.credits.givePlayerCredits(args[1], Integer.parseInt(args[2]) * -1);
					cs.sendMessage(tag + "You have successfully taken " + args[2] + " revival credits from " + args[1] + ".");
				}
				catch (NumberFormatException e) {
					cs.sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
				}
			} else {
				cs.sendMessage(perm);
				plugin.log("Player " + cs.getName() + " denied access to command: /credits give " + args[1] + " " + args[2]);
			}
			return true;
		}
		
		if (args.length == 1) {
			if (cs.hasPermission("deathban.credits.check.others")) {
				plugin.log("[" + cs.getName() + "] Player command: /credits " + args[0]);
				cs.sendMessage(tag + args[0] + "'s revival credits: " + plugin.credits.getPlayerCredits(args[0]));
			} else {
				cs.sendMessage(perm);
				plugin.log("Player " + cs.getName() + " denied access to command: /credits " + args[0]);
			}
			return true;
		}
		return true;
	}
}

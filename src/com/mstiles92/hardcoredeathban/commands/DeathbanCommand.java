package com.mstiles92.hardcoredeathban.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mstiles92.hardcoredeathban.HardcoreDeathBanPlugin;

public class DeathbanCommand implements CommandExecutor {
	
	private final HardcoreDeathBanPlugin plugin;
	private final String tag = ChatColor.GREEN + "[HardcoreDeathBan] ";
	private final String perm = ChatColor.DARK_RED + "You do not have permission to perform this command.";
	
	public DeathbanCommand(HardcoreDeathBanPlugin plugin) {
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
				plugin.config.set("Enabled", true);
				cs.sendMessage(tag + "Enabled!");
				
				Player[] plist = plugin.getServer().getOnlinePlayers();
				for (Player p : plist) {
					if (plugin.isBanned(p.getName())) {
						p.kickPlayer(plugin.replaceVariables(plugin.config.getString("Banned-Message"), p.getName()));
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
				plugin.config.set("Enabled", false);
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
				plugin.giveCredits(args[1], plugin.getCredits(args[1]) * -1);
				if (args.length == 3) {
					plugin.setBanned(args[1], args[2]);
				} else {
					plugin.setBanned(args[1]);
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
				if (plugin.isBanned(args[1])) {
					plugin.removeFromBan(args[1]);
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
				if (plugin.isBanned(args[1])) {
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

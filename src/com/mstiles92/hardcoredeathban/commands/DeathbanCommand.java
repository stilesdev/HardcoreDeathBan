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
				// TODO List all commands to player
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
				if (plugin.getServer().getPlayerExact(args[1]).hasPermission("deathban.ban.exempt")) {
					cs.sendMessage(tag + ChatColor.RED + "This player can not be banned!");
				} else {
					plugin.giveCredits(args[1], plugin.getCredits(args[1]) * -1);
					if (args.length == 3) {
						plugin.setBanned(args[1], args[2]);
					} else {
						plugin.setBanned(args[1]);
					}
					String s = "%player% is now banned until %unbantime% %unbandate%";
					cs.sendMessage(tag + plugin.replaceVariables(s, args[1]));
				}
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

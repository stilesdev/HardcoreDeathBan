package com.mstiles92.hardcoredeathban.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mstiles92.hardcoredeathban.HardcoreDeathBanPlugin;

public class DeathbanCommand implements CommandExecutor {
	
	private final HardcoreDeathBanPlugin plugin;
	private final String tag = ChatColor.GREEN + "[HardcoreDeathBan] ";
	
	public DeathbanCommand(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			// TODO List all commands to player
			
			return true;
		}
		
		if (args[0].equalsIgnoreCase("enable")) {
			plugin.enabled = true;
			cs.sendMessage(tag + "Enabled!");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("disable")) {
			plugin.enabled = false;
			cs.sendMessage(tag + "Disabled!");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("ban")) {
			if (args.length < 2) {
				cs.sendMessage(tag + ChatColor.DARK_RED + "You must specify a player.");
				return true;
			}
			
			if (args.length == 3) {
				plugin.setBanned(args[1], args[2]);
			} else {
				plugin.setBanned(args[1]);
			}
			String s = "%player% is now banned until %unbantime% %unbandate%";
			cs.sendMessage(tag + plugin.replaceVariables(s, args[1]));
			return true;
		}
		
		if (args[0].equalsIgnoreCase("unban")) {
			if (args.length < 2) {
				cs.sendMessage(tag + ChatColor.DARK_RED + "You must specify a player.");
				return true;
			}
			
			if (plugin.config.contains(args[1])) {
				plugin.config.set(args[1], null);
				cs.sendMessage(tag + args[1] + " has been unbanned.");
				plugin.saveConfig();
			} else {
				cs.sendMessage(tag + args[1] + " is not currently banned.");
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("status")) {
			if (args.length < 2) {
				cs.sendMessage(tag + ChatColor.DARK_RED + "You must specify a player.");
				return true;
			}
			
			if (plugin.isBanned(args[1])) {
				String s = "%player% is banned until %unbantime% %unbandate%";
				cs.sendMessage(tag + plugin.replaceVariables(s, args[1]));
			} else {
				cs.sendMessage(tag + args[1] + " is not currently banned.");
			}
			return true;
		}
		
		return false;
	}
	
	

}

package com.mstiles92.hardcoredeathban.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mstiles92.hardcoredeathban.HardcoreDeathBanPlugin;

public class CreditsCommand implements CommandExecutor {
	
	private final HardcoreDeathBanPlugin plugin;
	private final String tag = ChatColor.GREEN + "[HardcoreDeathBan] ";
	private final String perm = ChatColor.DARK_RED + "You do not have permission to perform this command.";
	
	public CreditsCommand(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			if (cs instanceof Player) {
				if (cs.hasPermission("deathban.credits.check")) {
					cs.sendMessage(tag + "Revival credits: " + plugin.getCredits(cs.getName()));
				} else {
					cs.sendMessage(perm);
				}
			} else {
				cs.sendMessage(tag + ChatColor.RED + "This command can not be run from the console unless a player is specified.");
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("send")) {
			if (args.length < 3) {
				cs.sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to send that player.");
				return true;
			}
			
			if (!(cs instanceof Player)) {
				cs.sendMessage(tag + ChatColor.RED + "This command can only be run as a player.");
				return true;
			}
			
			try {
				if (Integer.parseInt(args[2]) < 1) throw new NumberFormatException();
				if (plugin.getCredits(args[1]) >= Integer.parseInt(args[2])) {
					plugin.giveCredits(cs.getName(), Integer.parseInt(args[2]) * -1);
					plugin.giveCredits(args[1], Integer.parseInt(args[2]));
					cs.sendMessage(tag + "You have successfully sent " + args[1] + " " + args[2] + " revival credits.");
				} else {
					cs.sendMessage(tag + ChatColor.RED + "You do not have enough revival credits.");
				}
				return true;
			}
			catch (NumberFormatException e) {
				cs.sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
				return true;
			}
		}
		
		if (args[0].equalsIgnoreCase("give")) {
			if (args.length < 3) {
				cs.sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to give that player.");
				return true;
			}
			
			try {
				if (Integer.parseInt(args[2]) < 1) throw new NumberFormatException();
				plugin.giveCredits(args[1], Integer.parseInt(args[2]));
				cs.sendMessage(tag + "You have successfully given " + args[1] + " " + args[2] + " revival credits.");
				return true;
			}
			catch (NumberFormatException e) {
				cs.sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
				return true;
			}
		}
		
		if (args[0].equalsIgnoreCase("take")) {
			if (args.length < 3) {
				cs.sendMessage(tag + ChatColor.RED + "You must specify both a player and an amount to give that player.");
				return true;
			}
			
			try {
				if (Integer.parseInt(args[2]) < 1) throw new NumberFormatException();
				plugin.giveCredits(args[1], Integer.parseInt(args[2]) * -1);
				cs.sendMessage(tag + "You have successfully taken " + args[2] + " revival credits from " + args[1] + ".");
				return true;
			}
			catch (NumberFormatException e) {
				cs.sendMessage(tag + ChatColor.RED + "The amount must be specified as a positive integer value.");
				return true;
			}
		}
		
		if (args.length == 1) {
			if (cs.hasPermission("deathban.credits.check.others")) {
				cs.sendMessage(tag + args[0] + "'s revival credits: " + plugin.getCredits(args[0]));
			} else {
				cs.sendMessage(perm);
			}
			return true;
		}
		
		return true;
	}

}

package com.mstiles92.hardcoredeathban.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mstiles92.hardcoredeathban.HardcoreDeathBanPlugin;

public class CreditsCommand implements CommandExecutor {
	
	private final HardcoreDeathBanPlugin plugin;
	private final String tag = ChatColor.GREEN + "[HardcoreDeathBan] ";
	
	public CreditsCommand(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		if (args.length < 1) {
			
		}
		
		if (args[0].equalsIgnoreCase("send")) {
			
		}
		
		if (args[0].equalsIgnoreCase("give")) {
			
		}
		
		if (args[0].equalsIgnoreCase("take")) {
			
		}
		
		return false;
	}

}

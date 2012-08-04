package com.mstiles92.hardcoredeathban.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mstiles92.hardcoredeathban.HardcoreDeathBanPlugin;

public class CreditsCommand implements CommandExecutor {
	
	private final HardcoreDeathBanPlugin plugin;
	
	public CreditsCommand(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

}

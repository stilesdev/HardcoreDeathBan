package com.mstiles92.hardcoredeathban;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Commands implements CommandExecutor {
	
	private final HardcoreDeathBanPlugin plugin;
	
	public Commands(HardcoreDeathBanPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}

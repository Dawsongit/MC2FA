package com.connorlinfoot.mc2fa.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	private MC2FA mc2FA;

	public CommandHandler(MC2FA mc2FA) {
		this.mc2FA = mc2FA;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "This command must be ran as a player!");
			return false;
		}

		Player player = (Player) sender;
		if( mc2FA.getAuthHandler().isPending(player)) {
			if( args.length == 0 ) {
				player.sendMessage(ChatColor.RED + "Please validate your two-factor authentication key with /" + string + " <key>");
			} else {
				Integer key;
				try {
					key = Integer.valueOf(args[0]);
				} catch(Exception e) {
					player.sendMessage(ChatColor.RED + "Invalid key entered");
					return false;
				}

				boolean approved = mc2FA.getAuthHandler().approveKey(player, key);
				if( approved ) {
					player.sendMessage(ChatColor.GREEN + "You have successfully setup two-factor authentication :)");
				} else {
					player.sendMessage(ChatColor.RED + "The key you entered was not valid, please try again!");
				}
			}
		} else if( ! mc2FA.getAuthHandler().isEnabled(player)) {
			mc2FA.getAuthHandler().createKey(player);
			player.sendMessage(ChatColor.GREEN + "Please follow the link below to setup two-factor authentication");
			player.sendMessage(mc2FA.getAuthHandler().getQRCodeURL(player));
			player.sendMessage("");
			player.sendMessage(ChatColor.GREEN + "Please validate by entering your key: /" + string + " <key>");
		} else {
			if( args.length > 0 ) {
				boolean isValid = mc2FA.getAuthHandler().validateKey(player, Integer.valueOf(args[0]));
				player.sendMessage(String.valueOf(isValid));
			}
		}

		return false;
	}

}

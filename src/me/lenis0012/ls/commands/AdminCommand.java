package me.lenis0012.ls.commands;

import me.lenis0012.ls.LoginData;
import me.lenis0012.ls.ls;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor
{
	private ChatColor g = ChatColor.GREEN;
	private ChatColor i = ChatColor.GRAY;
	private ChatColor r = ChatColor.RED;
	private ls plugin;
	public AdminCommand(ls i) { plugin = i; }

	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Command can only be executed as player.");
			return true;
		}
		Player player = (Player)sender;
		
		if(!player.hasPermission("ls.admin"))
		{
			player.sendMessage(r+"No permission");
		}
		
		if(args.length == 0)
		{
			this.showInfo(player);
		}
		else if(args[0].equalsIgnoreCase("reload"))
		{
			this.reload(player);
		}
		else if(args[0].equalsIgnoreCase("changepass"))
		{
			this.changePass(player, args);
		}
		else
		{
			this.showInfo(player);
		}
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////
	//                                                                                    //
	//                                       Actions                                      //
	//                                                                                    //
	////////////////////////////////////////////////////////////////////////////////////////
	
	public void showInfo(Player player)
	{
		player.sendMessage(i+"==========-{ "+r+"L"+g+"oginSecurity "+r+"A"+g+"dmin "+r+"C"+g+"ommand "+i+"}-==========");
		player.sendMessage(g+"/lac changePass (Player) (Password)"+i+" - change a players pass");
		player.sendMessage(g+"/lac changePass reload"+i+" - ReloadConfig");
	}
	
	public void reload(Player player)
	{
		plugin.reloadConfig();
		player.sendMessage(g+"Reloaded config.yml");
		plugin.reloadCustomConfig();
		player.sendMessage(g+"Reloaded data.yml");
	}
	
	public void changePass(Player player, String[] args)
	{
		if(args.length < 3)
		{
			player.sendMessage(ChatColor.RED + "Invalid args");
			return;
		}
		LoginData.setPass(args[1], args[2], plugin, 1);
		player.sendMessage(g+"Changed password from "+args[1]+" to "+args[2]);
	}
}

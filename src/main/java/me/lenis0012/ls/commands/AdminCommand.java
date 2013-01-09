package me.lenis0012.ls.commands;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
			return true;
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
		else if(args[0].equalsIgnoreCase("setpass"))
		{
			this.setPass(player, args);
		}
		else if(args[0].equalsIgnoreCase("rmpass"))
		{
			this.rmPass(player, args);
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
		player.sendMessage(g+"/lac changePass (Player) (Password)"+i+" - Change a players pass");
		player.sendMessage(g+"/lac setPass (Player) (Password)"+i+" - Set a players password");
		player.sendMessage(g+"/lac rmPass (Player)"+i+" - Remove a players password");
		player.sendMessage(g+"/lac reload"+i+" - Reload the config");
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
		String pass = args[2];
		if(plugin.getConfig().getBoolean("options.use-MD5 Enryption"))
			pass = md5(pass);
		LoginData.setPass(args[1], pass, plugin, 1);
		player.sendMessage(g+"Changed password for "+args[1]+" to "+args[2]);
	}
	
	public void setPass(Player player, String[] args)
	{
		if(args.length < 3)
		{
			player.sendMessage(ChatColor.RED + "Invalid args");
			return;
		}
		String pass = args[2];
		if(plugin.getConfig().getBoolean("options.use-MD5 Enryption"))
			pass = md5(pass);
		LoginData.setPass(args[1], pass, plugin, 0);
		player.sendMessage(g+"Set password for "+args[1]+" to "+args[2]);
	}
	
	public void rmPass(Player player, String[] args)
	{
		if(args.length < 3)
		{
			player.sendMessage(ChatColor.RED + "Invalid args");
			return;
		}
		LoginData.delPass(args[1], plugin);
		player.sendMessage(g+"Removed password from "+args[1]);
	}
	
	public String md5(String str)
	{
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes(), 0, str.length());
			return new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {}
		return "";
	}
}

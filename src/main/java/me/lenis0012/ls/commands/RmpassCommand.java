package me.lenis0012.ls.commands;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.lenis0012.ls.LoginData;
import me.lenis0012.ls.Messages;
import me.lenis0012.ls.ls;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RmpassCommand implements CommandExecutor{
	public static ls plugin;
	public RmpassCommand(ls instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args){
		String pname = sender.getName();
		String password = LoginData.getPass(pname.toLowerCase(), plugin);
		if (args.length == 0){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(9, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if (args.length > 1){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(10, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if(plugin.getConfig().getBoolean("options.password-required") == true)
		{
			sender.sendMessage("[LoginSecurity] " + ChatColor.RED + Messages.getMessage(15, plugin));
			return true;
		}
		if(plugin.getConfig().getBoolean("options.use-MD5 Enryption") == true)
		{
			try{
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(args[0].getBytes(), 0, args[0].length());
				if(new BigInteger(1, md.digest()).toString(16).equals(password)){
					LoginData.delPass(pname.toLowerCase(), plugin);
					sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(13, plugin));
				}
				else
				{
					sender.sendMessage("[LoginSecurity] " + ChatColor.RED + Messages.getMessage(4, plugin));
					return true;
				}
				return true;
			} catch(NoSuchAlgorithmException e){
				e.printStackTrace();
			}
		}else
		{
			if(args[0].equals(password)){
				LoginData.delPass(pname.toLowerCase(), plugin);
				sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(13, plugin));
			}
			else
			{
				sender.sendMessage("[LoginSecurity] " + ChatColor.RED + Messages.getMessage(4, plugin));
				return true;
			}
		}
		return true;
	}
}

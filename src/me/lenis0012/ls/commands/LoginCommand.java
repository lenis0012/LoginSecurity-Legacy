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
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class LoginCommand implements CommandExecutor{
	public static ls plugin;
	public LoginCommand(ls instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args){
		String pname = sender.getName();
		String password = LoginData.getPass(pname.toLowerCase(), plugin);
		Player player = null;
	    if ((sender instanceof Player))
	    {
	      player = (Player)sender;
	    }
		if(args.length == 0){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(9, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if(args.length > 1){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(10, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if(plugin.getConfig().getBoolean("options.use-MD5 Enryption") == true)
		{
			try{
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(args[0].getBytes(), 0, args[0].length());
				if(new BigInteger(1, md.digest()).toString(16).equals(password)){
					plugin.invalid.remove(pname);
					sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(5, plugin));
					if(player.hasPotionEffect(PotionEffectType.BLINDNESS))
					{
						player.removePotionEffect(PotionEffectType.BLINDNESS);
					}
				}
				else
				{
					sender.sendMessage("[LoginSecurity] " + Messages.getMessage(4, plugin));	
					return true;
				}
			} catch(NoSuchAlgorithmException e){
				e.printStackTrace();
			}
		}else
		{
			if(args[0].equals(password)){
				plugin.invalid.remove(pname);
				sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(5, plugin));
				if(player.hasPotionEffect(PotionEffectType.BLINDNESS))
				{
					player.removePotionEffect(PotionEffectType.BLINDNESS);
				}
			}
			else
			{
				sender.sendMessage("[LoginSecurity] " + Messages.getMessage(4, plugin));	
				return true;
			}
		}
		return true;
	}
}

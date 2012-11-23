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

public class ChangePassCommand implements CommandExecutor{
	public static ls plugin;
	public ChangePassCommand(ls instance) { plugin = instance; }
	
	public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args){
		String pname = sender.getName();
		String password = LoginData.getPass(pname.toLowerCase(), plugin);
	    if (!(sender instanceof Player)) {
	        return true;
	    }
		if(args.length < 2){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(9, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if(args.length > 2){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(10, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if(args[1].length() < plugin.getConfig().getInt("options.min-length"))
		{
			String min = String.valueOf(plugin.getConfig().getInt("options.min-length"));
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(2, plugin).replace("{Min}", min));
			return true;
		}
		if(args[1].length() > plugin.getConfig().getInt("options.max-lenght"))
		{
			String max = String.valueOf(plugin.getConfig().getInt("options.max-lenght"));
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(3, plugin).replace("{Max}", max));
			return true;
		}
		if(plugin.getConfig().getBoolean("options.use-MD5 Enryption") == true)
		{
		try{
				MessageDigest md1 = MessageDigest.getInstance("MD5");
				MessageDigest md2 = MessageDigest.getInstance("MD5");
				md1.update(args[0].getBytes(), 0, args[0].length());
				md2.update(args[1].getBytes(), 0, args[1].length());
				if(new BigInteger(1, md1.digest()).toString(16).equals(password)){
					LoginData.setPass(pname.toLowerCase(), new BigInteger(1, md2.digest()).toString(16), plugin, 1);
					sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(12, plugin).replace("{Password}", args[1]));
				}
				else
				{
					sender.sendMessage("[LoginSecurity] " + ChatColor.BLUE + Messages.getMessage(4, plugin));	
					return true;
				}
			} catch(NoSuchAlgorithmException e){
				e.printStackTrace();
			}
		}else
		{
			if(args[0].equals(password))
			{
				LoginData.setPass(pname.toLowerCase(), args[1], plugin, 1);
				sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(12, plugin).replace("{Password}", args[1]));
			}else
			{
				sender.sendMessage("[LoginSecurity] " + ChatColor.BLUE + Messages.getMessage(4, plugin));	
				return true;
			}
		}
		return true;
	}
}

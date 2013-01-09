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

public class SetpassCommand implements CommandExecutor{
	public static ls plugin;
	public SetpassCommand(ls instance) { plugin = instance; }

	public boolean onCommand(CommandSender sender, Command cmnd, String label, String[] args){
		String pname = sender.getName();
	    if (!(sender instanceof Player)) {
	        return true;
	    }
	    if(LoginData.hasPass(pname.toLowerCase(), plugin))
		{
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(14, plugin));
			return true;
		}
		if (args.length == 0){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(9, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if(args.length > 1){
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(10, plugin));
			sender.sendMessage(cmnd.getUsage());
			return true;
		}
		if(args[0].length() < plugin.getConfig().getInt("options.min-length"))
		{
			String min = String.valueOf(plugin.getConfig().getInt("options.min-length"));
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(2, plugin).replace("{Min}", min));
			return true;
		}
		if(args[0].length() > plugin.getConfig().getInt("options.max-lenght"))
		{
			String max = String.valueOf(plugin.getConfig().getInt("options.max-lenght"));
			sender.sendMessage("[LoginSecurity] " +  ChatColor.RED + Messages.getMessage(3, plugin).replace("{Max}", max));
			return true;
		}
		if(plugin.getConfig().getBoolean("options.use-MD5 Enryption") == true)
		{
			try{
				MessageDigest md = MessageDigest.getInstance("MD5");
				md.update(args[0].getBytes(), 0, args[0].length());
				plugin.invalid.remove(pname);
				LoginData.setPass(pname.toLowerCase(), new BigInteger(1, md.digest()).toString(16), plugin, 0);
				sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(8, plugin).replace("{Password}", args[0]));
			} catch(NoSuchAlgorithmException e){
				e.printStackTrace();
			}
		}else
		{
			plugin.invalid.remove(pname);
			LoginData.setPass(pname.toLowerCase(), args[0], plugin, 0);
			sender.sendMessage("[LoginSecurity] " + ChatColor.GREEN + Messages.getMessage(8, plugin).replace("{Password}", args[0]));
		}
	    return true;
	}
}

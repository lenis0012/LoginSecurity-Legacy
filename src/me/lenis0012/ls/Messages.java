package me.lenis0012.ls;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.error.YAMLException;

public class Messages
{
	public static String getMessage(int s, ls plugin)
	{
		File file = new File(plugin.getDataFolder(), "Language.yml");
		
		if(file.exists())
		{
			try
			{
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
			
				switch(s)
				{
					case 1:
						return cfg.getString("No permissions", om(1));
						
					case 2:
						return cfg.getString("Password to short", om(2));
						
					case 3:
						return cfg.getString("Password to long", om(3));
						
					case 4:
						return cfg.getString("Invalid password", om(4));
						
					case 5:
						return cfg.getString("Login succesfull", om(5));
						
					case 6:
						return cfg.getString("Register", om(6));
						
					case 7:
						return cfg.getString("Login", om(7));
						
					case 8:
						return cfg.getString("Registered", om(8));
						
					case 9:
						return cfg.getString("Not enough args", om(9));
						
					case 10:
						return cfg.getString("To many args", om(10));
						
					case 11:
						return cfg.getString("Session continue", om(11));
						
					case 12:
						return cfg.getString("Changed password", om(12));
						
					case 13:
						return cfg.getString("Removed password", om(13));
						
					case 14:
						return cfg.getString("Password already set", om(14));
						
					case 15:
						return cfg.getString("Password required", om(15));
				}
			} catch(YAMLException e)
			{
				plugin.log.warning(e.getMessage());
			}
		}else
		{
			return om(s);
		}
		return "";
	}
	
	public static String om(int s)
	{
		switch(s)
		{
				case 1:
					return "No permissions.";
					
				case 2:
					return "Password must be longer than {Min}.";
					
				case 3:
					return "Password must be shorter than {Max}.";
					
				case 4:
					return "Invalid password.";
					
				case 5:
					return "Logged in!";
					
				case 6:
					return "Please register using /setpass <password>.";
					
				case 7:
					return "Please login using /login <password>.";
					
				case 8:
					return "Set password to: {Password}";
					
				case 9:
					return "Not enough args!";
					
				case 10:
					return "To many args!";
					
				case 11:
					return "Extending session from last login.";
					
				case 12:
					return "Changed password to: {Password}";
					
				case 13:
					return "Removed password.";
					
				case 14:
					return "You already got a password.";
					
				case 15:
					return "Password is required.";
			}
		return "";
	}
}

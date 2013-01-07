package me.lenis0012.ls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.lenis0012.ls.commands.AdminCommand;
import me.lenis0012.ls.commands.ChangePassCommand;
import me.lenis0012.ls.commands.LoginCommand;
import me.lenis0012.ls.commands.RmpassCommand;
import me.lenis0012.ls.commands.SetpassCommand;
import me.lenis0012.ls.Util.Metrics;
import me.lenis0012.ls.Util.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ls extends JavaPlugin{
	public static String Version = "1.5.7";
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	public Logger log = Logger.getLogger("Minecraft");
	public List<String> invalid = new ArrayList<String>();
	public List<String> ignore = new ArrayList<String>();
	public boolean Tasker = true;
	public int delay = 0;
	public List<String> onlinePlayers = new ArrayList<String>();

	@Override
	public void onEnable(){
		log.info("[LoginSecurity] safety first ;)");
		getServer().getPluginManager().registerEvents(new lsLogin(this), this);
		getServer().getPluginManager().registerEvents(new LogginSession(this), this);
		getServer().getPluginManager().registerEvents(new Version(this), this);
		final FileConfiguration config = this.getConfig();
		getCustomConfig().options().header("please do not remove the the data file");
		config.addDefault("options.password-required", false);
		config.addDefault("options.use-MD5 Enryption", true);
		config.addDefault("options.blindness", true);
		config.addDefault("options.blocked-chars", "!,@,#,$,%,^,&,*,(,),-,:,{,},[,]");
		config.addDefault("options.update-checker", true);
		config.addDefault("options.min-length", 3);
		config.addDefault("options.max-lenght", 16);
		config.addDefault("options.session.use", true);
		config.addDefault("options.session.timeout (sec)", 60);
		config.addDefault("options.timeout.use", true);
		config.addDefault("options.timeout.time (sec)", 120);
		config.addDefault("MySQL.use", false);
		config.addDefault("MySQL.host", "localhost");
		config.addDefault("MySQL.port", 3306);
		config.addDefault("MySQL.database", "LoginSecurity");
		config.addDefault("MySQL.username", "root");
		config.addDefault("MySQL.password", "password");
		config.options().copyDefaults(true);
		saveConfig();
		reloadCustomConfig();
		saveCustomConfig();
		
		getCommand("setpass").setExecutor(new SetpassCommand(this));
		getCommand("login").setExecutor(new LoginCommand(this));
		getCommand("rmpass").setExecutor(new RmpassCommand(this));
		getCommand("changepass").setExecutor(new ChangePassCommand(this));
		getCommand("lac").setExecutor(new AdminCommand(this));
		
		this.Task();
		LoginData.start(this);
		
		try {
			Metrics metrics = new Metrics(this);
			metrics.start();
		} catch (IOException e)
		{
			log.info("[LoginSecurity] Failed sending stats to mcstats.org");
		}
		
		for(Player player : Bukkit.getServer().getOnlinePlayers()) {
			this.onlinePlayers.add(player.getName());
		}
	}
	
	public void onDisable(){
		Tasker = false;
		log.info("[LoginSecurity] turned off");
		reloadConfig();
		reloadCustomConfig();
		LoginData.stopCon(this);
	}
	public void reloadCustomConfig()
	{
        if (customConfigFile == null)
        {
        	customConfigFile = new File(getDataFolder(), "data.yml");
        }
        customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
        java.io.InputStream defConfigStream = this.getResource("data.yml");
        if (defConfigStream != null)
        {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            customConfig.setDefaults(defConfig);
        }
    }
	public FileConfiguration getCustomConfig()
	{
        if (customConfig == null)
        {
            this.reloadCustomConfig();
        }
        return customConfig;
    }
	public void saveCustomConfig()
	{
        if (customConfig == null || customConfigFile == null) {
        return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);}
	}
	
	public void Task()
	{
		this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable()
		{
			public void run()
			{
				try
				{
					for(World world : ls.this.getServer().getWorlds())
					{
						for(Entity entity : world.getEntities())
						{
							if(entity instanceof Player)
							{
								Player player = (Player)entity;
								String pname = player.getName();
								if(ls.this.invalid.contains(pname))
								{
									if(ls.this.getConfig().getBoolean("options.password-required") == true && !LoginData.hasPass(player.getName(), ls.this)){
										player.sendMessage(ChatColor.RED + Messages.getMessage(6, ls.this));
									}else
									{
										player.sendMessage(ChatColor.RED + Messages.getMessage(7, ls.this));
									}
								}
							}
						}
					}
					/*if(delay == 60)
					{
						ls.this.delay = 0;
						LoginData.PurgeDatabase(ls.this);
					}
					ls.this.delay++;*/
				} catch(Exception e)
				{
					//nothing
				}
			}
		}
		, 20, 200);
	}
}

package me.lenis0012.ls;

import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LogginSession implements Listener{
	public static ls plugin;
	public LogginSession(ls instance) { plugin = instance; }
	@SuppressWarnings({ "unchecked", "rawtypes" })
	WeakHashMap<Player, Integer> session = new WeakHashMap();
	private boolean tasking = false;

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if(plugin.getConfig().getBoolean("options.session.use") == false)
			return;
		
		Player player = event.getPlayer();
		String pname = player.getName();
		
		if(LoginData.hasPass(pname, plugin) && !plugin.invalid.contains(pname))
		{
			session.put(player, plugin.getConfig().getInt("options.session.timeout (sec)"));
			plugin.getCustomConfig().set("ip." + pname.toLowerCase(), player.getAddress().getAddress().toString());
			plugin.saveCustomConfig();
		}
		if(tasking == false)
			this.RunTask();
	}
	
	public void RunTask()
	{
		tasking = true;
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
		{
			public void run()
			{
				try
				{
					for (Player player : LogginSession.this.session.keySet())
					{
						String pname = player.getName();
						int current = ((Integer)LogginSession.this.session.get(player)).intValue();
					
						if(current > 1)
						{
							LogginSession.this.session.put(player, Integer.valueOf(current - 1));
							if(!plugin.ignore.contains(pname))
							{
								plugin.ignore.add(pname);
							}
						}
						else
						{
							LogginSession.this.session.remove(player);
							if(plugin.ignore.contains(pname))
							{
								plugin.ignore.remove(pname);
							}
						}
					}
				} catch (Exception e)
				{
					//null
				}
			}
		}
		, 20L, 20L);
	}
}

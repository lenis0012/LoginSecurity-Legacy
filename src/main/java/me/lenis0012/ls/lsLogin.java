package me.lenis0012.ls;

import java.util.WeakHashMap;
import me.lenis0012.ls.ls;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@SuppressWarnings("deprecation")
public class lsLogin implements Listener{
	public boolean enable;
	public static ls plugin;
	public lsLogin(ls instance) { plugin = instance; }
	private WeakHashMap<String, Player> users = new WeakHashMap<String, Player>();

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent join)
	{
		final Player player = join.getPlayer();
		final String pname = player.getName();
		
		plugin.onlinePlayers.add(pname);
		
		if(!player.isOnline())
			return;
		
		if(pname.length() < 3)
		{
			player.kickPlayer("Username to short.");
			return;
		}
		
		String[] chars = plugin.getConfig().getString("options.blocked-chars").split(",");
		for(String s : chars)
		{
			if(pname.contains(s))
			{
				player.kickPlayer("Your username may not contain: "+ChatColor.RED+chars.toString().replace("[", "").replace("]", ""));
				return;
			}
		}
		
		if(plugin.ignore.contains(pname) && player.getAddress().getAddress().toString().equals(plugin.getCustomConfig().getString("ip." + pname.toLowerCase())))
		{
			player.sendMessage("[LoginSecurity] " + Messages.getMessage(11, plugin));
			return;
		}
		if(plugin.getConfig().getBoolean("options.password-required") == true && !LoginData.hasPass(pname.toLowerCase(), plugin)){
			player.sendMessage(ChatColor.RED + Messages.getMessage(6, plugin));
			if(!plugin.invalid.contains(pname)){
				plugin.invalid.add(pname);
			}
			if(plugin.getConfig().getBoolean("options.blindness") == true)
			{
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1728000, 15));
			}
		}
		else if(LoginData.hasPass(pname.toLowerCase(), plugin)){
			player.sendMessage(ChatColor.RED + Messages.getMessage(7, plugin));
			if(!plugin.invalid.contains(pname))
			{
				plugin.invalid.add(pname);
			}
			if(plugin.getConfig().getBoolean("options.blindness") == true)
			{
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1728000, 15));
			}
		}
		else{
			if(plugin.invalid.contains(pname)){
				plugin.invalid.remove(pname);
			}
			return;
		}
		
		users.put(pname, player);
		this.task(pname);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent move){
		Player player = move.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname)){
			player.teleport(move.getFrom());
		}else if(player.hasPotionEffect(PotionEffectType.BLINDNESS))
			player.removePotionEffect(PotionEffectType.BLINDNESS);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent chat){
		Player player = chat.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname)){
			chat.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent drop) {
		Player player = drop.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname)) {
			drop.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPreLogin(PlayerPreLoginEvent event) {
		String name = event.getName();
		if(plugin.onlinePlayers.contains(name)) {
			if(!plugin.invalid.contains(name)) {
				event.setKickMessage("You are already ingame.");
				event.setResult(Result.KICK_OTHER);
			}
		}
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent drop)
	{
		Player player = drop.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname))
		{
			drop.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent bbreak) {
		Player player = bbreak.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname)) {
			bbreak.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent place)
	{
		Player player = place.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname))
		{
			place.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent fill)
	{
		Player player = fill.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname))
		{
			fill.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent empty)
	{
		Player player = empty.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname))
		{
			empty.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		String pname = player.getName();
		if(plugin.getConfig().getBoolean("MySQL.use"))
		{
			if(LoginData.hasPass(pname.toLowerCase(), plugin))
			{
				LoginData.changeDate(pname.toLowerCase(), plugin);
			}
		}
		plugin.onlinePlayers.remove(pname);
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent command) {
		Player player = command.getPlayer();
		String pname = player.getName();
		if(plugin.invalid.contains(pname)){
		    if(!command.getMessage().startsWith("/login") && !command.getMessage().startsWith("/setpass"))
		  	{
		    	//faction fix start
		    	if(command.getMessage().startsWith("/f"))
		    		command.setMessage("/blablablablabla"); //this command does not exist :P
		    	//faction fix end
		    	command.setCancelled(true);
		  	}
		}
	}
	
	@EventHandler
	public void OnHealthRegain(EntityRegainHealthEvent event)
	{
		Entity entity = event.getEntity();
		if(!(entity instanceof Player))
			return;
		Player player = (Player)entity;
		String pname = player.getName();
			
		if(plugin.invalid.contains(pname))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void OnFoodLevelChange(FoodLevelChangeEvent event)
	{
		Entity entity = event.getEntity();
		if(!(entity instanceof Player))
			return;
		Player player = (Player)entity;
		String pname = player.getName();
			
		if(plugin.invalid.contains(pname))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		Entity entity = event.getEntity();
		if(!(entity instanceof Player))
			return;
		Player player = (Player)entity;
		String pname = player.getName();
			
		if(plugin.invalid.contains(pname))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		Entity entity = event.getWhoClicked();
		if(!(entity instanceof Player))
			return;
		Player player = (Player)entity;
		String pname = player.getName();
			
		if(plugin.invalid.contains(pname))
		{
			event.setCancelled(true);
		}
	}
	
	public void task(final String pname)
	{
		if(plugin.getConfig().getBoolean("options.timeout.use"))
		{
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
			{
				public void run()
				{
					try
					{
						Player player = users.get(pname);
						if(player.isOnline())
						{
							if(plugin.invalid.contains(pname))
							{
								player.kickPlayer("Login timed out.");
							}
						}
					} catch(Exception e) {}
				}
			}, plugin.getConfig().getInt("options.timeout.time (sec)") * 20);
		}
	}
}

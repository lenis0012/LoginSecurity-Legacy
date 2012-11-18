package me.lenis0012.ls.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import me.lenis0012.ls.ls;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Version implements Listener{
	private ls plugin;
	public Version(ls instance) { this.plugin = instance; }
	
	public String checkForUpdate() throws Exception {
		URL url = new URL("https://raw.github.com/lenis00012/LoginVersion/master/version.version");
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine = in.readLine();
		in.close();
		
		return inputLine;
		}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(plugin.getConfig().getBoolean("options.update-checker") == false)
		{
			return;
		}
		final Player player = event.getPlayer();
		if (player != null && (player.isOp() || player.hasPermission("marry.admin"))) {
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() { // create a new anonymous task/thread that will check the version asyncronously
				@Override
				public void run() {
					try {
						String oldVersion = plugin.getDescription().getVersion();
						String newVersion = checkForUpdate();
						if (newVersion != null && !newVersion.equals(oldVersion)) // do we have a version update? => notify player
							player.sendMessage("[LoginSecurity] " + ChatColor.YELLOW + "Update avaible, update v" + oldVersion + " to v" + newVersion);
						} catch (Exception e) {
							player.sendMessage("LoginSecurity could not get version update - see log for details.");
							plugin.log.warning("[LoginSecurity] Could not connect to remote server to check for update. Exception said: " + e.getMessage());
						}
					}
				}, 25L);
			}
	}
}

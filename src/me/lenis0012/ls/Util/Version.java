package me.lenis0012.ls.Util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import me.lenis0012.ls.ls;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Version implements Listener{
	private ls plugin;
	public Version(ls instance) { this.plugin = instance; }
	
	public boolean checkForUpdate(String old)
	{
		try
		{
			String url = "http://dev.bukkit.org/server-mods/loginsecurity/files.rss";
			url = url.replaceAll(" ", "%20");
			ParseResult result = parseRSS(url);
			url = result.getLink();
			String newVersion = null;
			for(String s : url.split("/"))
			{
				if(s.contains("-login-security"))
				{
					newVersion = s.split("-")[3];
				}
			}
			if(newVersion != null)
			{
				if(Double.valueOf(old) < Double.valueOf(newVersion))
					return true;
			}
		} catch(Exception e)
		{
		}
		return false;
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(plugin.getConfig().getBoolean("options.update-checker") == false)
		{
			return;
		}
		final Player player = event.getPlayer();
		if (player != null && player.hasPermission("ls.admin")) {
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { // create a new anonymous task/thread that will check the version asyncronously
				@Override
				public void run() {
					try {
						String oldVersion = plugin.getDescription().getVersion();
						boolean newVersion = checkForUpdate(oldVersion);
						if (newVersion) // do we have a version update? => notify player
							player.sendMessage("[LoginSecurity] " + ChatColor.YELLOW + "Update available, check BukkitDev");
						} catch (Exception e) {
							player.sendMessage("LoginSecurity could not get version update - see log for details.");
							plugin.log.warning("[LoginSecurity] Could not connect to remote server to check for update. Exception said: " + e.getMessage());
						}
					}
				}, 25L);
			}
	}
	
	 public ParseResult parseRSS(String pluginUrlString) throws Exception
	 {
		 String newPluginUrlString = null;
		 String pubDate = null;
		 String description = null;
		 try
		 {
			 URL url = new URL(pluginUrlString);
			 URLConnection urlConnection = url.openConnection();
			 HttpURLConnection connection = null;
			 connection = (HttpURLConnection)urlConnection;
			 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			 DocumentBuilder db = dbf.newDocumentBuilder();
			 Document doc = db.parse(connection.getInputStream());
			 doc.getDocumentElement().normalize();
			 NodeList nodeLst = doc.getElementsByTagName("item");
			 Node fstNode = nodeLst.item(0);
			 if (fstNode.getNodeType() == 1)
			 {
				 Element fstElmnt = (Element)fstNode;
				 NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("link");
				 Element fstNmElmnt = (Element)fstNmElmntLst.item(0);
				 NodeList fstNm = fstNmElmnt.getChildNodes();
				 newPluginUrlString = fstNm.item(0).getNodeValue();
				 fstNmElmntLst = fstElmnt.getElementsByTagName("pubDate");
				 fstNmElmnt = (Element)fstNmElmntLst.item(0);
				 fstNm = fstNmElmnt.getChildNodes();
				 pubDate = fstNm.item(0).getNodeValue();
				 fstNmElmntLst = fstElmnt.getElementsByTagName("description");
				 fstNmElmnt = (Element)fstNmElmntLst.item(0);
				 fstNm = fstNmElmnt.getChildNodes();
				 description = fstNm.item(0).getNodeValue();
			 }
		 }
		 catch (Exception localException)
		 {
		 }
		 return new ParseResult(newPluginUrlString, pubDate, description);
	  }
}

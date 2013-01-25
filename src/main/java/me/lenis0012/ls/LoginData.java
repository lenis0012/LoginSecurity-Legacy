package me.lenis0012.ls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class LoginData
{
	static private Connection connect = null;
	static private Statement statement = null;
	static private ResultSet resultSet = null;
	private static PreparedStatement preparedStatement = null;
	private static String table = "passwords";
	private static boolean first = true;
	private static int dis = 1;
	
	public static void start(ls plugin)
	{
		if(plugin.getConfig().getBoolean("MySQL.use"))
		{
			startDrivers(plugin);
			Table(plugin);
			PurgeDatabase(plugin);
		}
	}
	
	public static void Table(ls plugin)
	{
		try
		{
		statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + table +
				" (" + "username VARCHAR(250) NOT NULL UNIQUE,password VARCHAR(250) NOT NULL,lastlogin VARCHAR(30));");
		} catch(SQLException e)
		{
			plugin.log.warning("Error while creating table: " + e.getMessage());
		}
	}
	
	public static String getPass(String user, ls plugin)
	{
		String password = "";
		if(plugin.getConfig().getBoolean("MySQL.use"))
		{
			try
			{
				preparedStatement = connect.prepareStatement("SELECT * FROM " + table + " WHERE username=?;");
				preparedStatement.setString(1, user);
				resultSet = preparedStatement.executeQuery();
				if(resultSet.next())
					password = resultSet.getString("password");
			} catch (SQLException e) {
				plugin.log.warning("Error while trying to get data form MySQL databse: " + e.getMessage());
				ls.setLockDown(true);
			}
		}else
			password = plugin.getCustomConfig().getString("password.password." + user);
		return password;
	}
	
	public static void changeDate(String user, ls plugin)
	{
		try
		{
			Calendar c = Calendar.getInstance();
			String format = String.valueOf(c.get(Calendar.YEAR))+"-"
					+String.valueOf(c.get(Calendar.DAY_OF_YEAR));
			preparedStatement = connect.prepareStatement("UPDATE " + table + " SET lastlogin=? WHERE username=?;");
			preparedStatement.setString(1, format);
			preparedStatement.setString(2, user);
			preparedStatement.executeUpdate();
		} catch(SQLException e)
		{
			plugin.log.warning("Failes saving lastlogin to Database: " + e.getMessage());
			ls.setLockDown(true);
		}
	}
	
	public static boolean hasPass(String user, ls plugin)
	{
		if(plugin.getConfig().getBoolean("MySQL.use"))
		{
			if(getPass(user, plugin) != "")
				return true;
		}else
		{
			return getPass(user, plugin) != null;
		}
		return false;
	}
	
	public static void delPass(String user, ls plugin)
	{
		if(plugin.getConfig().getBoolean("MySQL.use"))
		{
			try
			{
				preparedStatement = connect.prepareStatement("DELETE FROM " + table + " WHERE username=?;");
				preparedStatement.setString(1, user);
				preparedStatement.executeUpdate();
			} catch(SQLException e)
			{
				plugin.log.warning("Error while trying to get data form MySQL databse: " + e.getMessage());
				ls.setLockDown(true);
			}
		}else
		{
			plugin.getCustomConfig().set("password.password." + user, null);
			plugin.saveCustomConfig();
		}
	}
	
	public static void setPass(String user, String pass, ls plugin, int mode)
	{
		if(plugin.getConfig().getBoolean("MySQL.use"))
		{
			try
			{
				Calendar c = Calendar.getInstance();
				String format = String.valueOf(c.get(Calendar.YEAR))+"-"
						+String.valueOf(c.get(Calendar.DAY_OF_YEAR));
				if(mode == 0)
				{
					preparedStatement = connect.prepareStatement("INSERT INTO " + table + "(username,password,lastlogin) VALUES(?,?,?);");
					preparedStatement.setString(1, user);
					preparedStatement.setString(2, pass);
					preparedStatement.setString(3, format);
					preparedStatement.executeUpdate();
				}
				else
				{
					preparedStatement = connect.prepareStatement("UPDATE " + table + " SET password=? WHERE username=?;");
					preparedStatement.setString(1, pass);
					preparedStatement.setString(2, user);
					preparedStatement.executeUpdate();
				}
					
			} catch (SQLException e) {
				plugin.log.warning("Error while trying to get data form MySQL databse: " + e.getMessage());
				ls.setLockDown(true);
			}
		}else
		{
			plugin.getCustomConfig().set("password.password." + user, pass);
			plugin.saveCustomConfig();
		}
	}
	
	public static void PurgeDatabase(ls plugin)
	{
		if(plugin.getConfig().getBoolean("MySQL.use"))
		{
			try
			{
				preparedStatement = connect.prepareStatement("SELECT * FROM " + table + ";");
				resultSet = preparedStatement.executeQuery();
				while(resultSet.next())
				{
					int t = 0;
					Calendar c = Calendar.getInstance();
					String[] split = resultSet.getString("lastlogin").split("-");
					int y = c.get(Calendar.YEAR);
					int d = c.get(Calendar.DAY_OF_YEAR);
					int year = Integer.valueOf(split[0]);
					int day = Integer.valueOf(split[1]);
					if(y != year)
					{
						t = 365 - day + d;
					}else
					{
						t = d - day;
					}
					
					if(t >= 30 && dis == 2)
					{
						String username = resultSet.getString("username");
						delPass(username, plugin);
						plugin.log.info("[LoginSecurity] Removed " + username + " from MySQL databse due player incactive.");
					}
				}
			} catch(SQLException e)
			{
				plugin.log.warning("Error while purging database: " + e.getMessage());
				ls.setLockDown(true);
			}
			stopCon(plugin);
			startDrivers(plugin);
		}
	}
	
	public static boolean tryConnect(ls plugin) {
		stopCon(plugin);
		return startDrivers(plugin);
	}
	
	private static boolean startDrivers(ls plugin)
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			String host = plugin.getConfig().getString("MySQL.host", "localhost");
			String port = String.valueOf(plugin.getConfig().getInt("MySQL.port", 3306));
			String databse = plugin.getConfig().getString("MySQL.database", "loginsecurity");
			String username = plugin.getConfig().getString("MySQL.username", "root");
			String pass = plugin.getConfig().getString("MySQL.password", "");
			connect = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port +"/" + databse + "?" +
			"user=" + username + "&password=" + pass);
			statement = connect.createStatement();
			if(first)
			{
				plugin.log.info("[LoginSecurity] Started MySQL driver.");
				first = false;
			}
		} catch(ClassNotFoundException e) {
		    plugin.log.warning("Error loading driver: " + e.getMessage());
		}  catch (SQLException e) {
			plugin.log.warning("Error while trying to login to MySQL: " + e.getMessage());
			return false;
		}
		return true;
	}
	
	public static void stopCon(ls plugin)
	{
		try {
			if(preparedStatement != null)
				preparedStatement.close();
			if(resultSet != null)
				resultSet.close();
			if(statement != null)
				statement.close();
			if(connect != null)
				connect.close();
		} catch (SQLException e) {
			plugin.log.warning("Error trying to close connection: " + e);
		}
	}
}

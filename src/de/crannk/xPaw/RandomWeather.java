package de.crannk.xPaw;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.rominos2.ThunderTower.ThunderTower;
import org.rominos2.ThunderTower.ThunderTowerWeatherListener;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class RandomWeather extends JavaPlugin
{
	private static PermissionHandler Permissions;
	ThunderTower thunderTower;
	public Configuration config;
	public final Logger log = Logger.getLogger( "Minecraft" );
	ConcurrentHashMap lastweather = new ConcurrentHashMap();
	
	public void onEnable( )
	{
		// Check to see if there is a configuration file.
		// Credits to <Sleaker>
		File yml = new File( getDataFolder() + "/config.yml" );
		
		if( !yml.exists() )
		{
			new File( getDataFolder().toString() ).mkdir();
			
			try
			{
				yml.createNewFile();
			}
			catch( IOException e )
			{
				log.warning( "[WeatherRestrictions] Exception: " + e.getMessage() );
				log.warning( "[WeatherRestrictions] Cannot create configuration file. And none to load, using defaults." );
			}
		}
		setupPermissions();
		setupThunderTower();
		config = getConfiguration();
		
		final RandomWeatherWeatherListener wL = new RandomWeatherWeatherListener( this );
		final RandomWeatherWorldListener worldL = new RandomWeatherWorldListener( this );
		final RandomWeatherBlockListener blockL = new RandomWeatherBlockListener(this);
		final RandomWeatherCommands commandL = new RandomWeatherCommands( this );
		final PluginManager pm = getServer().getPluginManager();
		final PluginDescriptionFile pdfFile = this.getDescription();
		
		List<World> worlds = getServer().getWorlds();
		
		for( World world : worlds )
		{
			worldL.WorldLoaded( world );
		}
		
		pm.registerEvent( Event.Type.WORLD_LOAD, worldL, Event.Priority.Monitor, this );
		pm.registerEvent( Event.Type.WEATHER_CHANGE, wL, Event.Priority.Highest, this );
		pm.registerEvent( Event.Type.THUNDER_CHANGE, wL, Event.Priority.Highest, this );
		pm.registerEvent( Event.Type.LIGHTNING_STRIKE, wL, Event.Priority.Highest, this );
		pm.registerEvent(Event.Type.SNOW_FORM, blockL, Event.Priority.Highest, this);
		PluginCommand batchcommand = this.getCommand("wr");
		batchcommand.setExecutor(commandL);
		try {
			PluginCommand command = this.getCommand("rain");
			command.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /rain, please use /wr rain");
		}
		try {
			PluginCommand command2 = this.getCommand("rainclear");
			command2.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /rainclear, please use /wr clear");
		}
		try {
			PluginCommand command3 = this.getCommand("clearrain");
			command3.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /clearrain, please use /wr clear");
		}
		try {
			PluginCommand commandthunder = this.getCommand("thunder");
			commandthunder.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /thunder, please use /wr thunder");
		}
		try {
			PluginCommand command5 = this.getCommand("weatherstats");
			command5.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /weatherstats, please use /wr stats");
		}
		try {
			PluginCommand commandlightning = this.getCommand("lightning");
			commandlightning.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /lightning, please use /wr lightning");
		}
		
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	public void onDisable( )
	{
		//
	}
	
	public boolean isNodeDisabled( String name, String worldName, boolean thedefault )
	{
		return config.getBoolean( worldName + "." + name, thedefault );
	}
	
	/**Default is True if not set.**/
	public boolean isNodeDisabled( String name, String worldName )
	{
		return config.getBoolean( worldName + "." + name, true );
	}
	
	public int getIntValue( String name, String worldName, int thedefault )
	{
		return config.getInt(worldName + "." + name, thedefault);
	}
	
	public void setConfigNode( String name, String worldName, Boolean value )
	{
		config.setProperty( worldName + "." + name, value );
	}
	
	public void setConfigNode( String name, String worldName, int value )
	{
		config.setProperty( worldName + "." + name, value );
	}
	
	private void setupPermissions() {
        Plugin permissions = this.getServer().getPluginManager().getPlugin("Permissions");

        if (Permissions == null) {
            if (permissions != null) {
                Permissions = ((Permissions)permissions).getHandler();
            } else {
            }
        }
    }
	
	private void setupThunderTower() {
        ThunderTower tt = (ThunderTower)this.getServer().getPluginManager().getPlugin("ThunderTower");

        if (thunderTower == null) {
            if (tt != null) {
                thunderTower = tt;
            } else {
            }
        }
    }
    
    public static boolean hasPermissions(Player player, String node) {
        if (Permissions != null) {
            return Permissions.has(player, node);
        } else {
            return player.isOp();
        }
    }

	public double getDoubleValue(String name, String worldName, int thedefault) {
		return config.getDouble(worldName + "." + name, thedefault);
	}

	public void setConfigNode(String name, String worldName,
			double value) {
		config.setProperty( worldName + "." + name, value );
		
	}
	
	boolean stringToBool(String thebool) {
		boolean result;
		if (thebool.trim().equalsIgnoreCase("true") || thebool.trim().equalsIgnoreCase("yes")) {
	    	result = true;
	    } else {
	    	result = false;
	    }
		return result;
	}
}
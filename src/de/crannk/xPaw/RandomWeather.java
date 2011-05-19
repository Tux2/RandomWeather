package de.crannk.xPaw;

import java.io.File;
import java.io.IOException;
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
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class RandomWeather extends JavaPlugin
{
	private static PermissionHandler Permissions;
	public Configuration config;
	public final Logger log = Logger.getLogger( "Minecraft" );
	public String playerweatherevent = "";
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
		config = getConfiguration();
		
		final RandomWeatherWeatherListener wL = new RandomWeatherWeatherListener( this );
		final RandomWeatherWorldListener worldL = new RandomWeatherWorldListener( this );
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
		PluginCommand command = this.getCommand("rain");
		command.setExecutor(commandL);
		PluginCommand command2 = this.getCommand("rainclear");
		command2.setExecutor(commandL);
		PluginCommand command3 = this.getCommand("clearrain");
		command3.setExecutor(commandL);
		PluginCommand command4 = this.getCommand("thunder");
		command4.setExecutor(commandL);
		PluginCommand command5 = this.getCommand("weatherstats");
		command5.setExecutor(commandL);
		
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	public void onDisable( )
	{
		//
	}
	
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
    
    public static boolean hasPermissions(Player player, String node) {
        if (Permissions != null) {
            return Permissions.has(player, node);
        } else {
            return player.isOp();
        }
    }
}
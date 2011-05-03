package de.crannk.xPaw;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class NoWeather extends JavaPlugin
{
	public Configuration config;
	public final Logger log = Logger.getLogger( "Minecraft" );
	
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
				log.warning( "[NoWeather] Exception: " + e.getMessage() );
				log.warning( "[NoWeather] Cannot create configuration file. And none to load, using defaults." );
			}
		}
		
		config = getConfiguration();
		
		final NoWeatherWeatherListener wL = new NoWeatherWeatherListener( this );
		final NoWeatherWorldListener worldL = new NoWeatherWorldListener( this );
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
	
	public void setConfigNode( String name, String worldName, Boolean value )
	{
		config.setProperty( worldName + "." + name, value );
	}
}
package de.crannk.xPaw;

import java.util.logging.Logger;

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
		config = getConfiguration();
		config.load();
		
		final NoWeatherWeatherListener wL = new NoWeatherWeatherListener( this );
		final NoWeatherWorldListener worldL = new NoWeatherWorldListener( this );
		final PluginManager pm = getServer().getPluginManager();
		final PluginDescriptionFile pdfFile = this.getDescription();
		
		pm.registerEvent( Event.Type.WORLD_LOAD, worldL, Event.Priority.Highest, this );
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
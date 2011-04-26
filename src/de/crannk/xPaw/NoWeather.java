package de.crannk.xPaw;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class NoWeather extends JavaPlugin
{
	public void onEnable( )
	{
		final Logger log = Logger.getLogger( "Minecraft" );
		final Configuration config = getConfiguration();
		
		Boolean disWeather   = config.getBoolean( "disable-weather", true );
		Boolean disThunder   = config.getBoolean( "disable-thunder", true );
		Boolean disLightning = config.getBoolean( "disable-lightning", true );
		
		config.setProperty( "disable-weather", disWeather );
		config.setProperty( "disable-thunder", disThunder );
		config.setProperty( "disable-lightning", disLightning );
		config.save();
		
		if( !disWeather && !disThunder && !disLightning )
		{
			log.warning( "Why you use NoWeather plugin? You didn't disable anything in it!" );
			return;
		}
		
		final NoWeatherWeatherListener wL = new NoWeatherWeatherListener();
		
		PluginManager pm = getServer().getPluginManager();
		
		if( disWeather )
			pm.registerEvent( Event.Type.WEATHER_CHANGE, wL, Event.Priority.Highest, this );
		
		if( disThunder )
			pm.registerEvent( Event.Type.THUNDER_CHANGE, wL, Event.Priority.Highest, this );
		
		if( disLightning )
			pm.registerEvent( Event.Type.LIGHTNING_STRIKE, wL, Event.Priority.Highest, this );
		
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	public void onDisable( )
	{
		//
	}
}
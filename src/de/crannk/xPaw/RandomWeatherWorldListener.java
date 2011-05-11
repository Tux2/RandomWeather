package de.crannk.xPaw;

import org.bukkit.World;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class RandomWeatherWorldListener extends WorldListener
{
	private RandomWeather plugin;
	
	public RandomWeatherWorldListener( RandomWeather plugin )
	{
		this.plugin = plugin;
	}
	
	public void onWorldLoad( WorldLoadEvent event )
	{
		WorldLoaded( event.getWorld() );
	}
	
	public void WorldLoaded( World world )
	{
		String worldName = world.getName();
		
		if( !plugin.config.getKeys( null ).contains( worldName ) )
		{
			plugin.log.info( "[RandomWeather] " + worldName + " - no configuration, generating defaults." );
		}
		
		Boolean disWeather   = plugin.isNodeDisabled( "disable-weather", worldName );
		Boolean disThunder   = plugin.isNodeDisabled( "disable-thunder", worldName );
		Boolean disLightning = plugin.isNodeDisabled( "disable-lightning", worldName );
		int minWaitWeather = plugin.getIntValue("minimum-rain-wait", worldName, 600);
		int maxWaitWeather = plugin.getIntValue( "max-rain-wait", worldName, 1000 );
		
		if( disWeather && world.hasStorm() )
		{
			world.setStorm( false );
			plugin.log.info( "[RandomWeather] Stopped storm in " + worldName );
		}
		
		if( disThunder && world.isThundering() )
		{
			world.setThundering( false );
			plugin.log.info( "[RandomWeather] Stopped thunder in " + worldName );
		}
		
	//	plugin.log.info( "[RandomWeather] " + worldName + " - Weather  : " + disWeather.toString() );
	//	plugin.log.info( "[RandomWeather] " + worldName + " - Thunder  : " + disThunder.toString() );
	//	plugin.log.info( "[RandomWeather] " + worldName + " - Lightning: " + disLightning.toString() );
		
		plugin.setConfigNode( "disable-weather", worldName, disWeather );
		plugin.setConfigNode( "disable-thunder", worldName, disThunder );
		plugin.setConfigNode( "disable-lightning", worldName, disLightning );
		plugin.setConfigNode( "minimum-rain-wait", worldName, minWaitWeather );
		plugin.setConfigNode( "max-rain-wait", worldName, maxWaitWeather );
		plugin.config.save();
	}
}
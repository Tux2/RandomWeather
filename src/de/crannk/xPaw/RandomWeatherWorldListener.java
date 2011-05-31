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
	
	public synchronized void WorldLoaded( World world )
	{
		String worldName = world.getName();
		
		if( !plugin.config.getKeys( null ).contains( worldName ) )
		{
			plugin.log.info( "[RandomWeather] " + worldName + " - no configuration, generating defaults." );
		}
		
		Boolean disWeather   = plugin.isNodeDisabled( "disable-weather", worldName );
		Boolean disThunder   = plugin.isNodeDisabled( "disable-thunder", worldName );
		Boolean disLightning = plugin.isNodeDisabled( "disable-lightning", worldName );
		Boolean disSnow = plugin.isNodeDisabled( "disable-snow-accumulation", worldName );
		Boolean alwaysThunderstorms = plugin.isNodeDisabled( "makeall-thunderstorms", worldName, false );
		int minWaitWeather = plugin.getIntValue("minimum-rain-wait", worldName, 600);
		int maxWaitWeather = plugin.getIntValue( "max-rain-wait", worldName, 1000 );
		int maxWaitRain = plugin.getIntValue( "max-rain-duration", worldName, -1 );
		double superchargedchance = plugin.getDoubleValue( "supercharged-thunder-chance", worldName, 0 );
		double superchargedexplosion = plugin.getDoubleValue( "supercharged-explosion-radius", worldName, 3 );
		
		if( disWeather && world.hasStorm() )
		{
			world.setStorm( false );
			plugin.log.info( "[RandomWeather] Stopped storm in " + worldName );
		}else if (world.hasStorm()) {
			if(maxWaitRain > 0 ) {
				WeatherStarts thestart;
				if(plugin.worldsweather.containsKey(world.getName())) {
					thestart = plugin.worldsweather.get(world.getName());
					thestart.setType(WeatherStarts.STOPRAIN);
					thestart.setDueTime((maxWaitRain * 1000) + System.currentTimeMillis());
				}else {
					thestart = new WeatherStarts(world.getName(), 
							(maxWaitRain * 1000) + System.currentTimeMillis(),
							WeatherStarts.STOPRAIN);
				}
				if(!plugin.timeweather.contains(thestart)) {
					plugin.timeweather.add(thestart);
				}
				plugin.dispatchThread.interrupt();
			}
		}else if(!world.hasStorm()) {
			if(maxWaitWeather > 0 ) {
				WeatherStarts thestart;
				if(plugin.worldsweather.containsKey(world.getName())) {
					thestart = plugin.worldsweather.get(world.getName());
					thestart.setType(WeatherStarts.STARTRAIN);
					thestart.setDueTime((maxWaitWeather * 1000) + System.currentTimeMillis());
				}else {
					thestart = new WeatherStarts(world.getName(), 
							(maxWaitWeather * 1000) + System.currentTimeMillis(),
							WeatherStarts.STARTRAIN);
				}
				if(!plugin.timeweather.contains(thestart)) {
					plugin.timeweather.add(thestart);
				}
				plugin.dispatchThread.interrupt();
			}else {
				if(plugin.worldsweather.containsKey(world.getName())) {
					WeatherStarts thestart = plugin.worldsweather.get(world.getName());
					if(plugin.timeweather.contains(thestart)) {
						plugin.timeweather.remove(thestart);
						plugin.dispatchThread.interrupt();
					}
				}
			}
		}
		
		//If they have it so there is only thunderstorms, make sure there is thunder here....
		if (world.hasStorm() && alwaysThunderstorms) {
			world.setThundering(true);
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
		plugin.setConfigNode( "disable-snow-accumulation", worldName, disSnow );
		plugin.setConfigNode( "minimum-rain-wait", worldName, minWaitWeather );
		plugin.setConfigNode( "max-rain-wait", worldName, maxWaitWeather );
		plugin.setConfigNode( "max-rain-duration", worldName, maxWaitRain );
		plugin.setConfigNode("makeall-thunderstorms", worldName, alwaysThunderstorms);
		plugin.setConfigNode("supercharged-thunder-chance", worldName, superchargedchance);
		plugin.setConfigNode("supercharged-explosion-radius", worldName, superchargedexplosion);
		plugin.config.save();
	}
}

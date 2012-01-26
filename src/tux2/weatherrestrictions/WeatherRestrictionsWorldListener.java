package tux2.weatherrestrictions;

import java.io.IOException;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class WeatherRestrictionsWorldListener implements Listener {
	private WeatherRestrictions plugin;
	
	public WeatherRestrictionsWorldListener( WeatherRestrictions plugin )
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public synchronized void WorldLoaded( World world ) {
		String worldName = world.getName();
		
		if( !plugin.config.contains( worldName ) ) {
			plugin.log.info( "[WeatherRestrictions] " + worldName + " - no configuration, generating defaults." );
		}
		
		Boolean disWeather   = plugin.isNodeDisabled( "disable-weather", worldName );
		Boolean disThunder   = plugin.isNodeDisabled( "disable-thunder", worldName );
		Boolean disLightning = plugin.isNodeDisabled( "disable-lightning", worldName );
		Boolean disSnow = plugin.isNodeDisabled( "disable-snow-accumulation", worldName );
		Boolean disIce = plugin.isNodeDisabled( "disable-ice-accumulation", worldName );
		double thunderstormChance = plugin.getDoubleValue( "thunderstorm-chance", worldName, 35 );
		int minWaitWeather = plugin.getIntValue("minimum-rain-wait", worldName, 600);
		int maxWaitWeather = plugin.getIntValue( "max-rain-wait", worldName, -1 );
		int maxWaitRain = plugin.getIntValue( "max-rain-duration", worldName, -1 );
		double superchargedchance = plugin.getDoubleValue( "supercharged-thunder-chance", worldName, 0 );
		double superchargedexplosion = plugin.getDoubleValue( "supercharged-explosion-radius", worldName, 3 );
		
		if( disWeather && world.hasStorm() ) {
			world.setStorm( false );
			plugin.log.info( "[WeatherRestrictions] Stopped storm in " + worldName );
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
		
		//set the chance of a thunder storm....
		if (world.hasStorm() && thunderstormChance > 0) {
			if(new Random().nextInt(10000) <= (thunderstormChance*100)) {
				world.setThundering(true);
			}else {
				world.setThundering(false);
			}
		}
		
		if( disThunder && world.isThundering() )
		{
			world.setThundering( false );
			plugin.log.info( "[WeatherRestrictions] Stopped thunder in " + worldName );
		}
		
	//	plugin.log.info( "[WeatherRestrictions] " + worldName + " - Weather  : " + disWeather.toString() );
	//	plugin.log.info( "[WeatherRestrictions] " + worldName + " - Thunder  : " + disThunder.toString() );
	//	plugin.log.info( "[WeatherRestrictions] " + worldName + " - Lightning: " + disLightning.toString() );
		
		plugin.setConfigNode( "disable-weather", worldName, disWeather );
		plugin.setConfigNode( "disable-thunder", worldName, disThunder );
		plugin.setConfigNode( "disable-lightning", worldName, disLightning );
		plugin.setConfigNode( "disable-snow-accumulation", worldName, disSnow );
		plugin.setConfigNode( "disable-ice-accumulation", worldName, disIce );
		plugin.setConfigNode( "minimum-rain-wait", worldName, minWaitWeather );
		plugin.setConfigNode( "max-rain-wait", worldName, maxWaitWeather );
		plugin.setConfigNode( "max-rain-duration", worldName, maxWaitRain );
		plugin.setConfigNode("thunderstorm-chance", worldName, thunderstormChance);
		plugin.setConfigNode("supercharged-thunder-chance", worldName, superchargedchance);
		plugin.setConfigNode("supercharged-explosion-radius", worldName, superchargedexplosion);
		try {
			plugin.config.save(plugin.yml);
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
}

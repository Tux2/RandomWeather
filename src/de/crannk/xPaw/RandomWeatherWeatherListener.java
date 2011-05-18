package de.crannk.xPaw;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class RandomWeatherWeatherListener extends WeatherListener
{
	private RandomWeather plugin;
	private ConcurrentHashMap lastweather = new ConcurrentHashMap();
	
	public RandomWeatherWeatherListener( RandomWeather plugin )
	{
		this.plugin = plugin;
	}
	
	public void onWeatherChange( WeatherChangeEvent event )
	{
		if( !event.isCancelled() && event.toWeatherState() && plugin.isNodeDisabled( "disable-weather", event.getWorld().getName() ) )
		{
			event.setCancelled( true );
		}else {
			long lasttime = 0;
			try {
				lasttime = (Long) lastweather.get(event.getWorld().getName());
			}catch(Exception e) {
				
			}
			if(plugin.isNodeDisabled("disable-weather", event.getWorld().getName())) {
				//System.out.println("Stopped weather in world \"" + event.getWorld().getName() + "\"");
				event.setCancelled( true );
			}else if(!plugin.isNodeDisabled("disable-weather", event.getWorld().getName()) && !event.getWorld().hasStorm()
					&& ((plugin.getIntValue("minimum-rain-wait", event.getWorld().getName(), 600)*1000) >= System.currentTimeMillis() - lasttime)) {
				//System.out.println("Minimum time not elapsed. Stopped weather in world \"" + event.getWorld().getName() + "\"");
				//System.out.println("This world has a storm \"" + event.getWorld().hasStorm() + "\"");
				event.setCancelled( true );
			}else if(!plugin.isNodeDisabled("disable-weather", event.getWorld().getName()) && !event.getWorld().hasStorm() 
					&& ((plugin.getIntValue("minimum-rain-wait", event.getWorld().getName(), 600)*1000) < System.currentTimeMillis() - lasttime)) {
				//System.out.println("Let it rain in world \"" + event.getWorld().getName() + "\"");
				//let it rain.
			}else if(event.getWorld().hasStorm()) {
				//System.out.println("Weather stopped in world \"" + event.getWorld().getName() + "\" Recording...");
				lastweather.put(event.getWorld().getName(), System.currentTimeMillis());
			}
		}
	}
	
	public void onThunderChange( ThunderChangeEvent event )
	{
		if( !event.isCancelled() && event.toThunderState() && plugin.isNodeDisabled( "disable-thunder", event.getWorld().getName() ) )
		{
			event.setCancelled( true );
		}
	}
	
	public void onLightningStrike( LightningStrikeEvent event )
	{
		if( !event.isCancelled() && plugin.isNodeDisabled( "disable-lightning", event.getWorld().getName() ) )
		{
			event.setCancelled( true );
		}
	}
}
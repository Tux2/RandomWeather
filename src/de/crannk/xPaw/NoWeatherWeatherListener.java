package de.crannk.xPaw;

import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class NoWeatherWeatherListener extends WeatherListener
{
	private NoWeather plugin;
	
	public NoWeatherWeatherListener( NoWeather plugin )
	{
		this.plugin = plugin;
	}
	
	public void onWeatherChange( WeatherChangeEvent event )
	{
		if( event.toWeatherState() && plugin.isNodeDisabled( "disable-weather", event.getWorld().getName() ) )
		{
			event.setCancelled( true );
		}
	}
	
	public void onThunderChange( ThunderChangeEvent event )
	{
		if( event.toThunderState() && plugin.isNodeDisabled( "disable-thunder", event.getWorld().getName() ) )
		{
			event.setCancelled( true );
		}
	}
	
	public void onLightningStrike( LightningStrikeEvent event )
	{
		if( plugin.isNodeDisabled( "disable-lightning", event.getWorld().getName() ) )
		{
			event.setCancelled( true );
		}
	}
}
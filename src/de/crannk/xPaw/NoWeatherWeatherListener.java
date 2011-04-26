package de.crannk.xPaw;

import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;

public class NoWeatherWeatherListener extends WeatherListener
{
	public NoWeatherWeatherListener( )
	{
		//
	}
	
	public void onWeatherChange( WeatherChangeEvent event )
	{
		if( event.toWeatherState() )
		{
			event.setCancelled( true );
		}
	}
	
	public void onThunderChange( ThunderChangeEvent event )
	{
		if( event.toThunderState() )
		{
			event.setCancelled( true );
		}
	}
	
	public void onLightningStrike( LightningStrikeEvent event )
	{
		event.setCancelled( true );
	}
}
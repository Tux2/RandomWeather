package de.crannk.xPaw;

import java.util.List;

import org.bukkit.World;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class NoWeatherWorldListener extends WorldListener
{
	private NoWeather plugin;
	
	public NoWeatherWorldListener( NoWeather plugin )
	{
		this.plugin = plugin;
		
		List<World> worlds = plugin.getServer().getWorlds();
		
		for( World world : worlds )
		{
			WorldLoaded( world );
		}
	}
	
	public void onWorldLoad( WorldLoadEvent event )
	{
		WorldLoaded( event.getWorld() );
	}
	
	private void WorldLoaded( World world )
	{
		String worldName     = world.getName();
		Boolean disWeather   = plugin.isNodeDisabled( "disable-weather", worldName );
		Boolean disThunder   = plugin.isNodeDisabled( "disable-thunder", worldName );
		Boolean disLightning = plugin.isNodeDisabled( "disable-lightning", worldName );
		
		if( disWeather && world.hasStorm() )
		{
			world.setStorm( false );
			plugin.log.info( "[NoWeather] Stopped rain in " + world.getName() );
		}
		
		if( disThunder && world.isThundering() )
		{
			world.setThundering( false );
			plugin.log.info( "[NoWeather] Stopped thunder in " + world.getName() );
		}
		
		plugin.setConfigNode( "disable-weather", worldName, disWeather );
		plugin.setConfigNode( "disable-thunder", worldName, disThunder );
		plugin.setConfigNode( "disable-lightning", worldName, disLightning );
		plugin.config.save();
	}
}

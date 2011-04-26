package de.crannk.xPaw;

import java.util.List;

import org.bukkit.World;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class NoWeatherWorldListener extends WorldListener
{
	protected NoWeather plugin;
	
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
		if( plugin.disWeather && world.hasStorm() )
		{
			world.setStorm( false );
			plugin.log.info( "[NoWeather] Stopped rain in " + world.getName() );
		}
		
		if( plugin.disThunder && world.isThundering() )
		{
			world.setThundering( false );
			plugin.log.info( "[NoWeather] Stopped thunder in " + world.getName() );
		}
	}
}

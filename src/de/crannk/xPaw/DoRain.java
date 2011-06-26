package de.crannk.xPaw;

import org.bukkit.World;

public class DoRain implements Runnable {

	WeatherStarts world;
	RandomWeather plugin;
	
	public DoRain(RandomWeather plugin, WeatherStarts world) {
		this.plugin = plugin;
		this.world = world;
	}
	
	@Override
	public void run() {
		if(world.getType() == WeatherStarts.STARTRAIN) {
    		plugin.getServer().getWorld(world.getWorld()).setStorm(true);
    	}else if(world.getType() == WeatherStarts.STOPRAIN) {
    		plugin.getServer().getWorld(world.getWorld()).setStorm(false);
    		plugin.getServer().getWorld(world.getWorld()).setThundering(false);
    	}

	}

}

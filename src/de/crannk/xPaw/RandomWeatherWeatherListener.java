package de.crannk.xPaw;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherListener;
import org.rominos2.ThunderTower.ThunderTowerWeatherListener;

public class RandomWeatherWeatherListener extends WeatherListener
{
	private RandomWeather plugin;
	Random rand = new Random();
	
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
				lasttime = (Long) plugin.lastweather.get(event.getWorld().getName());
			}catch(Exception e) {
				
			}
			if(plugin.isNodeDisabled("disable-weather", event.getWorld().getName())) {
				//Cancel Storm
				event.setCancelled( true );
			}else if(!plugin.isNodeDisabled("disable-weather", event.getWorld().getName()) && !event.getWorld().hasStorm()
					&& ((plugin.getIntValue("minimum-rain-wait", event.getWorld().getName(), 600)*1000) >= System.currentTimeMillis() - lasttime)) {
				//Cancel Storm
				event.setCancelled( true );
			}else if(!plugin.isNodeDisabled("disable-weather", event.getWorld().getName()) && !event.getWorld().hasStorm() 
					&& ((plugin.getIntValue("minimum-rain-wait", event.getWorld().getName(), 600)*1000) < System.currentTimeMillis() - lasttime)) {
				//let it rain.
				//Check to see if we just want thunderstorms...
				if(plugin.isNodeDisabled("makeall-thunderstorms", event.getWorld().getName(), false)) {
					//If so, let's add some thunder in the mix
					event.getWorld().setThundering(true);
				}
				if(plugin.getIntValue("max-rain-duration", event.getWorld().getName(), -1) > 0 ) {
					WeatherStarts thestart;
					if(plugin.worldsweather.containsKey(event.getWorld().getName())) {
						thestart = plugin.worldsweather.get(event.getWorld().getName());
						thestart.setType(WeatherStarts.STOPRAIN);
						thestart.setDueTime((plugin.getIntValue("max-rain-duration", event.getWorld().getName(), -1) * 1000) + System.currentTimeMillis());
					}else {
						thestart = new WeatherStarts(event.getWorld().getName(), 
								(plugin.getIntValue("max-rain-duration", event.getWorld().getName(), -1) * 1000) + System.currentTimeMillis(),
								WeatherStarts.STOPRAIN);
					}
					if(!plugin.timeweather.contains(thestart)) {
						plugin.timeweather.add(thestart);
					}
					plugin.dispatchThread.interrupt();
				}
			}else if(event.getWorld().hasStorm()) {
				//Record when the rain stopped
				plugin.lastweather.put(event.getWorld().getName(), System.currentTimeMillis());
				if(plugin.getIntValue("max-rain-wait", event.getWorld().getName(), -1) > 0 ) {
					WeatherStarts thestart;
					if(plugin.worldsweather.containsKey(event.getWorld().getName())) {
						thestart = plugin.worldsweather.get(event.getWorld().getName());
						thestart.setType(WeatherStarts.STARTRAIN);
						thestart.setDueTime((plugin.getIntValue("max-rain-wait", event.getWorld().getName(), -1) * 1000) + System.currentTimeMillis());
					}else {
						thestart = new WeatherStarts(event.getWorld().getName(), 
								(plugin.getIntValue("max-rain-wait", event.getWorld().getName(), -1) * 1000) + System.currentTimeMillis(),
								WeatherStarts.STARTRAIN);
					}
					if(!plugin.timeweather.contains(thestart)) {
						plugin.timeweather.add(thestart);
					}
					plugin.dispatchThread.interrupt();
				}else {
					if(plugin.worldsweather.containsKey(event.getWorld().getName())) {
						WeatherStarts thestart = plugin.worldsweather.get(event.getWorld().getName());
						if(plugin.timeweather.contains(thestart)) {
							plugin.timeweather.remove(thestart);
							plugin.dispatchThread.interrupt();
						}
					}
				}
				
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
			//Capture that lightning event and add a percent chance that it is supercharged.
		}else if( !event.isCancelled()) {
			if (rand.nextInt(10000) >= (10000.0 - (plugin.getDoubleValue("supercharged-thunder-chance", event.getWorld().getName(), 0) * 100.0))) {
				boolean towernotfound = true;
				if(plugin.thunderTower != null) {
					//System.out.println("Thunder towers active in world: " + plugin.thunderTower.getProperties(event.getWorld()).getProperty("active", "true"));
					//System.out.println("Thunder tower block id: " + plugin.thunderTower.getProperties(event.getWorld()).getTypeID());
					//System.out.println("Block id: " + event.getLightning().getLocation().getBlock().getTypeId());
					if(plugin.stringToBool(plugin.thunderTower.getProperties(event.getWorld()).getProperty("active", "true"))
							&& plugin.thunderTower.getProperties(event.getWorld()).getTypeID() == event.getLightning().getLocation().getBlock().getTypeId()) {
						//System.out.println("Searching for a thunder tower!");
						//Let's not destroy the user's thunder towers...
						ArrayList<int[]> ttlist = plugin.thunderTower.getTowersList(plugin.getServer().getWorld("world")).list;
						for(int j = 0; j < ttlist.size() && towernotfound; j++) {
							int[] value = ttlist.get(j);
							Location lightingstrike = event.getLightning().getLocation();
							if(value[0] == lightingstrike.getBlockX() && value[1] == lightingstrike.getBlockY() && value[2] == lightingstrike.getBlockZ()) {
								towernotfound = false;
							}
						}
					}
					if(towernotfound) {
						Location loc = event.getLightning().getLocation();
						//System.out.println("Tower not found! Activating super lightning!");
						((org.bukkit.craftbukkit.CraftWorld)event.getWorld()).getHandle().createExplosion(null, loc.getX(), loc.getY(), loc.getZ(), (float)plugin.getIntValue("supercharged-explosion-radius", event.getWorld().getName(), 3), true);
					}else {
						//System.out.println("Tower found! Not activating super lightning!");
					}
				}

				//System.out.println("Lightning hit: " + event.getLightning().getLocation().getBlockX() + ", " + event.getLightning().getLocation().getBlockY() + ", " + event.getLightning().getLocation().getBlockZ());

			}
		}
	}
}
package tux2.weatherrestrictions;

import java.util.Random;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WeatherRestrictionsWeatherListener implements Listener {
	private WeatherRestrictions plugin;
	Random rand = new Random();
	
	public WeatherRestrictionsWeatherListener( WeatherRestrictions plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public synchronized void onWeatherChange( WeatherChangeEvent event ) {
		if( !event.isCancelled() && event.toWeatherState() && plugin.isNodeDisabled( "disable-weather", event.getWorld().getName() ) ) {
			event.setCancelled( true );
		}else {
			long lasttime = 0;
			try {
				lasttime = plugin.lastweather.get(event.getWorld().getName());
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
				//set the ratio of thunderstorms...
				if(new Random().nextInt(10000) <= (plugin.getDoubleValue("thunderstorm-chance", event.getWorld().getName(), 35)*100)) {
					//If so, let's add some thunder in the mix
					event.getWorld().setThundering(true);
				}else {
					event.getWorld().setThundering(false);
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
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public synchronized void onThunderChange( ThunderChangeEvent event ) {
		if( !event.isCancelled() && event.toThunderState() && plugin.isNodeDisabled( "disable-thunder", event.getWorld().getName() ) ) {
			event.setCancelled( true );
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public synchronized void onLightningStrike( LightningStrikeEvent event ) {
		if( !event.isCancelled() && plugin.isNodeDisabled( "disable-lightning", event.getWorld().getName() ) ) {
			event.setCancelled( true );
			//Capture that lightning event and add a percent chance that it is supercharged.
		}else if( !event.isCancelled()) {
			if (rand.nextInt(10000) >= (10000.0 - (plugin.getDoubleValue("supercharged-thunder-chance", event.getWorld().getName(), 0) * 100.0))) {
				if(plugin.thunderTower != null) {
					if(!plugin.thunderTower.isThunderTowerTop(event.getLightning().getLocation())) {
						Location loc = event.getLightning().getLocation();
						event.getWorld().createExplosion(loc, (float)plugin.getIntValue("supercharged-explosion-radius", event.getWorld().getName(), 3), true);
					}
				}else {
					Location loc = event.getLightning().getLocation();
					event.getWorld().createExplosion(loc, (float)plugin.getIntValue("supercharged-explosion-radius", event.getWorld().getName(), 3), true);
				}

			}
		}
	}
}
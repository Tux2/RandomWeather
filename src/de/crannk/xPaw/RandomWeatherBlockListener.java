package de.crannk.xPaw;

import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SnowFormEvent;


public class RandomWeatherBlockListener extends BlockListener {
	
	RandomWeather plugin;

	public RandomWeatherBlockListener(RandomWeather plugin) {
		this.plugin = plugin;
	}
	
	public void onSnowForm(SnowFormEvent event) {
		if(!event.isCancelled() && plugin.isNodeDisabled("disable-snow-accumulation", event.getBlock().getWorld().getName())) {
			event.setCancelled(true);
		}
	}

}

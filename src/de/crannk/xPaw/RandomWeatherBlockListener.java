package de.crannk.xPaw;

import org.bukkit.Material;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockListener;


public class RandomWeatherBlockListener extends BlockListener {
	
	RandomWeather plugin;

	public RandomWeatherBlockListener(RandomWeather plugin) {
		this.plugin = plugin;
	}
	
	/*
	public void onSnowForm(SnowFormEvent event) {
		if(!event.isCancelled() && plugin.isNodeDisabled("disable-snow-accumulation", event.getBlock().getWorld().getName())) {
			event.setCancelled(true);
		}
	}*/
	
	public void onBlockForm(BlockFormEvent event) {
		if(!event.isCancelled() && event.getNewState().getType() == Material.ICE) {
			if(plugin.isNodeDisabled("disable-ice-accumulation", event.getBlock().getWorld().getName())) {
				event.setCancelled(true);
			}
		}else if(!event.isCancelled() && event.getNewState().getType() == Material.SNOW && plugin.isNodeDisabled("disable-snow-accumulation", event.getBlock().getWorld().getName())) {
			//System.out.println("Snow tried to form!");
			event.setCancelled(true);
		}
	}

}

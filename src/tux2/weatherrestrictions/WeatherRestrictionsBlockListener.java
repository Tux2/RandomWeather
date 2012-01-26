package tux2.weatherrestrictions;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;


public class WeatherRestrictionsBlockListener implements Listener {
	
	WeatherRestrictions plugin;

	public WeatherRestrictionsBlockListener(WeatherRestrictions plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
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

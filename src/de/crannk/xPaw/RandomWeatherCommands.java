package de.crannk.xPaw;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandomWeatherCommands implements CommandExecutor {
	
	RandomWeather plugin;

	public RandomWeatherCommands(RandomWeather randomWeather) {
		super();
		plugin = randomWeather;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if(commandLabel.equalsIgnoreCase("rain")){
				if(plugin.hasPermissions(player, "RandomWeather.rain")) {
					try {
						if(args.length != 0) {
							long lasttime = 0;
							try {
								lasttime = (Long) plugin.lastweather.get(args[0]);
							}catch(Exception e) {
								
							}
							if(plugin.isNodeDisabled("disable-weather", args[0])) {
								player.sendMessage(ChatColor.RED + "Uhoh, weather is disabled in this world.");
							}else if((plugin.getIntValue("minimum-rain-wait", args[0], 600)*1000) >= System.currentTimeMillis() - lasttime) {
								player.sendMessage(ChatColor.RED + "Please wait, you can't trigger a storm just yet.");
							}else {
								sender.getServer().getWorld(args[0]).setStorm(true);
								player.sendMessage(ChatColor.GREEN + "It starts to rain in world \"" + args[0] + "\"");
							}
						}else {
							String theworld = player.getWorld().getName();
							long lasttime = 0;
							try {
								lasttime = (Long) plugin.lastweather.get(theworld);
							}catch(Exception e) {
								
							}
							if(plugin.isNodeDisabled("disable-weather", theworld)) {
								player.sendMessage(ChatColor.RED + "Uhoh, weather is disabled in this world.");
							}else if((plugin.getIntValue("minimum-rain-wait", theworld, 600)*1000) >= System.currentTimeMillis() - lasttime) {
								player.sendMessage(ChatColor.RED + "Please wait, you can't trigger a storm just yet.");
							}else {
								player.getWorld().setStorm(true);
								player.sendMessage(ChatColor.GREEN + "It starts to rain...");
							}
						}
					}catch (Exception e) {
						return false;
					}
				}
				return true;
			}if(commandLabel.equalsIgnoreCase("thunder")){
				if(plugin.hasPermissions(player, "RandomWeather.thunder")) {
					
					try {
						if(args.length != 0) {
							if(plugin.isNodeDisabled( "disable-thunder",args[0] )) {
								player.sendMessage(ChatColor.RED + "Thunder is disabled in this world, sorry...");
							}else {
								sender.getServer().getWorld(args[0]).setThundering(true);
								player.sendMessage(ChatColor.GREEN + "It starts to thunder in world \"" + args[0] + "\"");
							}
							//plugin.playerweatherevent = player.getName();
						}else {
							if(plugin.isNodeDisabled( "disable-thunder", player.getWorld().getName() )) {
								player.sendMessage(ChatColor.RED + "Thunder is disabled in this world, sorry...");
							}else {
								player.getWorld().setThundering(true);
								player.sendMessage(ChatColor.GREEN + "It starts to thunder...");
							}
							//plugin.playerweatherevent = player.getName();
						}
					}catch (Exception e) {
						return false;
					}
				}
				return true;
			}else if(commandLabel.equalsIgnoreCase("rainclear") || commandLabel.equalsIgnoreCase("clearrain")){
				if(plugin.hasPermissions(player, "RandomWeather.clear")) {
					try {
						if(args.length != 0) {
							sender.getServer().getWorld(args[0]).setStorm(false);
							sender.getServer().getWorld(args[0]).setThundering(false);
							player.sendMessage(ChatColor.GREEN + "You now have clear skies in world \"" + args[0] + "\"");
						}else {
							player.getWorld().setStorm(false);
							player.getWorld().setThundering(false);
							player.sendMessage(ChatColor.GREEN + "You now have clear skies.");
						}
					}catch (Exception e) {
						return false;
					}
				}
				return true;
			}else if(commandLabel.equalsIgnoreCase("weatherstats")){
				if(plugin.hasPermissions(player, "RandomWeather.stats")) {
					try {
						List<World> worlds = sender.getServer().getWorlds();
						for(World theworld : worlds) {
							String status = ChatColor.GREEN + "No Storm";
							if(theworld.hasStorm()) {
								status = ChatColor.BLUE + "Raining";
							}
							if(theworld.isThundering()) {
								status = status + ChatColor.RED + " and Thundering";
							}
							player.sendMessage(ChatColor.YELLOW + theworld.getName() + ": " + status );
						}
					}catch (Exception e) {
						return false;
					}
				}
				return true;
			}else if(commandLabel.equalsIgnoreCase("lightning")){
				if(plugin.hasPermissions(player, "RandomWeather.lightning")) {
					Location loc = player.getLocation().clone();
					Random rand = new Random();
					loc.setX(loc.getX() + (rand.nextInt(20) - 10));
					loc.setZ(loc.getZ() + (rand.nextInt(20) - 10));
					player.getWorld().strikeLightning(loc);
					player.getWorld().strikeLightningEffect(loc);
				}
				return true;
			} //If this has happened the function will break and return true. if this hasn't happened the a value of false will be returned.
		}
		return false; 
	}

}

package de.crannk.xPaw;

import java.util.List;

import org.bukkit.ChatColor;
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
							sender.getServer().getWorld(args[0]).setStorm(true);
							player.sendMessage(ChatColor.GREEN + "It starts to rain in world \"" + args[0] + "\"");
						}else {
							player.getWorld().setStorm(true);
							player.sendMessage(ChatColor.GREEN + "It starts to rain...");
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
							sender.getServer().getWorld(args[0]).setThundering(true);
							player.sendMessage(ChatColor.GREEN + "It starts to thunder in world \"" + args[0] + "\"");
						}else {
							player.getWorld().setThundering(true);
							player.sendMessage(ChatColor.GREEN + "It starts to thunder...");
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
			} //If this has happened the function will break and return true. if this hasn't happened the a value of false will be returned.
		}
		return false; 
	}

}

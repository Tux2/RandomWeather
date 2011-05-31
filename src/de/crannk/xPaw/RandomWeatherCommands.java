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

	public synchronized boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player) {
			Player player = (Player)sender;
			if(commandLabel.equalsIgnoreCase("rain")){
				return rain(player, args);
			}if(commandLabel.equalsIgnoreCase("thunder")){
				return thunder(player, args);
			}else if(commandLabel.equalsIgnoreCase("rainclear") || commandLabel.equalsIgnoreCase("clearrain")){
				return clearrain(player, args);
			}else if(commandLabel.equalsIgnoreCase("weatherstats")){
				return stats(player);
			}else if(commandLabel.equalsIgnoreCase("lightning")){
				return lightning(player);
			}else if(commandLabel.equalsIgnoreCase("wr")) {
				if(args.length == 0) {
					if(plugin.hasPermissions(player, "RandomWeather.help")) {
						player.sendMessage(ChatColor.GREEN + "Weather Restrictions Command list:");
						if(plugin.hasPermissions(player, "RandomWeather.rain")) {
							player.sendMessage(ChatColor.GREEN + "/wr rain [world] - make it rain");
						}
						if(plugin.hasPermissions(player, "RandomWeather.thunder")) {
							player.sendMessage(ChatColor.GREEN + "/wr thunder [world] - make it a thunderstorm");
						}
						if(plugin.hasPermissions(player, "RandomWeather.clear")) {
							player.sendMessage(ChatColor.GREEN + "/wr clear [world] - clears all weather");
						}
						if(plugin.hasPermissions(player, "RandomWeather.lightning")) {
							player.sendMessage(ChatColor.GREEN + "/wr lightning - send a lightning bolt near you");
						}
						if(plugin.hasPermissions(player, "RandomWeather.stats")) {
							player.sendMessage(ChatColor.GREEN + "/wr stats - shows you the current weather in all worlds");
						}
					}
				}else {
					String[] args2 = new String[args.length - 1];
					if(args.length > 1) {
						for(int i = 1; i < args.length; i++) {
							args2[i - 1] = args[i];
						}
					}
					String commandLabel2 = args[0];
					if(commandLabel2.equalsIgnoreCase("rain")){
						return rain(player, args2);
					}if(commandLabel2.equalsIgnoreCase("thunder")){
						return thunder(player, args2);
					}else if(commandLabel2.equalsIgnoreCase("clear")){
						return clearrain(player, args2);
					}else if(commandLabel2.equalsIgnoreCase("stats")){
						return stats(player);
					}else if(commandLabel2.equalsIgnoreCase("lightning")){
						return lightning(player);
					}
				}
			}
		}
		return false; 
	}
	
	private synchronized boolean rain (Player player, String args[]){
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
						player.getServer().getWorld(args[0]).setStorm(true);
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
	}
	
	private synchronized boolean thunder (Player player, String args[]){
		if(plugin.hasPermissions(player, "RandomWeather.thunder")) {
			
			try {
				if(args.length != 0) {
					if(plugin.isNodeDisabled( "disable-thunder",args[0] )) {
						player.sendMessage(ChatColor.RED + "Thunder is disabled in this world, sorry...");
					}else {
						player.getServer().getWorld(args[0]).setThundering(true);
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
	}
	
	private synchronized boolean clearrain (Player player, String args[]){
		if(plugin.hasPermissions(player, "RandomWeather.clear")) {
			try {
				if(args.length != 0) {
					player.getServer().getWorld(args[0]).setStorm(false);
					player.getServer().getWorld(args[0]).setThundering(false);
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
	}
	
	private synchronized boolean stats (Player player) {
		if(plugin.hasPermissions(player, "RandomWeather.stats")) {
			try {
				List<World> worlds = player.getServer().getWorlds();
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
	}
	
	private synchronized boolean lightning (Player player) {
		if(plugin.hasPermissions(player, "RandomWeather.lightning")) {
			Location loc = player.getLocation().clone();
			Random rand = new Random();
			loc.setX(loc.getX() + (rand.nextInt(20) - 10));
			loc.setZ(loc.getZ() + (rand.nextInt(20) - 10));
			player.getWorld().strikeLightning(loc);
			player.getWorld().strikeLightningEffect(loc);
		}
		return true;
	}

}

package tux2.weatherrestrictions;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherRestrictionsCommands implements CommandExecutor {
	
	WeatherRestrictions plugin;

	public WeatherRestrictionsCommands(WeatherRestrictions randomWeather) {
		super();
		plugin = randomWeather;
	}

	public synchronized boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if(commandLabel.equalsIgnoreCase("rain")){
			return rain(sender, args);
		}if(commandLabel.equalsIgnoreCase("thunder")){
			return thunder(sender, args);
		}else if(commandLabel.equalsIgnoreCase("rainclear") || commandLabel.equalsIgnoreCase("clearrain")){
			return clearrain(sender, args);
		}else if(commandLabel.equalsIgnoreCase("weatherstats")){
			return stats(sender);
		}else if(commandLabel.equalsIgnoreCase("lightning") && player != null){
			return lightning(player);
		}else if(commandLabel.equalsIgnoreCase("wr")) {
			if(args.length == 0) {
				if(player != null) {
					if(plugin.hasPermissions(player, "weatherRestrictions.help")) {
						player.sendMessage(ChatColor.GREEN + "Weather Restrictions Command list:");
						if(plugin.hasPermissions(player, "weatherRestrictions.rain")) {
							player.sendMessage(ChatColor.GREEN + "/wr rain [world] - make it rain");
						}
						if(plugin.hasPermissions(player, "weatherRestrictions.thunder")) {
							player.sendMessage(ChatColor.GREEN + "/wr thunder [world] - make it a thunderstorm");
						}
						if(plugin.hasPermissions(player, "weatherRestrictions.clear")) {
							player.sendMessage(ChatColor.GREEN + "/wr clear [world] - clears all weather");
						}
						if(plugin.hasPermissions(player, "weatherRestrictions.lightning")) {
							player.sendMessage(ChatColor.GREEN + "/wr lightning - send a lightning bolt near you");
						}
						if(plugin.hasPermissions(player, "weatherRestrictions.stats")) {
							player.sendMessage(ChatColor.GREEN + "/wr stats - shows you the current weather in all worlds");
						}
					}
					return true;
				}else {
					sender.sendMessage(ChatColor.GREEN + "Weather Restrictions Command list:");
					sender.sendMessage(ChatColor.GREEN + "/wr rain [world] - make it rain");
					sender.sendMessage(ChatColor.GREEN + "/wr thunder [world] - make it a thunderstorm");
					sender.sendMessage(ChatColor.GREEN + "/wr clear [world] - clears all weather");
					sender.sendMessage(ChatColor.GREEN + "/wr stats - shows you the current weather in all worlds");
					return true;
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
					return rain(sender, args2);
				}if(commandLabel2.equalsIgnoreCase("thunder")){
					return thunder(sender, args2);
				}else if(commandLabel2.equalsIgnoreCase("clear")){
					return clearrain(sender, args2);
				}else if(commandLabel2.equalsIgnoreCase("stats")){
					return stats(sender);
				}else if(commandLabel2.equalsIgnoreCase("lightning")){
					return lightning(player);
				}
			}
		}
		return false; 
	}
	
	private synchronized boolean rain (CommandSender sender, String args[]){
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if(player != null && !plugin.hasPermissions(player, "weatherRestrictions.rain")) {
			return true;
		}
		try {
			if(args.length != 0) {
				long lasttime = 0;
				try {
					lasttime = plugin.lastweather.get(args[0]);
				}catch(Exception e) {
					
				}
				if(plugin.isNodeDisabled("disable-weather", args[0])) {
					sender.sendMessage(ChatColor.RED + "Uhoh, weather is disabled in this world.");
				}else if((plugin.getIntValue("minimum-rain-wait", args[0], 600)*1000) >= System.currentTimeMillis() - lasttime) {
					sender.sendMessage(ChatColor.RED + "Please wait, you can't trigger a storm just yet.");
				}else {
					sender.getServer().getWorld(args[0]).setStorm(true);
					sender.sendMessage(ChatColor.GREEN + "It starts to rain in world \"" + args[0] + "\"");
				}
			}else {
				if(player == null) {
					return false;
				}
				String theworld = player.getWorld().getName();
				long lasttime = 0;
				try {
					lasttime = plugin.lastweather.get(theworld);
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
	
		return true;
	}
	
	private synchronized boolean thunder (CommandSender sender, String args[]){
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if(player != null && !plugin.hasPermissions(player, "weatherRestrictions.thunder")) {
			return true;
		}
		
		try {
			if(args.length != 0) {
				if(plugin.isNodeDisabled( "disable-thunder",args[0] )) {
					sender.sendMessage(ChatColor.RED + "Thunder is disabled in this world, sorry...");
				}else {
					sender.getServer().getWorld(args[0]).setThundering(true);
					sender.sendMessage(ChatColor.GREEN + "It starts to thunder in world \"" + args[0] + "\"");
				}
			}else {
				if(player == null) {
					return false;
				}
				if(plugin.isNodeDisabled( "disable-thunder", player.getWorld().getName() )) {
					player.sendMessage(ChatColor.RED + "Thunder is disabled in this world, sorry...");
				}else {
					player.getWorld().setThundering(true);
					player.sendMessage(ChatColor.GREEN + "It starts to thunder...");
				}
			}
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private synchronized boolean clearrain (CommandSender sender, String args[]){
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if(player != null && !plugin.hasPermissions(player, "weatherRestrictions.clear")) {
			return true;
		}
		try {
			if(args.length != 0) {
				sender.getServer().getWorld(args[0]).setStorm(false);
				sender.getServer().getWorld(args[0]).setThundering(false);
				sender.sendMessage(ChatColor.GREEN + "You now have clear skies in world \"" + args[0] + "\"");
			}else {
				if(player == null) {
					return false;
				}
				player.getWorld().setStorm(false);
				player.getWorld().setThundering(false);
				player.sendMessage(ChatColor.GREEN + "You now have clear skies.");
			}
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private synchronized boolean stats (CommandSender sender) {
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if(player != null && !plugin.hasPermissions(player, "weatherRestrictions.stats")) {
			return true;
		}
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
				sender.sendMessage(ChatColor.YELLOW + theworld.getName() + ": " + status );
			}
		}catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private synchronized boolean lightning (Player player) {
		if(plugin.hasPermissions(player, "weatherRestrictions.lightning")) {
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

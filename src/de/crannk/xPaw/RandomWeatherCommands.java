package de.crannk.xPaw;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RandomWeatherCommands implements CommandExecutor {
	
	RandomWeather plugin;

	public RandomWeatherCommands(RandomWeather randomWeather) {
		super();
		plugin = randomWeather;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){

		if(commandLabel.equalsIgnoreCase("rain")){ // If the player typed /basic then do the following...
			//doSomething
			if(sender.isOp()) {
				try {
					sender.getServer().getWorld(args[0]).setStorm(true);
					System.out.println("Telling world \"" + args[0] + "\" to rain.");
				}catch (Exception e) {
					return false;
				}
			}
			return true;
		}else if(commandLabel.equalsIgnoreCase("rainclear")){ // If the player typed /basic then do the following...
			//doSomething
			if(sender.isOp()) {
				try {
					sender.getServer().getWorld(args[0]).setStorm(false);
					sender.getServer().getWorld(args[0]).setThundering(false);
				}catch (Exception e) {
					return false;
				}
			}
			return true;
		} //If this has happened the function will break and return true. if this hasn't happened the a value of false will be returned.
		return false; 
	}

}

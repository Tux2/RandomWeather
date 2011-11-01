package tux2.weatherrestrictions;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.rominos2.ThunderTower.ThunderTower;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class WeatherRestrictions extends JavaPlugin implements Runnable
{
	private static PermissionHandler Permissions;
	ThunderTower thunderTower;
	public FileConfiguration config;
	File yml;
	Thread dispatchThread;
	public final Logger log = Logger.getLogger( "Minecraft" );
	ConcurrentHashMap lastweather = new ConcurrentHashMap();
	LinkedList<WeatherStarts> timeweather = new LinkedList<WeatherStarts>();
	ConcurrentHashMap<String, WeatherStarts> worldsweather = new ConcurrentHashMap<String, WeatherStarts>();
	
	public void onEnable( )
	{
		// Check to see if there is a configuration file.
		// Credits to <Sleaker>
		yml = new File( getDataFolder() + "/config.yml" );
		
		if( !yml.exists() )
		{
			getDataFolder().mkdir();
			
			try
			{
				yml.createNewFile();
			}
			catch( IOException e )
			{
				log.warning( "[WeatherRestrictions] Exception: " + e.getMessage() );
				log.warning( "[WeatherRestrictions] Cannot create configuration file. And none to load, using defaults." );
			}
		}
		setupPermissions();
		setupThunderTower();
		config = getConfig();

		dispatchThread = new Thread(this);
        dispatchThread.start();
		
		final WeatherRestrictionsWeatherListener wL = new WeatherRestrictionsWeatherListener( this );
		final WeatherRestrictionsWorldListener worldL = new WeatherRestrictionsWorldListener( this );
		final WeatherRestrictionsBlockListener blockL = new WeatherRestrictionsBlockListener(this);
		final WeatherRestrictionsCommands commandL = new WeatherRestrictionsCommands( this );
		final PluginManager pm = getServer().getPluginManager();
		final PluginDescriptionFile pdfFile = this.getDescription();
		timeweather.clear();
		worldsweather.clear();
		
		List<World> worlds = getServer().getWorlds();
		
		for( World world : worlds )
		{
			worldL.WorldLoaded( world );
		}
		
		pm.registerEvent( Event.Type.WORLD_LOAD, worldL, Event.Priority.Monitor, this );
		pm.registerEvent( Event.Type.WEATHER_CHANGE, wL, Event.Priority.Highest, this );
		pm.registerEvent( Event.Type.THUNDER_CHANGE, wL, Event.Priority.Highest, this );
		pm.registerEvent( Event.Type.LIGHTNING_STRIKE, wL, Event.Priority.Highest, this );
		//pm.registerEvent(Event.Type.SNOW_FORM, blockL, Event.Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_FORM, blockL, Event.Priority.Highest, this);
		PluginCommand batchcommand = this.getCommand("wr");
		batchcommand.setExecutor(commandL);
		try {
			PluginCommand command = this.getCommand("rain");
			command.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /rain, please use /wr rain");
		}
		try {
			PluginCommand command2 = this.getCommand("rainclear");
			command2.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /rainclear, please use /wr clear");
		}
		try {
			PluginCommand command3 = this.getCommand("clearrain");
			command3.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /clearrain, please use /wr clear");
		}
		try {
			PluginCommand commandthunder = this.getCommand("thunder");
			commandthunder.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /thunder, please use /wr thunder");
		}
		try {
			PluginCommand command5 = this.getCommand("weatherstats");
			command5.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /weatherstats, please use /wr stats");
		}
		try {
			PluginCommand commandlightning = this.getCommand("lightning");
			commandlightning.setExecutor(commandL);
		}catch (NullPointerException e) {
			System.out.println("WeatherRestrictions: Another plugin is using /lightning, please use /wr lightning");
		}
		
		log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
	}
	
	public void onDisable( )
	{
		//
	}
	
	public boolean isNodeDisabled( String name, String worldName, boolean thedefault )
	{
		return config.getBoolean( worldName + "." + name, thedefault );
	}
	
	/**Default is True if not set.**/
	public boolean isNodeDisabled( String name, String worldName )
	{
		return config.getBoolean( worldName + "." + name, true );
	}
	
	public int getIntValue( String name, String worldName, int thedefault )
	{
		return config.getInt(worldName + "." + name, thedefault);
	}
	
	public void setConfigNode( String name, String worldName, Boolean value )
	{
		config.set( worldName + "." + name, value );
	}
	
	public void setConfigNode( String name, String worldName, int value )
	{
		config.set( worldName + "." + name, value );
	}
	
	private void setupPermissions() {
        Plugin permissions = this.getServer().getPluginManager().getPlugin("Permissions");

        if (Permissions == null) {
            if (permissions != null) {
                Permissions = ((Permissions)permissions).getHandler();
            } else {
            }
        }
    }
	
	private void setupThunderTower() {
        ThunderTower tt = (ThunderTower)this.getServer().getPluginManager().getPlugin("ThunderTower");

        if (thunderTower == null) {
            if (tt != null) {
                thunderTower = tt;
            } else {
            }
        }
    }
    
    public static boolean hasPermissions(Player player, String node) {
        if (Permissions != null) {
            return Permissions.has(player, node);
        } else {
            return player.hasPermission(node);
        }
    }

	public double getDoubleValue(String name, String worldName, int thedefault) {
		return config.getDouble(worldName + "." + name, thedefault);
	}

	public void setConfigNode(String name, String worldName,
			double value) {
		config.set( worldName + "." + name, value );
		
	}
	
	boolean stringToBool(String thebool) {
		boolean result;
		if (thebool.trim().equalsIgnoreCase("true") || thebool.trim().equalsIgnoreCase("yes")) {
	    	result = true;
	    } else {
	    	result = false;
	    }
		return result;
	}

	@Override
	public synchronized void run() {
		boolean running = true;
        while (running) {

            // If the list is empty, wait until something gets added.
            if (timeweather.size() == 0) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    // Do nothing.
                }
                
            }

            WeatherStarts reminder = (WeatherStarts) timeweather.getFirst();
            long delay = reminder.getDueTime() - System.currentTimeMillis();
            if (delay > 0) {
                try {
                    wait(delay);
                }
                catch (InterruptedException e) {
                    // A new weather event was added. Sort the list.
                    Collections.sort(timeweather);
                }
            }
            else {
            	//Remove this before another thread might add it back...
            	timeweather.remove(reminder);
            	getServer().getScheduler().scheduleSyncDelayedTask(this, new DoRain(this, reminder));
            	/*if(reminder.getType() == WeatherStarts.STARTRAIN) {
            		
            		getServer().getWorld(reminder.getWorld()).setStorm(true);
            	}else if(reminder.getType() == WeatherStarts.STOPRAIN) {
            		getServer().getWorld(reminder.getWorld()).setStorm(false);
            		getServer().getWorld(reminder.getWorld()).setThundering(false);
            	}*/
            }
        }
	}
}
package tux2.weatherrestrictions;

import java.io.Serializable;

public class WeatherStarts implements Comparable {
	
	private long dueTime;
	private String world;
	int type = 0;
	public static final int STARTRAIN = 1;
	public static final int STOPRAIN = 2;
	
	public WeatherStarts(String world, long duetime, int type) {
		this.world = world;
		dueTime = duetime;
		this.type = type;
	}
	
	public synchronized int compareTo(Object o) {
        if (o instanceof WeatherStarts) {
        	WeatherStarts other = (WeatherStarts) o;
            if (dueTime < other.dueTime) {
                return -1;
            }
            else if (dueTime > other.dueTime) {
                return 1;
            }
        }
        return 0;
    }

	public synchronized long getDueTime() {
		return dueTime;
	}

	public synchronized void setDueTime(long dueTime) {
		this.dueTime = dueTime;
	}

	public synchronized String getWorld() {
		return world;
	}

	public synchronized void setWorld(String world) {
		this.world = world;
	}

	public synchronized int getType() {
		return type;
	}

	public synchronized void setType(int type) {
		this.type = type;
	}

}

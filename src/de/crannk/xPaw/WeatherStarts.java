package de.crannk.xPaw;

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
	
	public int compareTo(Object o) {
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

	public long getDueTime() {
		return dueTime;
	}

	public void setDueTime(long dueTime) {
		this.dueTime = dueTime;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}

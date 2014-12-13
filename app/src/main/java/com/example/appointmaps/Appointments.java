package com.example.appointmaps;

import android.location.Location;
import android.text.format.Time;

public class Appointments {
	public int hour;
    public int minute;
	public Location originLocation;
	public Location destLocation;
	public String content;

	public Appointments(String title, int _hour, int _minute) {
        hour = _hour;
        minute = _minute;
        content = title;
	}

	public Location getLocation() {
		return destLocation;
	}

	public String getTitle() {
		return content;
	}
}

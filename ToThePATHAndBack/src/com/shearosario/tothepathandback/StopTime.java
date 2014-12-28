package com.shearosario.tothepathandback;

import android.content.Context;
import android.text.format.DateFormat;

/**
 * Times that a vehicle arrives at and departs from individual stops for each trip.
 * 
 * @author Jose Andres Rosario
 */
/**
 * @author Jose Andres Rosario
 *
 */
public class StopTime implements Comparable<StopTime>
{
	/**
	 * The trip_headsign field contains the text that appears on a sign that
	 * identifies the trip's destination to passengers. Use this field to
	 * distinguish between different patterns of service in the same route.
	 */
	private String trip_headsign;
	/**
	 * Either 0 or 1, determines what direction the trip is going.
	 */
	private String direction_id;
	/**
	 * The route_id field contains an ID that uniquely identifies a route. The
	 * route_id is dataset unique.
	 */
	private String route_id;
	/**
	 * The route_long_name contains the full name of a route.
	 */
	private String route_long_name;
	/**
	 * <p>The departure_time specifies the departure time from a specific stop for
	 * a specific trip on a route. The time is measured from "noon minus 12h"
	 * (effectively midnight, except for days on which daylight savings time
	 * changes occur) at the beginning of the service date. Times must be eight
	 * digits in HH:MM:SS format (H:MM:SS is also accepted, if the hour begins
	 * with 0).</p>
	 * 
	 * <p>Note: Trips that span multiple dates will have stop times greater than
	 * 24:00:00. For example, if a trip begins at 10:30:00 p.m. and ends at
	 * 2:15:00 a.m. on the following day, the stop times would be 22:30:00 and
	 * 26:15:00. Entering those stop times as 22:30:00 and 02:15:00 would not
	 * produce the desired results.</p>
	 */
	private int departure_hour, am_pm;
	private String departure_minute;
	
	private String dayOfWeek;
	
	/**
	 * Create a new StopTime object
	 * 
	 * @param trip_id identifies this trip
	 * @param arrival_time the arrivate time from this stop on this trip
	 * @param departure_time the departure time from this stop on this trip
	 * @param stop_id uniquely identifies this stop
	 * @param stop_sequence identifies the order of the stops for a particular trip
	 */
	public StopTime(int departure_hour, String departure_minute, int am_pm, String route_long_name,
			String trip_headsign, String direction_id, String route_id)
	{
		this.departure_hour = departure_hour;
		this.departure_minute = departure_minute;
		this.am_pm = am_pm;
		this.trip_headsign = trip_headsign;
		this.direction_id = direction_id;
		this.route_id = route_id;
		this.route_long_name = route_long_name;
		this.dayOfWeek = null;
	}
	
	/**
	 * Creates a new StopTime object that is a copy of an already existing StopTime
	 * object
	 * 
	 * @param sT a StopTime object
	 */
	public StopTime (StopTime sT)
	{
		this.departure_hour = sT.getDeparture_hour();
		this.departure_minute = sT.getDeparture_minute();
		this.am_pm = sT.getAm_pm();
		this.trip_headsign = sT.getTrip_headsign();
		this.direction_id = sT.getDirection_id();
		this.route_id = sT.getRoute_id();
		this.route_long_name = sT.getRoute_long_name();
		this.dayOfWeek = sT.getDayOfWeek();
	}

	/**
	 * @return the trip_headsign
	 */
	public String getTrip_headsign() {
		return trip_headsign;
	}

	/**
	 * @return the direction_id
	 */
	public String getDirection_id() {
		return direction_id;
	}

	/**
	 * @return the route_id
	 */
	public String getRoute_id() {
		return route_id;
	}

	/**
	 * @return the route_long_name
	 */
	public String getRoute_long_name() {
		return route_long_name;
	}

	@Override
	public int compareTo(StopTime another) {
		String another_minute = another.getDeparture_minute();
		int another_hour = another.getDeparture_hour();
		
		if (this.departure_hour > another_hour)
			return 1;
		else if (this.departure_hour < another_hour)
			return -1;
		else
			return (this.departure_minute).compareToIgnoreCase(another_minute);
	}

	/**
	 * @return the departure_hour
	 */
	private int getDeparture_hour() {
		return departure_hour;
	}

	/**
	 * @return the departure_minute
	 */
	private String getDeparture_minute() {
		return departure_minute;
	}

	/**
	 * @return the am_pm
	 */
	private int getAm_pm() {
		return am_pm;
	}

	public String getDeparture_time(Context c) {
		if (DateFormat.is24HourFormat(c))
		{
			String time = Integer.toString(this.departure_hour) + ":" + this.departure_minute;
			if (dayOfWeek != null)
				time = time + " " + dayOfWeek;
			return time;
		}
		
		String hour = null;
		if (this.departure_hour > 12)
			hour = Integer.toString(this.departure_hour - 12);
		else if (this.departure_hour == 0)
			hour = "12";
		else 
			hour = Integer.toString(this.departure_hour);
		
		String amPM = null;
		if (am_pm == 0)
			amPM = "AM";
		else
			amPM = "PM";
		
		if (dayOfWeek != null)
			amPM = amPM + " " + dayOfWeek;
		
		return hour + ":" + this.departure_minute + " " + amPM;
	}

	/**
	 * @return the dayOfWeek
	 */
	public String getDayOfWeek() {
		return dayOfWeek;
	}

	/**
	 * @param dayOfWeek the dayOfWeek to set
	 */
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	/**
	 * @param trip_headsign the trip_headsign to set
	 */
	public void setTrip_headsign(String trip_headsign) {
		this.trip_headsign = trip_headsign;
	}
}

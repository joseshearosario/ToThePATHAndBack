package com.shearosario.tothepathandback;

import com.google.android.gms.maps.model.LatLng;

/**
 * <p>
 * Representation of an entrance to a station in the PATH system. 
 * Included is the station name, unique entrance ID and the ID of their associated station, 
 * any notes for the entrance, it's latitude and longitude, and whether the entrance:
 * <ul>
 * <li>Is an elevator</li>
 * <li>Is an escalator</li>
 * <li>Only accesses a NY-bound platform</li>
 * <li>Only accesses a NJ-bound platform</li>
 * <ul>
 * </p>
 * 
 * @author shea
 */

public class Entrance {
	/**
	 * Public variables for Entrance class
	 */
	public String entranceid, stationid, name, notes;
	public boolean escalator, elevator, nybound, njbound;
	public LatLng entranceLocation;
	
	/**
	 * Constructor for Entrance object 
	 *  
	 * @param eid - Unique entrance ID
	 * @param sid - Unique station ID for the station this entrance is associated with
	 * @param n - Station name this entrance is associated with
	 * @param nt - Any notes for this entrance
	 * @param es - Whether this entrance is an escalator
	 * @param el - Whether this entrance is an elevator
	 * @param ny - Whether this only accesses a NY-bound platform
	 * @param nj - Whether this only accesses a NJ-bound platform
	 * @param lat - The latitude of this entrance
	 * @param lon - The longitude of this entrance
	 */
	public Entrance (String eid, String sid, String n, String nt, int es, int el, int ny, int nj, double lat, double lon)
	{
		entranceid = eid;
		stationid = sid;
		name = n;
		notes = nt;
		
		if (es == 1)
			escalator = true;
		else 
			escalator = false;
		
		if (el == 1)
			elevator = true;
		else
			elevator = false;
			
		if (ny == 1)
			nybound = true;
		else
			nybound = false;
		
		if (nj == 1)
			njbound = true;
		else
			njbound = false;
		
		entranceLocation = new LatLng(lat, lon);
	}

	/**
	 * Obtain the unique ID for this entrance
	 * 
	 * @return the entranceid
	 */
	public String getEntranceid() {
		return entranceid;
	}

	/**
	 * Return the unique ID of the station that this entrance is associated with
	 * 
	 * @return the stationid
	 */
	public String getStationid() {
		return stationid;
	}

	/**
	 * Get the name of the station that this entrance is associated with
	 * 
	 * @return the name
	 */
	public String getStationName() {
		return name;
	}

	/**
	 * Return the notes for this entrance. If there are any notes, they're usually 
	 * just time restrictions on the entrance (e.g. Not opened between 1900 - 0700)
	 * 
	 * @return the notes
	 */
	public String getEntranceNotes() {
		return notes;
	}

	/**
	 * If the entrance is an elevator, it will return true. Otherwise, false.
	 * 
	 * @return the elevator
	 */
	public boolean isElevator() {
		return elevator;
	}

	/**
	 * If the entrance only has access to the platform where trains are bound for NY 
	 * terminals, return true. Otherwise, return false. 
	 * 
	 * @return the nybound
	 */
	public boolean isNybound() {
		return nybound;
	}

	/**
	 * If the entrance only has access to the platform where trains are bound for NJ 
	 * terminals, return true. Otherwise, return false.
	 * 
	 * @return the njbound
	 */
	public boolean isNjbound() {
		return njbound;
	}
	
	/**
	 * Return true if the entrance is an escalator. Otherwise, false.
	 * 
	 * @return the escalator
	 */
	public boolean isEscalator() {
		return escalator;
	}

	/**
	 * The LatLng object referring to the geolocation of the entrance is returned.
	 * 
	 * @return the entranceLocation
	 */
	public LatLng getEntranceLocation() {
		return entranceLocation;
	}
}

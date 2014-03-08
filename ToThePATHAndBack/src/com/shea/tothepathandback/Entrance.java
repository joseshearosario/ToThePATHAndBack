package com.shea.tothepathandback;

import com.google.android.gms.maps.model.LatLng;

public class Entrance {
	public String entranceid, stationid, name, notes;
	public boolean escalator, elevator, nybound, njbound;
	public LatLng entranceLocation;
	
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
	 * @return the entranceid
	 */
	public String getEntranceid() {
		return entranceid;
	}

	/**
	 * @return the stationid
	 */
	public String getStationid() {
		return stationid;
	}

	/**
	 * @return the name
	 */
	public String getStationName() {
		return name;
	}

	/**
	 * @return the notes
	 */
	public String getEntranceNotes() {
		return notes;
	}

	/**
	 * @return the elevator
	 */
	public boolean isElevator() {
		return elevator;
	}

	/**
	 * @return the nybound
	 */
	public boolean isNybound() {
		return nybound;
	}

	/**
	 * @return the njbound
	 */
	public boolean isNjbound() {
		return njbound;
	}
	
	/**
	 * @return the escalator
	 */
	public boolean isEscalator() {
		return escalator;
	}
	
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return entranceLocation.latitude;
	}
	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return entranceLocation.longitude;
	}

	/**
	 * @return the entranceLocation
	 */
	public LatLng getEntranceLocation() {
		return entranceLocation;
	}
}

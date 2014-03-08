package com.shea.tothepathandback;

import com.google.android.gms.maps.model.LatLng;
import com.shea.tothepathandback.DatabaseHandler;
import com.shea.tothepathandback.Entrance;

public class Station {
	public String stationID, stationName, stationCity, stationState;
	public LatLng stationLocation;
	public Entrance[] entranceList;
	
	public Station (String id, String name, String city, String state, double lat, double lon) {
		stationID = id;
		stationName = name;
		stationCity = city;
		stationState = state;
		stationLocation = new LatLng(lat,lon);
	}
	
	public void setEntranceList (DatabaseHandler db)
	{
		entranceList = db.getAllEntrancesForStation(this);
	}
	
	public Entrance[] getEntranceList ()
	{
		return entranceList;
	}
	
	public String getStationID () {
		return stationID;
	}
	public String getStationName () {
		return stationName; 
	}
	public String getStationCity () {
		return stationCity;
	}
	public String getStationState () {
		return stationState;
	}
	public double getLatitude () {
		return stationLocation.latitude;
	}
	public double getLongitude () {
		return stationLocation.longitude;
	}
/*
	public void setStationID (String s) {
		stationID = s;
	}
	public void setStationName (String s) {
		stationName = s;
	}
	public void setStationCity (String s) {
		stationCity = s;
	}
	public void setStationState (String s) {
		stationState = s;
	}
	public void setLatitude (double lat) {
		loc.setLatitude(lat);
	}
	public void setLongitude (double lon) {
		loc.setLongitude(lon);
	}*/

	/**
	 * @return the stationLocation
	 */
	public LatLng getStationLocation() {
		return stationLocation;
	}
}
package com.shea.tothepathandback;

import com.google.android.gms.maps.model.LatLng;
import com.shea.tothepathandback.DatabaseHandler;
import com.shea.tothepathandback.Entrance;

/**
 * @author shea
 *
 * Representation of a station in the PATH system. 
 * Included is the station name, unique ID, city and state, and it's latitude and longitude. 
 * The location given in the station is not an entrance, but rather the given location reported by
 * the Port Authority and Google Maps.
 * In addition to its defining characteristics, each station includes a Entrance array holding all 
 * the entrances to that station. These entrances are obtained from an imported database.  
 */
public class Station
{
	/**
	 * public variables for Station class
	 */
	public String stationID, stationName, stationCity, stationState;
	public LatLng stationLocation;
	public Entrance[] entranceList;
	
	/**
	 * Constructor for Station class
	 * 
	 * @param id - the unique identification for that station
	 * @param name - the name of the station
	 * @param city - the town/city where the station is located
	 * @param state - the state where the station is located
	 * @param lat - the latitude of the station
	 * @param lon - the longitude of the station
	 */
	public Station (String id, String name, String city, String state, double lat, double lon)
	{
		stationID = id;
		stationName = name;
		stationCity = city;
		stationState = state;
		stationLocation = new LatLng(lat,lon);
	}
	
	/**
	 * Obtains all the entrances for this station from the passed database
	 * Called in MainActivity
	 * 
	 * @param db - A database containing all stations and entrances
	 * @see com.shea.tothepathandback.DatabaseHandler 
	 */
	public void setEntranceList (DatabaseHandler db)
	{
		entranceList = db.getAllEntrancesForStation(this);
	}

	/**
	 * Returns the stationID associated with this station
	 * 
	 * @return the stationID
	 */
	public String getStationID() 
	{
		return stationID;
	}

	/**
	 * Returns the full name of this station
	 * 
	 * @return the stationName
	 */
	public String getStationName() 
	{
		return stationName;
	}

	/**
	 * Returns the name of the town/city where this station is located
	 * 
	 * @return the stationCity
	 */
	public String getStationCity() 
	{
		return stationCity;
	}

	/**
	 * Returns the state this station is located
	 * Will either be NY or NJ
	 * 
	 * @return the stationState
	 */
	public String getStationState() 
	{
		return stationState;
	}

	/**
	 * Returns the geolocation of this station as a LatLng object, which holds the latitude and longitude of it
	 * 
	 * @return the stationLocation
	 */
	public LatLng getStationLocation() 
	{
		return stationLocation;
	}

	/**
	 * Returns all the entrances to this station as an array
	 * Should be called after all entrances are obtained from an appropriate database
	 * 
	 * @return the entranceList
	 */
	public Entrance[] getEntranceList() 
	{
		return entranceList;
	}
}
package com.shearosario.tothepathandback;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import com.shearosario.tothepathandback.DatabaseHandler;
import com.shearosario.tothepathandback.Entrance;

/**
 * @author shea
 * 
 *         A class representing a station in the PATH system.
 *         <p>
 *         Included in each station is its name, unique ID, city and state, and
 *         its latitude and longitude. The location given in the station is not
 *         an entrance, but rather the given location reported by the Port
 *         Authority and Google Maps. In addition, each station includes a
 *         Entrance ArrayList holding all the entrances to that station. These
 *         entrances are obtained from an imported database.
 */
public class Station implements Parcelable
{
	/**
	 * private variables for Station class
	 */
	private String stationID, stationName, stationCity, stationState;
	private double station_lat, station_lon;
	private ArrayList<Entrance> entranceList;
	private ArrayList<Entrance> handicapAccessEntrances;
	
	/**
	 * Constructor for Station
	 * 
	 * @param id unique identification number for station
	 * @param name official name of station
	 * @param city town/city where the station is located
	 * @param state state where the station is located
	 * @param lat latitude of the station
	 * @param lon longitude of the station
	 */
	public Station (String id, String name, String city, String state, double lat, double lon)
	{
		stationID = id;
		stationName = name;
		stationCity = city;
		stationState = state;
		station_lat = lat;
		station_lon = lon;
	}
	
	/**
	 * Constructor for a parcelable Station
	 * <p>
	 * Creates a Station object in the same order as writeToParcel
	 * 
	 * @see #writeToParcel(Parcel, int)
	 * @param source Parcel argument
	 */
	public Station(Parcel source) {
		this.stationID = source.readString();
		this.stationName = source.readString();
		this.stationCity = source.readString();
		this.stationState = source.readString();
		this.station_lat = source.readDouble();
		this.station_lon = source.readDouble();
		this.entranceList = new ArrayList<Entrance>();
		source.readTypedList(entranceList, Entrance.CREATOR);
		this.handicapAccessEntrances = new ArrayList<Entrance>();
		source.readTypedList(handicapAccessEntrances, Entrance.CREATOR);
	}

	/**
	 * Obtains all the entrances for this station from the passed database, and then creates 
	 * a separate ArrayList of handicap accessible entrances.
	 * 
	 * @param db A database containing all stations and entrances
	 * @see DatabaseHandler#getAllEntrancesForStation(Station)
	 */	
	public void setEntranceList (DatabaseHandler db)
	{
		entranceList = db.getAllEntrancesForStation(this);
		setHandicapAccessible();
	}

	/**
	 * Returns the unique identification number for this station
	 * 
	 * @return stationID 
	 */
	public String getStationID() 
	{
		return stationID;
	}

	/**
	 * Returns the full name of this station
	 * 
	 * @return stationName
	 */
	public String getStationName() 
	{
		return stationName;
	}

	/**
	 * Returns the name of the town/city where this station is located
	 * 
	 * @return stationCity
	 */
	public String getStationCity() 
	{
		return stationCity;
	}

	/**
	 * Returns the state (NY/NJ) this station is located
	 * 
	 * @return stationState
	 */
	public String getStationState() 
	{
		return stationState;
	}

	/**
	 * Returns the coordinates of this station as a double array
	 * in the order of latitude and longitude
	 * 
	 * @return stationLocation 
	 */
	public double[] getStationLocation() 
	{
		return new double[]{station_lat, station_lon};
	}

	/**
	 * Returns all the entrances to this station as an ArrayList
	 * <p>
	 * Should be called after all entrances are obtained from an appropriate database
	 * 
	 * @return entranceList
	 * @see #setEntranceList(DatabaseHandler)
	 */
	public ArrayList<Entrance> getEntranceList() 
	{
		return entranceList;
	}
	
	/**
	 * Returns all the entrances to this station as an ArrayList that are handicap accessible
	 * <p>
	 * Should be called after all entrances are obtained from an appropriate database
	 * 
	 * @return handicapAccessEntrances
	 * @see #setEntranceList(DatabaseHandler)
	 */
	public ArrayList<Entrance> getHandicapAccessEntrances()
	{
		return handicapAccessEntrances;
	}

	/**
	 * Creates Station object from a Parcel
	 */
	public static final Parcelable.Creator<Station> CREATOR = new Parcelable.Creator<Station>() 
	{
		/**
		 * Takes in a parcel and passes it to constructor
		 * 
		 * @see Station#Station(Parcel)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public Station createFromParcel(Parcel source) {
			return new Station(source);
		}

		/**
		 * Allows an array of Station objects to be parcelled
		 * 
		 * @see Station#Station(Parcel)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Station[] newArray(int size) {
			return new Station[size];
		}
		
	};
	
	/**
	 * Not used in Station, returns 0.
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Writes fields to a parcel in a particular order
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(stationID);
		dest.writeString(stationName);
		dest.writeString(stationCity);
		dest.writeString(stationState);
		dest.writeDouble(station_lat);
		dest.writeDouble(station_lon);
		dest.writeTypedList(entranceList);
		dest.writeTypedList(handicapAccessEntrances);
	}
	
	/**
	 * Adds handicap accessible entrances at this station to handicapAccessEntrances ArrayList. Called 
	 * after entranceList is given values to hold.
	 */
	private void setHandicapAccessible()
	{
		handicapAccessEntrances = new ArrayList<Entrance>();
		for (int i = 0; i < entranceList.size(); i++)
		{
			if (entranceList.get(i).isHandicapAccessible())
				handicapAccessEntrances.add(entranceList.get(i));
		}
	}
	
	/**
	 * For each entrance that is handicap accessible at this station, it will check if it meets the 
	 * option set by the user (escalator or elevator). 
	 * 
	 * @param access user-specified handicap access option 
	 * @return true if an entrance meets the user's handicap access requirement else false
	 */
	public boolean isHandicapAccessible(String access)
	{
		if (access == null || access.isEmpty())
			return false;
		if (handicapAccessEntrances.isEmpty() || handicapAccessEntrances == null)
			return false;
		
		for (int i = 0; i < handicapAccessEntrances.size(); i++)
		{
			if (handicapAccessEntrances.get(i).isHandicapAccessible(access))
				return true;
		}
		
		return false;
	}
}
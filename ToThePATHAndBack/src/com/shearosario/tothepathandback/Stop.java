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
public class Stop implements Parcelable
{
	/**
	 * private variables for Station class
	 */
	private String stop_id;
	private String stop_name;
	private double stop_lat;
	private double stop_lon;
	private ArrayList<Entrance> entranceList;
	private ArrayList<Entrance> handicapAccessEntrances;
	private ArrayList<String> weekdayService;
	private ArrayList<String> weekendService;
	
	/**
	 * @param stop_id
	 * @param stop_name
	 * @param stop_lat
	 * @param stop_lon
	 */
	public Stop(String stop_id, String stop_name, double stop_lat,
			double stop_lon, ArrayList<Entrance> entranceList) {
		this.stop_id = stop_id;
		this.stop_name = stop_name;
		this.stop_lat = stop_lat;
		this.stop_lon = stop_lon;
		this.entranceList = new ArrayList<Entrance>(entranceList);
		setHandicapAccessible();
	}

	/**
	 * Constructor for a parcelable Station
	 * <p>
	 * Creates a Station object in the same order as writeToParcel
	 * 
	 * @see #writeToParcel(Parcel, int)
	 * @param source Parcel argument
	 */
	public Stop(Parcel source) {
		this.stop_id = source.readString();
		this.stop_name = source.readString();
		this.stop_lat = source.readDouble();
		this.stop_lon = source.readDouble();
		
		this.entranceList = new ArrayList<Entrance>();
		source.readTypedList(entranceList, Entrance.CREATOR);
		this.handicapAccessEntrances = new ArrayList<Entrance>();
		source.readTypedList(handicapAccessEntrances, Entrance.CREATOR);
		
		this.weekdayService = new ArrayList<String>();
		source.readStringList(weekdayService);
		this.weekendService = new ArrayList<String>();
		source.readStringList(weekendService);
	}

	/**
	 * Copies station variable in parameter to new Station
	 * @param station
	 */
	public Stop(Stop station) {
		this.stop_id = station.getStopID();
		this.stop_name = station.getStopName();
		this.stop_lat = station.getStopLocation()[0];
		this.stop_lon = station.getStopLocation()[1];
		this.weekdayService = station.getWeekdayService();
		this.weekendService = station.getWeekendService();
		this.entranceList = station.getEntranceList();
		this.handicapAccessEntrances = station.getHandicapAccessEntrances();
	}

	/**
	 * 
	 */
	public void setWeekdayService(DatabaseHandler db)
	{
		
	}

	/**
	 * 
	 */
	public void setWeekendService(DatabaseHandler db)
	{
		
	}

	/**
	 * Returns the unique identification number for this station
	 * 
	 * @return stationID 
	 */
	public String getStopID() 
	{
		return stop_id;
	}

	/**
	 * Returns the full name of this station
	 * 
	 * @return stationName
	 */
	public String getStopName() 
	{
		return stop_name;
	}

	/**
	 * Returns the coordinates of this station as a double array
	 * in the order of latitude and longitude
	 * 
	 * @return stationLocation 
	 */
	public double[] getStopLocation() 
	{
		return new double[]{stop_lat, stop_lon};
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
	 * @return the weekdayService
	 */
	public ArrayList<String> getWeekdayService() {
		return weekdayService;
	}

	/**
	 * @return the weekendService
	 */
	public ArrayList<String> getWeekendService() {
		return weekendService;
	}

	/**
	 * Creates Station object from a Parcel
	 */
	public static final Parcelable.Creator<Stop> CREATOR = new Parcelable.Creator<Stop>() 
	{
		/**
		 * Takes in a parcel and passes it to constructor
		 * 
		 * @see Stop#Station(Parcel)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public Stop createFromParcel(Parcel source) {
			return new Stop(source);
		}

		/**
		 * Allows an array of Station objects to be parcelled
		 * 
		 * @see Stop#Station(Parcel)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Stop[] newArray(int size) {
			return new Stop[size];
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
		dest.writeString(stop_id);
		dest.writeString(stop_name);
		dest.writeDouble(stop_lat);
		dest.writeDouble(stop_lon);
		dest.writeTypedList(entranceList);
		dest.writeTypedList(handicapAccessEntrances);
		dest.writeStringList(weekdayService);
		dest.writeStringList(weekendService);
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
	 * For each entrance that is handicap accessible at this station, it will check if this station meets the 
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
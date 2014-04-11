package com.shearosario.tothepathandback;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Representation of an entrance to a station in the PATH system. 
 * <p>
 * Included is the station name, unique entrance ID and the ID of their associated station, 
 * any notes for the entrance, it's latitude and longitude, and whether the entrance:
 * <ul>
 * <li>Is an elevator</li>
 * <li>Is an escalator</li>
 * <li>Only accesses a NY-bound platform</li>
 * <li>Only accesses a NJ-bound platform</li>
 * <ul>
 * 
 * @author shea
 */

public class Entrance implements Parcelable {
	/**
	 * private variables for Entrance class
	 */
	private String entranceid, stationid, name, notes;
	private boolean escalator, elevator, nybound, njbound;
	private double station_lat, station_lon;
	
	/**
	 * Constructor for Entrance object 
	 *  
	 * @param eid unique entrance ID
	 * @param sid unique station ID for the station this entrance is associated with
	 * @param n station name this entrance is associated with
	 * @param nt any notes for this entrance
	 * @param es whether this entrance is an escalator
	 * @param el whether this entrance is an elevator
	 * @param ny whether this only accesses a NY-bound platform
	 * @param nj whether this only accesses a NJ-bound platform
	 * @param lat latitude of entrance
	 * @param lon longitude of entrance
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
		
		// entranceLocation = new LatLng(lat, lon);
		station_lat = lat;
		station_lon = lon;
	}

	/**
	 * Constructor for a parcelable Entrance
	 * <p>
	 * Creates a Entrance object in the same order as writeToParcel
	 * 
	 * @see #writeToParcel(Parcel, int)
	 * @param source Parcel argument
	 */
	public Entrance(Parcel source) {
		this.entranceid = source.readString();
		this.stationid = source.readString();
		this.name = source.readString();
		this.notes = source.readString();
		this.escalator = (source.readInt() == 1);
		this.elevator = (source.readInt() == 1);
		this.nybound = (source.readInt() == 1);
		this.njbound = (source.readInt() == 1);
		this.station_lat = source.readDouble();
		this.station_lon = source.readDouble();
	}

	
	/**
	 * Return the unique ID for this entrance
	 * 
	 * @return entranceid
	 */
	public String getEntranceid() {
		return entranceid;
	}

	/**
	 * Return the unique ID of the station that this entrance is associated with
	 * 
	 * @return stationid
	 */
	public String getStationid() {
		return stationid;
	}

	/**
	 * Return the name of the station that this entrance is associated with
	 * 
	 * @return name
	 */
	public String getStationName() {
		return name;
	}

	/**
	 * Return any notes for this entrance. If there are any notes, they're usually 
	 * just time restrictions on the entrance (e.g. Not opened between 1900 - 0700)
	 * 
	 * @return notes
	 */
	public String getEntranceNotes() {
		return notes;
	}

	/**
	 * If the entrance is an elevator, it will return true. Otherwise, false.
	 * 
	 * @return elevator
	 */
	public boolean isElevator() {
		return elevator;
	}

	/**
	 * If the entrance only has access to the platform where trains are bound for NY 
	 * terminals, return true. Otherwise, return false. 
	 * 
	 * @return nybound
	 */
	public boolean isNybound() {
		return nybound;
	}

	/**
	 * If the entrance only has access to the platform where trains are bound for NJ 
	 * terminals, return true. Otherwise, return false.
	 * 
	 * @return njbound
	 */
	public boolean isNjbound() {
		return njbound;
	}
	
	/**
	 * Return true if the entrance is an escalator. Otherwise, false.
	 * 
	 * @return escalator
	 */
	public boolean isEscalator() {
		return escalator;
	}

	/**
	 * The double array referring to the coordinates of the entrance in latitude,longitude order is returned.
	 * 
	 * @return entranceLocation
	 */
	public double[] getEntranceLocation() {
		return new double[]{station_lat, station_lon};
	}

	/**
	 * Creates Entrance object from a Parcel
	 */
	public static final Parcelable.Creator<Entrance> CREATOR = new Parcelable.Creator<Entrance>() 
	{
		/**
		 * Takes in a parcel and passes it to constructor
		 * 
		 * @see Entrance#Entrance(Parcel)
		 * @see android.os.Parcelable.Creator#createFromParcel(android.os.Parcel)
		 */
		@Override
		public Entrance createFromParcel(Parcel source) {
			return new Entrance(source);
		}

		/**
		 * Allows an array of Entrance objects to be parcelled
		 * 
		 * @see Station#Station(Parcel)
		 * @see android.os.Parcelable.Creator#newArray(int)
		 */
		@Override
		public Entrance[] newArray(int size) {
			return new Entrance[size];
		}
	};
	
	/**
	 * Not used in Entrance, returns 0.
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
		dest.writeString(entranceid);
		dest.writeString(stationid);
		dest.writeString(name);
		dest.writeString(notes);
		dest.writeInt(escalator ? 1 : 0);
		dest.writeInt(elevator ? 1 : 0);
		dest.writeInt(nybound ? 1 : 0);
		dest.writeInt(njbound ? 1 : 0);
		dest.writeDouble(station_lat);
		dest.writeDouble(station_lon);
	}
	
	/**
	 * Determines if the entrance is handicap accessible by checking if it has an elevator or escalator
	 * 
	 * @return if entrance is handicap accessible
	 */
	public boolean isHandicapAccessible()
	{
		if (escalator || elevator)
			return true;
		return false;
	}
	
	/**
	 * Returns whether the selected handicap access the user requires is met by this entrance. 
	 * Selected as an option in the main activity.   
	 * 
	 * @param access What specific mode of access the user needs (elevator/escalator) 
	 * @return whether the entrance meets the level of handicap access the user needs
	 */
	public boolean isHandicapAccessible(String access)
	{		
		if (isHandicapAccessible())
		{
			if (access.compareToIgnoreCase("escalator") == 0)
			{
				if (escalator)
					return true;
			}
			else if (access.compareToIgnoreCase("elevator") == 0)
			{
				if (elevator)
					return true;
			}
		}
		
		return false;
	}
}

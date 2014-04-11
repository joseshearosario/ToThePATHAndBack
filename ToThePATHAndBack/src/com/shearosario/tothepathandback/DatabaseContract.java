package com.shearosario.tothepathandback;

import android.provider.BaseColumns;

/**
 * @author shea
 * 
 * Contract class to contain the database columns that are imported in DatabaseHandler.java 
 * that hold the stations and entrances. The constants are divided into two classes, one for the station and 
 * one for the entrances. Each constant represents a column in their respective database.
 * 
 */
public class DatabaseContract implements BaseColumns
{
	/**
	 * An empty constructor, in order to avoid any accidents that could arise by instantiating 
	 * this class.
	 */
	public DatabaseContract()
	{
		
	}
	
    /**
     * Abstract class that will correspond to the columns in the station database
     */
    public static abstract class StationEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "stations";
		public static final String COLUMN_STATIONID = "stationid";
		public static final String COLUMN_STATIONNAME = "name";
		public static final String COLUMN_LATITUDE = "latitude";
		public static final String COLUMN_LONGITUDE = "longitude";
		public static final String COLUMN_CITY = "city";
		public static final String COLUMN_STATE = "state";
	}
    
    /**
     * Abstract class that will correspond to the columns in the entrance database
     */
    public static abstract class EntranceEntry implements BaseColumns
    {
    	public static final String TABLE_NAME = "station_entrances";
    	public static final String COLUMN_STATIONID = "stationid";
    	public static final String COLUMN_STATIONNAME = "name";
		public static final String COLUMN_LATITUDE = "latitude";
		public static final String COLUMN_LONGITUDE = "longitude";
		public static final String COLUMN_NOTES = "notes";
		public static final String COLUMN_ESCALATOR = "escalator";
		public static final String COLUMN_ELEVATOR = "elevator";
		public static final String COLUMN_NYBOUND = "nybound";
		public static final String COLUMN_NJBOUND = "njbound";
    }
}

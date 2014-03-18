package com.shea.tothepathandback;

import android.provider.BaseColumns;

/**
 * @author shea
 * 
 * This contract class is a container for the database columns that are imported in DatabaseHandler.java 
 * that hold the stations and entrances. The constants are divided into two classes, one for the station and 
 * one for the entrances. Each constant represents a column in their respective database.
 * 
 * This style of class is beneficial for us because it allows you to change the column names in the databases. 
 * If you find it necessary to change the name of one column, all you'll have to do is change that constant 
 * here and it will propagate throughout the project. 
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
     * Abstract class in DatabaseContract that will correspond to the columns in 
     * the station database
     *
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
     * Abstract class in DatabaseContract that will correspond to the columns in 
     * the entrance database
     *
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

package com.shea.tothepathandback;

import android.provider.BaseColumns;

public class DatabaseContract implements BaseColumns {
	public DatabaseContract()
	{
		
	}
	
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

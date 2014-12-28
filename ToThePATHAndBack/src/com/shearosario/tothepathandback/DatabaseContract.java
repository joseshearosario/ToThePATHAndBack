package com.shearosario.tothepathandback;

import android.provider.BaseColumns;

/**
 * Contract class to contain the database columns that are imported in
 * DatabaseHandler.java that hold the stations and entrances. The constants are
 * divided into the tables of the database. Each constant represents a column in
 * their respective database.
 * 
 * @author shea
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
     * Abstract class that will correspond to the columns in the stop database
     */
    public static abstract class StopEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "stops";
		public static final String COLUMN_STOPID = "stop_id";
		public static final String COLUMN_STOPCODE = "stop_code";
		public static final String COLUMN_STOPNAME = "stop_name";
		public static final String COLUMN_STOPDESC = "stop_desc";
		public static final String COLUMN_STOPLAT = "stop_lat";
		public static final String COLUMN_STOPLON = "stop_lon";
		public static final String COLUMN_ZONEID = "zone_id";
		public static final String COLUMN_STOPURL = "stop_url";
		public static final String COLUMN_LOCATIONTYPE = "location_type";
		public static final String COLUMN_PARENTSTATION = "parent_station";
		public static final String COLUMN_STOPTIMEZONE = "stop_timezone";
	}
    
    /**
     * Abstract class that will correspond to the columns in the route database
     */
    public static abstract class RouteEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "routes";
		public static final String COLUMN_ROUTEID = "route_id";
		public static final String COLUMN_ROUTELONGNAME = "route_long_name";
		public static final String COLUMN_ROUTEDESC = "route_desc";
		public static final String COLUMN_ROUTECOLOR = "route_color";
	}
    
    
    /**
     * Abstract class that will correspond to the columns in the entrance database
     */
    public static abstract class EntranceEntry implements BaseColumns
    {
    	public static final String TABLE_NAME = "entrances";
    	public static final String COLUMN_ENTRANCEID = "entrance_id";
    	public static final String COLUMN_STOPID = "stop_id";
		public static final String COLUMN_LATITUDE = "latitude";
		public static final String COLUMN_LONGITUDE = "longitude";
		public static final String COLUMN_NOTES = "notes";
		public static final String COLUMN_ESCALATOR = "escalator";
		public static final String COLUMN_ELEVATOR = "elevator";
		public static final String COLUMN_NYBOUND = "nybound";
		public static final String COLUMN_NJBOUND = "njbound";
    }
    
    /**
     * Abstract class that will correspond to the columns in the calendar database
     */
    public static abstract class CalendarEntry implements BaseColumns
    {
    	public static final String TABLE_NAME = "calendar";
    	public static final String COLUMN_SERVICEID = "service_id";
    	public static final String COLUMN_SERVICENAME = "service_name";
    	public static final String COLUMN_MONDAY = "monday";
    	public static final String COLUMN_TUESDAY = "tuesday";
    	public static final String COLUMN_WEDNESDAY = "wednesday";
    	public static final String COLUMN_THURSDAY = "thursday";
    	public static final String COLUMN_FRIDAY = "friday";
    	public static final String COLUMN_SATURDAY = "saturday";
    	public static final String COLUMN_SUNDAY = "sunday";
    	public static final String COLUMN_STARTDATE = "start_date";
    	public static final String COLUMN_ENDDATE = "end_date";
    }
    
    /**
     * Abstract class that will correspond to the columns in the calendar_date database
     */
    public static abstract class CalendarDateEntry implements BaseColumns
    {
    	public static final String TABLE_NAME = "calendar_dates";
    	public static final String COLUMN_DATE = "date";
    	public static final String COLUMN_EXCEPTIONTYPE = "exception_type";
    	public static final String COLUMN_SERVICEID = "service_id";
    }
    
    /**
     * Abstract class that will correspond to the columns in the trip database
     */
    public static abstract class TripEntry implements BaseColumns
    {
    	public static final String TABLE_NAME = "trips";
    	public static final String COLUMN_ROUTEID = "route_id";
    	public static final String COLUMN_SERVICEID = "service_id";
    	public static final String COLUMN_TRIPID = "trip_id";
    	public static final String COLUMN_TRIPHEADSIGN = "trip_headsign";
    	public static final String COLUMN_SHAPEID = "shape_id";
		public static final String COLUMN_DIRECTIONID = "direction_id";
    }
    
    /**
     * Abstract class that will correspond to the columns in the stop_time database
     */
    public static abstract class StopTimeEntry implements BaseColumns
    {
    	public static final String TABLE_NAME = "stop_times";
    	public static final String COLUMN_TRIPID = "trip_id";
    	public static final String COLUMN_ARRIVALTIME = "arrival_time";
    	public static final String COLUMN_DEPARTURETIME = "departure_time";
    	public static final String COLUMN_STOPID = "stop_id";
    	public static final String COLUMN_STOPSEQUENCE = "stop_sequence";
    }
}

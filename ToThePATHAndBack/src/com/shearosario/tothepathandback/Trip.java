package com.shearosario.tothepathandback;

import java.util.ArrayList;

/**
 * A trip is a sequence of two or more stops that occurs at specific time. It is
 * associated with a route and service.
 * 
 * @author Jose Andres Rosario
 */
public class Trip 
{
	/**
	 * The route_id field contains an ID that uniquely identifies a route.
	 */
	private String route_id;
	/**
	 * The service_id contains an ID that uniquely identifies a set of dates
	 * when service is available for the route(s).
	 */
	private String service_id;
	/**
	 * The trip_id field contains an ID that identifies a trip. The trip_id is
	 * dataset unique.
	 */
	private String trip_id;
	/**
	 * The trip_headsign field contains the text that appears on a sign that
	 * identifies the trip's destination to passengers. Use this field to
	 * distinguish between different patterns of service in the same route.
	 */
	private String trip_headsign;
	/**
	 * The shape_id field contains an ID that defines a shape for the trip.
	 */
	private String shape_id;
	/**
	 * The stop times associated with this trip
	 */
	private ArrayList<StopTime> stopTimes;
	
	/**
	 * Creates a new Trip object 
	 * 
	 * @param route_id uniquely identifies a route
	 * @param service_id uniquely identifies a set of dates when service is available for the route
	 * @param trip_id identifies this trip
	 * @param trip_headsign identifies the trip's destination
	 * @param shape_id defines a shape for the trip
	 */
	public Trip(String route_id, String service_id, String trip_id,
			String trip_headsign, String shape_id, ArrayList<StopTime> stopTimes)
	{
		this.route_id = route_id;
		this.service_id = service_id;
		this.trip_id = trip_id;
		this.trip_headsign = trip_headsign;
		this.shape_id = shape_id;
		this.stopTimes = stopTimes;
	}
	
	/**
	 * Creates a new Trip object that is a copy of an already existing Trip object 
	 * @param t a Trip object
	 */
	public Trip (Trip t)
	{
		this.route_id = t.getRoute_id();
		this.service_id = t.getService_id();
		this.trip_id = t.getTrip_id();
		this.trip_headsign = t.getTrip_headsign();
		this.shape_id = t.getShape_id();
		this.stopTimes = t.getStopTimes();
	}

	/**
	 * @return the route_id
	 */
	public String getRoute_id()
	{
		return route_id;
	}

	/**
	 * @return the service_id
	 */
	public String getService_id()
	{
		return service_id;
	}

	/**
	 * @return the trip_id
	 */
	public String getTrip_id()
	{
		return trip_id;
	}

	/**
	 * @return the trip_headsign
	 */
	public String getTrip_headsign() 
	{
		return trip_headsign;
	}

	/**
	 * @return the shape_id
	 */
	public String getShape_id()
	{
		return shape_id;
	}

	/**
	 * @return the stopTimes
	 */
	public ArrayList<StopTime> getStopTimes() {
		return stopTimes;
	}
}

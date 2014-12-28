package com.shearosario.tothepathandback;

/**
 * A route is a group of trips that are displayed to riders as a single service.
 * 
 * @author Jose Andres Rosario
 */
public class Route
{
	/**
	 * The route_id field contains an ID that uniquely identifies a route. The
	 * route_id is dataset unique.
	 */
	private int route_id;
	/**
	 * The route_long_name contains the full name of a route.
	 */
	private String route_long_name;
	/**
	 * The route_desc field contains a description of a route.
	 */
	private String route_desc;
	/**
	 * The route_color field defines a color that corresponds to a route. The
	 * color must be provided as a six-character hexadecimal number, for
	 * example, 00FFFF. If no color is specified, the default route color is
	 * white (FFFFFF).
	 */
	private String route_color;
	
	/**
	 * Creates a new Route object
	 * 
	 * @param route_id uniquely identifies this route
	 * @param route_long_name full name of this route
	 * @param route_desc describes this route
	 * @param route_color defines the color that corresponds to this route
	 */
	public Route(int route_id, String route_long_name, String route_desc, String route_color)
	{
		this.route_id = route_id;
		this.route_long_name = route_long_name;
		this.route_desc = route_desc;
		this.route_color = route_color;
	}
	
	/**
	 * Creates a new Route object that is a copy of an already existing Route object 
	 * @param r a Route object
	 */
	public Route (Route r)
	{
		this.route_id = r.getRoute_id();
		this.route_long_name = r.getRoute_long_name();
		this.route_desc = r.getRoute_desc();
		this.route_color = r.getRoute_color();
	}

	/**
	 * @return the route_id
	 */
	public int getRoute_id() {
		return route_id;
	}

	/**
	 * @return the route_long_name
	 */
	public String getRoute_long_name() {
		return route_long_name;
	}

	/**
	 * @return the route_desc
	 */
	public String getRoute_desc() {
		return route_desc;
	}

	/**
	 * @return the route_color
	 */
	public String getRoute_color() {
		return route_color;
	}
}

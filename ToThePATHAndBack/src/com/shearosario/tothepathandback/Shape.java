package com.shearosario.tothepathandback;

/**
 * Rules for drawing lines on a map to represent a transit organization's
 * routes.
 * 
 * @author Jose Andres Rosario
 */
public class Shape 
{
	/**
	 * The shape_id field contains an ID that uniquely identifies a shape.
	 */
	private String shape_id;
	/**
	 * The shape_pt_lat field associates a shape point's latitude with a shape
	 * ID. The field value must be a valid WGS 84 latitude.
	 */
	private double shape_pt_lat;
	/**
	 * The shape_pt_lon field associates a shape point's longitude with a shape
	 * ID. The field value must be a valid WGS 84 longitude value from -180 to
	 * 180
	 */
	private double shape_pt_lon;
	/**
	 * The shape_pt_sequence field associates the latitude and longitude of a
	 * shape point with its sequence order along the shape. The values for
	 * shape_pt_sequence must be non-negative integers, and they must increase
	 * along the trip.
	 */
	private int shape_pt_sequence;
	
	/**
	 * Creates a new Shape object 
	 * 
	 * @param shape_id uniquely identifies this shape
	 * @param shape_pt_lat a shape point's latitude
	 * @param shape_pt_lon a shape point's longitude
	 * @param shape_pt_sequence its sequence order along the shape
	 */
	public Shape(String shape_id, double shape_pt_lat, double shape_pt_lon, int shape_pt_sequence) 
	{
		this.shape_id = shape_id;
		this.shape_pt_lat = shape_pt_lat;
		this.shape_pt_lon = shape_pt_lon;
		this.shape_pt_sequence = shape_pt_sequence;
	}
	
	/**
	 * Creates a new Shape object that is a copy of an already existing Shape object
	 * 
	 *  @param s a Shape object
	 */
	public Shape (Shape s)
	{
		this.shape_id = s.getShape_id();
		this.shape_pt_lat = s.getShape_pt_lat();
		this.shape_pt_lon = s.getShape_pt_lon();
		this.shape_pt_sequence = s.getShape_pt_sequence();
	}

	/**
	 * @return the shape_id
	 */
	public String getShape_id()
	{
		return shape_id;
	}

	/**
	 * @return the shape_pt_lat
	 */
	public double getShape_pt_lat()
	{
		return shape_pt_lat;
	}

	/**
	 * @return the shape_pt_lon
	 */
	public double getShape_pt_lon()
	{
		return shape_pt_lon;
	}

	/**
	 * @return the shape_pt_sequence
	 */
	public int getShape_pt_sequence() 
	{
		return shape_pt_sequence;
	}
}

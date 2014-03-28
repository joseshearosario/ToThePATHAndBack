/**
 * 
 */
package com.shearosario.tothepathandback;

/**
 * An object that each hold a station and the distance or duration from the current 
 * location of the user. The purpose for this class is to associate the station with one 
 * of those metrics in order to sort through all the stations and to find which is shorter or 
 * takes less time to arrive at. Only has get methods, and overridden sort method for two 
 * of these objects.  
 * 
 * @author shea
 */
public class StationSort implements Comparable<StationSort>
{
	public Station station;
	public double sortMeasure;
	
	/**
	 * Constructor for StationSortObject
	 * 
	 * @param s - the station in question 
	 * @param d - the distance or duration from the current location to the station
	 */
	public StationSort (Station s, double d)
	{
		station = s;
		sortMeasure = d;
	}

	/**
	 * Whether it is the duration or distance, it will return the integer value of it
	 * 
	 * @return the sortMeasure
	 */
	public double getSortMeasure() {
		return sortMeasure;
	}
	
	public String getSortMeasureString()
	{
		return Double.toString(sortMeasure);
	}

	/**
	 * @return the station
	 */
	public Station getStation() {
		return station;
	}

	/**
	 * Compares the sort value of this and the passed StationSortObject by returning 
	 * the difference between the two. This will sort in ascending order.
	 * 
	 * @return 
	 */
	@Override
	public int compareTo(StationSort arg0) {
		double compareMeasure = ((StationSort) arg0).getSortMeasure();
		return Double.compare(this.sortMeasure, compareMeasure);
	}
}

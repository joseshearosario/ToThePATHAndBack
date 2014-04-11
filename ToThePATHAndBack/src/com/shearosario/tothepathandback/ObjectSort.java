package com.shearosario.tothepathandback;


/**
 * 
 * Used to sort an object based on their distance or time from a location.
 * <p>
 * Because we need to sort entrances and stations, this class is generic. Only
 * has get methods, and overridden sort method for two of these objects.
 * 
 * @author shea
 */
public class ObjectSort<T> implements Comparable<ObjectSort<T>>
{
	public T object;
	public double sortMeasure;
	
	/**
	 * Constructor for ObjectSort
	 * 
	 * @param o object
	 * @param d distance or duration from the current location to the object
	 */
	public ObjectSort (T o, double d)
	{
		object = o;
		sortMeasure = d;
	}

	/**
	 * Whether it is the duration or distance, it will return the double value of it.
	 * 
	 * @return distance or duration from object to location
	 */
	public double getSortMeasure() {
		return sortMeasure;
	}

	/**
	 * The object can be a Station or Entrance. This will retrieve it.
	 * 
	 * @return Station or Entrance object
	 */
	public T getObject() {
		return object;
	}

	/**
	 * Compares the sort value of this and the passed ObjectSort by returning 
	 * the difference between the two. This will sort in ascending order.
	 * 
	 * @return 
	 */
	@Override
	public int compareTo(ObjectSort<T> another) {
		double compareMeasure = another.getSortMeasure();
		return Double.compare(this.sortMeasure, compareMeasure);		
	}

	/**
	 * @param sortMeasure the sortMeasure to set
	 */
	public void setSortMeasure(double sortMeasure) {
		this.sortMeasure = sortMeasure;
	}
}

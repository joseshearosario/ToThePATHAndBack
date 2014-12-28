/**
 * 
 */
package com.shearosario.tothepathandback;

/**
 * @author Jose Andres Rosario
 *
 */
public class TimeStampsAndRoutes {
	private String[] allRoutesWithDirection;
	private StopTime[] allTimeStamps;
	
	/**
	 * @param allRoutesWithDirection
	 * @param allTimeStamps
	 */
	public TimeStampsAndRoutes(String[] allRoutesWithDirection,
			StopTime[] allTimeStamps) {
		this.allRoutesWithDirection = allRoutesWithDirection;
		this.allTimeStamps = allTimeStamps;
	}
	
	/**
	 * @return the allRoutesWithDirection
	 */
	public String[] getAllRoutesWithDirection() {
		return allRoutesWithDirection;
	}
	/**
	 * @return the allTimeStamps
	 */
	public StopTime[] getAllTimeStamps() {
		return allTimeStamps;
	}
}

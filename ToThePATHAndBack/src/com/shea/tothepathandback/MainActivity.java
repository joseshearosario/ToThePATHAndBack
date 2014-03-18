package com.shea.tothepathandback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.LatLng;
import com.shea.tothepathandback.Directions.DownloadAllData;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity{
	// The main database, where all information will initially be held
	public DatabaseHandler pathStations;
	// Static LatLng variable updated periodically in the userLocation class
	public static LatLng currentLocation = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get current location
		new userLocation(this);
		
		// Create database
		pathStations = new DatabaseHandler(this);
		try {
			pathStations.createDatabase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("Database", "Database was not created/opened");
		} 
		
		// Get all stations from database
		Station[] allStations = pathStations.getAllStations();
		
		// Get all entrances from each station
		for (int i = 0; i < allStations.length; i++)
			allStations[i].setEntranceList(pathStations);
		
		// create all the URLs
		String [] allJSONURLs = Directions.getAllURLS(currentLocation, allStations);
		for (int i = 0; i < allJSONURLs.length; i++)
			Log.d("allJSONURLs", allJSONURLs[i]);
		
		// Get direction data from current location (null) to each station from URLs
		DownloadAllData downloadAllData = new DownloadAllData();
		downloadAllData.execute(allJSONURLs);
		String[] allJSONdata = null;
		try {
			allJSONdata = downloadAllData.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Obtain each of the station's distance and duration from the user
		int[] allJSONdistances = null;
		int[] allJSONdurations = null;
		try {
			allJSONdistances = DirectionsJSONParser.getDistance(allJSONdata);
			allJSONdurations = DirectionsJSONParser.getDuration(allJSONdata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// sort the stations utilizing the StationSortObject class, where it will hold
		// the station and the distance/duration. Done in this matter because I have not found a 
		// cleaner way to sort the distance/duration while maintaining a connection to the station 
		// they're associated with.
		List<StationSortObject> allStationsSortDistance = new ArrayList<StationSortObject>();
		List<StationSortObject> allStationsSortDuration = new ArrayList<StationSortObject>();
		for (int i = 0; i < allStations.length; i++)
		{
			allStationsSortDistance.add(new StationSortObject(allStations[i], allJSONdistances[i]));
			allStationsSortDuration.add(new StationSortObject(allStations[i], allJSONdurations[i]));
		}
		Collections.sort(allStationsSortDistance);
		Collections.sort(allStationsSortDuration);
		
		// get the three shortest/quickest stations
		for (int i = 3; i < allStationsSortDistance.size() && i < allStationsSortDuration.size();)
		{
			allStationsSortDistance.remove(i);
			allStationsSortDuration.remove(i);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * <p>An object that each hold a station and the distance or duration from the current 
	 * location of the user. The purpose for this class is to associate the station with one 
	 * of those metrics in order to sort through all the stations and to find which is shorter or 
	 * takes less time to arrive at. Only has get methods, and overridden sort method for two 
	 * of these objects.  
	 * 
	 */
	public class StationSortObject implements Comparable<StationSortObject>
	{
		public Station station = null;
		public int sortMeasure;
		
		/**
		 * Contructor for StationSortObject
		 * 
		 * @param s - the station in question 
		 * @param d - the distance or duration from the current location to the station
		 */
		public StationSortObject (Station s, int d)
		{
			station = s;
			sortMeasure = d;
		}

		/**
		 * Whether it is the duration or distance, it will return the integer value of it
		 * 
		 * @return the sortMeasure
		 */
		public int getSortMeasure() {
			return sortMeasure;
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
		public int compareTo(StationSortObject arg0) {
			int compareMeasure = ((StationSortObject) arg0).getSortMeasure();
			return this.sortMeasure - compareMeasure;
		}
	}
}

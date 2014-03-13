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
	public DatabaseHandler pathStations;
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

		// sort the stations
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
	
	public class StationSortObject implements Comparable<StationSortObject>
	{
		public Station station = null;
		public int sortMeasure;
		
		public StationSortObject (Station s, int d)
		{
			station = s;
			sortMeasure = d;
		}

		/**
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

		@Override
		public int compareTo(StationSortObject arg0) {
			int compareMeasure = ((StationSortObject) arg0).getSortMeasure();
			return this.sortMeasure - compareMeasure;
		}
	}
}

package com.shea.tothepathandback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	DatabaseHandler pathStations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		{
			allStations[i].setEntranceList(pathStations);
			Station s = allStations[i];
			String log = "ID:"+s.getStationID()+", Name:" + s.getStationName()+", City:" + s.getStationCity()+
				", State: " + s.getStationState() + " " + s.getLatitude() + "," + s.getLongitude();
			Log.d("Station", log);
			
			Entrance[] allEntrances = s.getEntranceList();
			for (int j = 0; j < allEntrances.length; j++)
			{
				Entrance e = allEntrances[j];
				String log1 = "ID:"+e.getEntranceid()+", StationID:"+e.getStationid()+", Station:"+e.getStationName()+
						", Notes:"+e.getEntranceNotes()+", "+e.getLatitude()+","+e.getLongitude()+
						", Elevator:"+e.isElevator()+ ", Escalator:"+e.isElevator()+", NYBOUND:"+e.isNybound()+", NJBOUND:"+e.isNjbound();
				Log.d("Entrance",log1);
			}
		}
		
		// Get current location of the user
		
		
		// Get direction data from current location (null) to each station
		String[] allJSONdata = Directions.getAllJSONdata(null, allStations);
		
		// get the distance and duration from current location to each station using the downloaded JSON data
		DirectionsJSONParser dp = new DirectionsJSONParser();
		int[] allJSONdistances = dp.getDistance(allJSONdata);
		int[] allJSONdurations = dp.getDuration(allJSONdata);
		
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

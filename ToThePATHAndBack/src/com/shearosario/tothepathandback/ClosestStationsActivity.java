package com.shearosario.tothepathandback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AliasActivity;
import android.app.Fragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.os.Build;

public class ClosestStationsActivity extends Activity
{
	public LatLng origin;
	public String[] allJSONURLs;
	public String[] allJSONdata;
	
	private void createURLs ()
	{
		allJSONURLs = Directions.getAllURLS(origin, MainActivity.allStations);
		/*for (int i = 0; i < allJSONURLs.length; i++)
			Log.d("allJSONURLs", allJSONURLs[i]);*/
	}
	
	private void downloadJSON ()
	{
		// Get direction data from origin to each station from URLs
		Directions.DownloadAllData downloadAllData = new Directions.DownloadAllData();
		downloadAllData.execute(allJSONURLs);
		try {
			allJSONdata = downloadAllData.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double[] getAllSortMeasures()
	{
		double[] allJSONdistances = null;
		try {
			allJSONdistances = DirectionsJSONParser.getDistanceOrDuration(allJSONdata);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allJSONdistances;
	}
	
	private void confirmOptions()
	{
		TextView confirmTransport = (TextView) findViewById(R.id.text_confirmTransport);
		TextView confirmSort = (TextView) findViewById(R.id.text_confirmSort);
		confirmTransport.setText("Transport Mode: "+MainActivity.transportMode.toUpperCase());
		confirmSort.setText("Sort By: "+MainActivity.distance_duration.toUpperCase());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_closest_stations);

		/*if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		
		Intent intent = getIntent();

		if(intent.hasExtra("Manual"))
		{
			double[] manual = intent.getDoubleArrayExtra("Manual");
			origin = new LatLng (manual[0], manual[1]);
		}
		else if (intent.hasExtra("Current"))
		{
			double[] current = intent.getDoubleArrayExtra("Current");
			origin = new LatLng (current[0], current[1]);
		}
						
		createURLs();
		downloadJSON();
		double[] allSortMeasures = getAllSortMeasures();
		confirmOptions();
		
		
		// sort the stations utilizing the StationSortObject class, where it will hold
		// the station and the distance/duration. Done in this matter because I have not found a 
		// cleaner way to sort the distance/duration while maintaining a connection to the station 
		// they're associated with.
		List<StationSort> allStationsSort = new ArrayList<StationSort>();
		for (int i = 0; i < MainActivity.allStations.length; i++)
		{
			allStationsSort.add(new StationSort(MainActivity.allStations[i], allSortMeasures[i]));
		}
		Collections.sort(allStationsSort);
		
		// get the three shortest/quickest stations
		for (int i = 3; i < allStationsSort.size();)
		{
			allStationsSort.remove(i);
		}
		
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, R.layout.listitem, allStationsSort);
		/*ListAdapter adapter = createListAdapter(allStationsSortDistance);*/
		ListView listview = (ListView) findViewById(R.id.ClosestStationsList);
		listview.setAdapter(adapter);
		/*setListAdapter(adapter);*/
	}
	
/*	private List<Map<String, String>> convertToListItems (List<StationSort> sS)
	{
		final List<Map<String, String>> listItem = new ArrayList<Map<String, String>>(sS.size());

		for (final StationSort tempSS: sS) 
		{
			HashMap<String, String> listItemMap = new HashMap<String, String>();
			listItemMap.put("stationName", tempSS.getStation().getStationName());
			listItemMap.put("sortMeasure", tempSS.getSortMeasureString());
			listItem.add(listItemMap);
		}

		return listItem;
	}
	
	private ListAdapter createListAdapter (List<StationSort> sS)
	{
		String[] from = new String[] {"stationName", "sortMeasure"};
	    int[] to = new int[] {android.R.id.text1, android.R.id.text2};
	    List<Map<String, String>> list = convertToListItems(sS);

	    return new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
	}*/
	
	private class MySimpleArrayAdapter extends ArrayAdapter<StationSort>
	{
		private final Context context;
		private final List<StationSort> stationSortList;
		
		public MySimpleArrayAdapter(Context context, int resource, List<StationSort> objects) 
		{
			super(context, resource, objects);
			this.context = context;
			stationSortList = objects;
		}
		
		@Override
		public View getView (int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.listitem, parent, false);
			}
			
			TextView stationName = (TextView) convertView.findViewById(R.id.stationName);
			TextView sortMeasure = (TextView) convertView.findViewById(R.id.sortMeasure); 
			
			StationSort sS = stationSortList.get(position);
			
			stationName.setText(sS.getStation().getStationName());
			sortMeasure.setText(sS.getSortMeasureString());
			
			return convertView;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.closest_stations, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	/*
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_closest_stations, container, false);
			return rootView;
		}
	}
	*/
	
	@Override
	public void onBackPressed() {
		// have to make sure not to create an infinite loop
	    // startActivity(new Intent(this, MainActivity.class));
	}
}

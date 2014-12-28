package com.shearosario.tothepathandback;

import android.support.v4.app.NavUtils;

import java.util.ArrayList;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ClosestStationsActivity extends Activity
{
	private static LatLng origin;		
	private static Context context;
	private static Activity activity;
	private AdView adView;
	
	@Override
	protected void onResume()
	{
		if (adView != null) 
			adView.resume();
		super.onResume();
	}
	
	@Override
	public void onPause() 
	{
	    if (adView != null)
	      adView.pause();
	    super.onPause();
	}
	
	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() 
	{
		// Destroy the AdView.
	    if (adView != null)
	      adView.destroy();
	    super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_closest_stations);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		context = this;
		activity = this;
		
		adView = (AdView) this.findViewById(R.id.adViewClosest);
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("949F5429A9EC251C1DD4395558D33531").build();
		// AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
		
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
		
		double[] entranceMeasures = intent.getDoubleArrayExtra("closestSortMeasures");
		ArrayList<Entrance> closestEntrances = intent.getParcelableArrayListExtra("closestEntrances");
		
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, R.layout.listitem, closestEntrances, entranceMeasures);
		//ListAdapter adapter = createListAdapter(allStationsSortDistance);
		ListView listview = (ListView) findViewById(R.id.ClosestStationsList);
		listview.setAdapter(adapter);
		//setListAdapter(adapter);
		
		final GoogleMap gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapview)).getMap();
		gMap.setMyLocationEnabled(false);			
		gMap.addMarker(new MarkerOptions()
			.title("Origin")
			.position(origin));
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 13));
		gMap.setBuildingsEnabled(false);
		gMap.getUiSettings().setZoomControlsEnabled(false);
		
		TextView textView = (TextView) findViewById(R.id.osm_directions);
		textView.setText(Html.fromHtml(
	            "Data provided by © OpenStreetMap contributors " +
	            "<a href=\"http://www.openstreetmap.org/copyright\">License</a>"));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		textView = (TextView) findViewById(R.id.directions_text);
		textView.setText(Html.fromHtml(
	            "Directions, Nominatim Search Courtesy of " +
	            "<a href=\"http://www.mapquest.com\">MapQuest</a>"));
		textView.setMovementMethod(LinkMovementMethod.getInstance());

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
		      @Override
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		      {		    	    
		    	  final Entrance item = (Entrance) parent.getItemAtPosition(position);
		    	  
		    	  gMap.clear();
		    	  
		    	  LatLngBounds bounds = new LatLngBounds.Builder()
		    	  			.include(origin)
		    	  			.include(new LatLng (item.getEntranceLocation()[0], item.getEntranceLocation()[1]))
		    	  			.build();
		    	  
		    	  gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
		    	  
		    	  gMap.addMarker(new MarkerOptions()
					.title("Origin")
					.position(origin));
		    	  
		    	  String stationName = null;
		    	  for (int i = 0; i < MainActivity.getAllStations().size(); i++)
		    	  {
		    		  if (item.getStopid().equalsIgnoreCase(MainActivity.getAllStations().get(i).getStopID()))
		    		  {
		    			  stationName = MainActivity.getAllStations().get(i).getStopName();
		    			  break;
		    		  }
		    	  }
		    	  
		    	  gMap.addMarker(new MarkerOptions()
					// .title(item.getStationName())
					.title(stationName)
					.position(new LatLng(item.getEntranceLocation()[0], item.getEntranceLocation()[1])));
		    	  
		    	  Button button = (Button) findViewById(R.id.button_destination);
		    	  button.setEnabled(true);
		    	  // button.setText("Select " + item.getStationName());
		    	  button.setText("Select " + stationName);
		    	
		    	  button.setOnClickListener(new View.OnClickListener() 
		    	  {
		              public void onClick(View v) 
		              {
		            	  /*
		            	   * To check if the phone is currently using a network connection. 
		            	   * Listens to broadcasts when the the device is or is not connected to 
		            	   * a network
		            	   */
		          			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		          			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		          			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
		          			if (!isConnected)
		          			{
		          				Toast.makeText(context, "No network connection", Toast.LENGTH_SHORT).show();
		          				return;
		          			}
		          			
		          			new DisplayDirectionsIntent(context, activity, origin, item);
		              }
		          });
		      }
		 });
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
	
	private class MySimpleArrayAdapter extends ArrayAdapter<Entrance>
	{
		private final Context context;
		private final ArrayList<Entrance> entranceSortList;
		private final double[] measures;
		
		public MySimpleArrayAdapter(Context context, int resource, ArrayList<Entrance> closestEntrances, double[] entranceMeasures) 
		{
			super(context, resource, closestEntrances);
			this.context = context;
			entranceSortList = closestEntrances;
			measures = entranceMeasures;
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
			
			Entrance e = entranceSortList.get(position);
			double m = measures[position];
			// stationName.setText(e.getStationName());
			
			for (int i = 0; i < MainActivity.getAllStations().size(); i++)
			{
				if (e.getStopid().equalsIgnoreCase(MainActivity.getAllStations().get(i).getStopID()))
				{
					stationName.setText(MainActivity.getAllStations().get(i).getStopName());
					break;
				}
			}
						
			if (ClosestStationFragment.getDistance_duration().compareToIgnoreCase("distance") == 0)
			{
				if (MainActivity.getUnitMeasurement().equalsIgnoreCase("m"))
					sortMeasure.setText("About " + Double.toString(m) + " miles");
				else if (MainActivity.getUnitMeasurement().equalsIgnoreCase("k"))
					sortMeasure.setText("About " + Double.toString(m) + " kilometers");
			}
			else
			{
				if ((int) m == 60)
					sortMeasure.setText("About a minute");
				else if (m > 60)
					sortMeasure.setText("About " + Integer.toString(((int) m)/60) + " minutes");
				else
					sortMeasure.setText("About " + Integer.toString((int) m) + " seconds");
			}
			
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
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    // navigate up to parent activity (MainActivity)
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	// navigate back to MainActivity
	@Override
	public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
	}
}
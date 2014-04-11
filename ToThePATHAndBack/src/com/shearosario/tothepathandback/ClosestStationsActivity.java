package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
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

public class ClosestStationsActivity extends Activity
{
	public static LatLng origin;
	public static List<ObjectSort<Station>> allStationsSort;
		
	// Minimum & maximum latitude so we can span it
    // The latitude is clamped between -90 degrees and +90 degrees inclusive
    // thus we ensure that we go beyond that number
    private int minLatitude;
    private int maxLatitude;
   
    // Minimum & maximum longitude so we can span it
    // The longitude is clamped between -180 degrees and +180 degrees inclusive
    // thus we ensure that we go beyond that number
    private int minLongitude;
    private int maxLongitude;
	private Context context;
	private Activity activity;
	
	private void setupMapSpan(GeoPoint one, GeoPoint two)
	{
		if (one != null)
		{
			// Sets the minimum and maximum latitude so we can span and zoom
            minLatitude = (minLatitude > one.getLatitudeE6()) ? one.getLatitudeE6() : minLatitude;
            maxLatitude = (maxLatitude < one.getLatitudeE6()) ? one.getLatitudeE6() : maxLatitude;               
           
            // Sets the minimum and maximum latitude so we can span and zoom
            minLongitude = (minLongitude > one.getLongitudeE6()) ? one.getLongitudeE6() : minLongitude;
            maxLongitude = (maxLongitude < one.getLongitudeE6()) ? one.getLongitudeE6() : maxLongitude;
		}
		
		if (two != null)
		{
			// Sets the minimum and maximum latitude so we can span and zoom
            minLatitude = (minLatitude > two.getLatitudeE6()) ? two.getLatitudeE6() : minLatitude;
            maxLatitude = (maxLatitude < two.getLatitudeE6()) ? two.getLatitudeE6() : maxLatitude;               
           
            // Sets the minimum and maximum latitude so we can span and zoom
            minLongitude = (minLongitude > two.getLongitudeE6()) ? two.getLongitudeE6() : minLongitude;
            maxLongitude = (maxLongitude < two.getLongitudeE6()) ? two.getLongitudeE6() : maxLongitude;
		}
	}
	
	private GeoPoint createGeoPoint (ObjectSort<Station> station)
	{
		if (station == null)
			return new GeoPoint (origin.latitude, origin.longitude);
		else
			return new GeoPoint (station.getObject().getStationLocation()[0], station.getObject().getStationLocation()[1]);
	}
	
	private void addMarker(MapView map, ObjectSort<Station> station)
	{
		Marker m = new Marker(map);
		m.setPosition(createGeoPoint(station));
		m.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
		if (station == null)
			m.setTitle("Origin");
		else
			m.setTitle(station.getObject().getStationName());
		map.getOverlays().add(m);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_closest_stations);
		
		allStationsSort = new ArrayList<ObjectSort<Station>>();
		Intent intent = getIntent();
		context = this;
		activity = this;
		
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
		
		double[] closestSortMeasures = null;
		ArrayList<Station> closestStations;
		if(intent.hasExtra("closestSortMeasures"))
			closestSortMeasures = intent.getDoubleArrayExtra("closestSortMeasures");
		closestStations = intent.getParcelableArrayListExtra("closestStations");
		
		for (int i = 0; i < closestStations.size(); i++)
		{
			allStationsSort.add(new ObjectSort<Station>(closestStations.get(i), closestSortMeasures[i]));
		}
		
		MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, R.layout.listitem, allStationsSort);
		/* ListAdapter adapter = createListAdapter(allStationsSortDistance); */
		ListView listview = (ListView) findViewById(R.id.ClosestStationsList);
		listview.setAdapter(adapter);
		/*setListAdapter(adapter);*/
		
		final MapView map = (MapView) findViewById(R.id.mapview);
		map.setTileSource(TileSourceFactory.MAPNIK);
		map.setBuiltInZoomControls(true);
		map.setMultiTouchControls(true);
		
		final GeoPoint startPoint = createGeoPoint(null);
		final IMapController mapController = map.getController();
		mapController.setZoom(13);
		mapController.setCenter(startPoint);
		addMarker(map, null);
		
		map.invalidate();
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() 
		{
		      @Override
		      public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		      {
		    	  minLatitude = (int)(+91 * 1E6);
		    	  maxLatitude = (int)(-91 * 1E6);
		    	  minLongitude  = (int)(+181 * 1E6);
		    	  maxLongitude  = (int)(-181 * 1E6);
		    	    
		    	  final ObjectSort<Station> item = (ObjectSort<Station>) parent.getItemAtPosition(position);
		    	  map.getOverlays().clear();
		    	  		    	  
		    	  setupMapSpan(createGeoPoint(item), createGeoPoint(null));
		    	  mapController.zoomToSpan((maxLatitude - minLatitude), (maxLongitude - minLongitude));
		    	  mapController.animateTo(new GeoPoint((maxLatitude + minLatitude)/2,(maxLongitude + minLongitude)/2));
		    	  
		    	  addMarker(map, null);
		    	  addMarker(map, item);
		    	  
		    	  map.invalidate();
		    	  
		    	  Button button = (Button) findViewById(R.id.button_destination);
		    	  button.setEnabled(true);
		    	  button.setText("Select " + item.getObject().getStationName());
		    	
		    	  button.setOnClickListener(new View.OnClickListener() 
		    	  {
		              public void onClick(View v) 
		              {
		            	  // Toast.makeText(getApplicationContext(), item.getStation().getStationName() + " selected", Toast.LENGTH_SHORT).show();
		            	  Log.d("item", "item: " + item.getObject().getStationLocation()[0] + "," + item.getObject().getStationLocation()[1]);
		            	  new DisplayDirectionsIntent(context, activity, origin, item.getObject());
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
	
	private class MySimpleArrayAdapter extends ArrayAdapter<ObjectSort<Station>>
	{
		private final Context context;
		private final List<ObjectSort<Station>> stationSortList;
		
		public MySimpleArrayAdapter(Context context, int resource, List<ObjectSort<Station>> objects) 
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
			
			ObjectSort<Station> sS = stationSortList.get(position);
			
			stationName.setText(sS.getObject().getStationName());
			
			if (MainActivity.getDistance_duration().compareToIgnoreCase("distance") == 0)
				sortMeasure.setText("About " + Double.toString(sS.getSortMeasure()) + " km");
			else
			{
				if ((int) sS.getSortMeasure() == 60)
					sortMeasure.setText("About a minute");
				else if (sS.getSortMeasure() > 60)
					sortMeasure.setText("About " + Integer.toString(((int) sS.getSortMeasure())/60) + " minutes");
				else
					sortMeasure.setText("About " + Integer.toString((int) sS.getSortMeasure()) + " seconds");
			}
			
			// if sort measure is -1, then disable the lsitview all together. Inform the user via toast notification that 
			// the directions were not obtainable for some unknown reason.
			
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
	
	@Override
	public void onBackPressed() {
		// have to make sure not to create an infinite loop
	    // startActivity(new Intent(this, MainActivity.class));
	}


}

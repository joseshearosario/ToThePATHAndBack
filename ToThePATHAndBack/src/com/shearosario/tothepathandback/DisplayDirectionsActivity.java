package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Marker;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayDirectionsActivity extends Activity {

	/*private LatLng origin;
	private LatLng destination;*/
	private List<Marker> markers;
	private int markersIndex;
	private static GoogleMap gMap;
	
	/**
	 * If not at the first marker, moves to the previous step and shows the info window. If reaches the 
	 * first marker, it disables itself. Also, if the next button is disabled and we're not at the last marker, it 
	 * activates the next button. 
	 * 
	 * @param view button pressed
	 */
	public void previousStep (View view)
	{
		Button next = (Button) findViewById(R.id.button_nextStep);
		markersIndex--;
		
		if (!next.isEnabled() && markersIndex != (markers.size()-1))
			next.setEnabled(true);
			
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(markersIndex).getPosition(), 17));
		markers.get(markersIndex).showInfoWindow();
		
		if (markersIndex == 0)
			view.setEnabled(false);
	}
	
	/**
	 * If not at the first marker, moves to the next step and shows the info window. If reaches the 
	 * last marker, it disables itself. Also, if the previous button is disabled and we're not at the first marker, it 
	 * activates the previous button. 
	 * 
	 * @param view
	 */
	public void nextStep (View view)
	{
		Button previous = (Button) findViewById(R.id.button_previousStep);
		markersIndex++;
		
		if (!previous.isEnabled() && markersIndex != 0)
			previous.setEnabled(true);
			
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(markersIndex).getPosition(), 17));
		markers.get(markersIndex).showInfoWindow();
		
		if (markersIndex == (markers.size()-1))
			view.setEnabled(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_directions);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		
		/*if(intent.hasExtra("origin"))
		{
			double[] temp = intent.getDoubleArrayExtra("origin");
			origin = new LatLng (temp[0], temp[1]);
		}
		if (intent.hasExtra("destination"))
		{
			double[] temp = intent.getDoubleArrayExtra("destination");
			destination = new LatLng (temp[0], temp[1]);
		}*/
		
		String linkCollection = null;
		String nodeCollection = null;
		String points = null;
		
		if (intent.hasExtra("GuidanceLinkCollection"))
			linkCollection = intent.getStringExtra("GuidanceLinkCollection");
		if (intent.hasExtra("GuidanceNodeCollection"))
			nodeCollection = intent.getStringExtra("GuidanceNodeCollection");
		if (intent.hasExtra("shapePoints"))
			points = intent.getStringExtra("shapePoints");
		
		gMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.directionsView)).getMap();
		gMap.setMyLocationEnabled(false);
		gMap.setBuildingsEnabled(false);
		gMap.getUiSettings().setZoomControlsEnabled(false);
		
		TextView textView = (TextView) findViewById(R.id.osm_guidance);
		textView.setText(Html.fromHtml(
	            "Data provided by © OpenStreetMap contributors " +
	            "<a href=\"http://www.openstreetmap.org/copyright\">License</a>"));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		textView = (TextView) findViewById(R.id.guidance_text);
		textView.setText(Html.fromHtml(
	            "Guidance Courtesy of " +
	            "<a href=\"http://www.mapquest.com\">MapQuest</a>"));
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		
		PolylineOptions rectOptions = new PolylineOptions();
		markers = new ArrayList<Marker>();
		
		try {			
			JSONArray jGuidanceLinkCollection = new JSONArray (linkCollection);
			JSONArray jGuidanceNodeCollection = new JSONArray (nodeCollection);
			JSONArray jShapePoints = new JSONArray (points);
			
			int lastIndex = 0;
			
			for (int i = 0; i < jGuidanceNodeCollection.length(); i++)
			{
				JSONObject nodeObject = jGuidanceNodeCollection.getJSONObject(i);
				JSONArray linkIds = nodeObject.getJSONArray("linkIds");
				
				int linkIndex = 0;
				if (linkIds.length() != 0)
					linkIndex = linkIds.getInt(0);
				else
					continue;
				
				JSONObject linkObject = jGuidanceLinkCollection.getJSONObject(linkIndex);
				int shapeIndex = linkObject.getInt("shapeIndex");
				
				// The index of a specific shape point is i/2, so multiply by 2 to get the beginning index in shapePoints
				// evens are lat and odds are lng
				double lat = jShapePoints.getDouble((shapeIndex*2));
				double lng = jShapePoints.getDouble((shapeIndex*2)+1);
				
				lastIndex = ((shapeIndex*2)+1);
				
				if (i == 0)
				{
					Marker temp = gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title("Origin"));
					markers.add(temp);
				}
				else if (nodeObject.isNull("infoCollection") == false)
				{
					Marker temp = gMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(nodeObject.getJSONArray("infoCollection").getString(0)));
					markers.add(temp);
				}
			}
			
			
			for (int i = 0; i < lastIndex; i++)
			{
				double lat = jShapePoints.getDouble(i);
				i++;
				double lng = jShapePoints.getDouble(i);
				
				rectOptions.add(new LatLng(lat,lng));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gMap.addPolyline(rectOptions);
		
		markersIndex = 0;
		
		gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(markersIndex).getPosition(), 17));
		markers.get(markersIndex).showInfoWindow();
		
		gMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker arg0)
			{
				if (arg0 != null)
				{
					for (int i = 0; i < markers.size(); i++)
					{
						if (markers.get(i).equals(arg0))
						{
							markersIndex = i;
							break;
						}
					}
					
					if (markersIndex == 0)
					{
						findViewById(R.id.button_nextStep).setEnabled(true);
						findViewById(R.id.button_previousStep).setEnabled(false);
					}
					else if (markersIndex == (markers.size()-1))
					{
						findViewById(R.id.button_nextStep).setEnabled(false);
						findViewById(R.id.button_previousStep).setEnabled(true);
					}
					else
					{
						findViewById(R.id.button_nextStep).setEnabled(true);
						findViewById(R.id.button_previousStep).setEnabled(true);
					}
					
					gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arg0.getPosition(), 17));
					
					arg0.showInfoWindow();
					
					return true;
				}
				
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_directions, menu);
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
}

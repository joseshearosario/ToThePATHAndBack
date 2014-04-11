package com.shearosario.tothepathandback;

import java.util.ArrayList;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;

public class DisplayDirectionsActivity extends Activity {

	private LatLng origin;
	private LatLng destination;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_directions);

		Intent intent = getIntent();
		context = this;
		
		if(intent.hasExtra("origin"))
		{
			double[] temp = intent.getDoubleArrayExtra("origin");
			origin = new LatLng (temp[0], temp[1]);
		}
		if (intent.hasExtra("destination"))
		{
			double[] temp = intent.getDoubleArrayExtra("destination");
			destination = new LatLng (temp[0], temp[1]);
		}
		
		final MapView map = (MapView) findViewById(R.id.directionsView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        
        final GeoPoint startPoint = new GeoPoint(origin.latitude, origin.longitude);
        IMapController mapController = map.getController();
        mapController.setZoom(9);
        mapController.setCenter(startPoint);
		
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(startMarker);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));
        startMarker.setTitle("Start point");
        
		new Thread(new Runnable()
		{

			@Override
			public void run() 
			{
		        RoadManager roadManager = new MapQuestRoadManager(MainActivity.getAPP_KEY());
		        roadManager.addRequestOption("routeType=fastest");
		        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
		        waypoints.add(startPoint);
		        GeoPoint endPoint = new GeoPoint(destination.latitude, destination.longitude);
		        waypoints.add(endPoint);
		        Road road = roadManager.getRoad(waypoints);
		        Polyline roadOverlay = RoadManager.buildRoadOverlay(road, context);
		        map.getOverlays().add(roadOverlay);
		        
		        Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_node);
		        for (int i=0; i<road.mNodes.size(); i++){
		                RoadNode node = road.mNodes.get(i);
		                Marker nodeMarker = new Marker(map);
		                nodeMarker.setPosition(node.mLocation);
		                nodeMarker.setIcon(nodeIcon);
		                nodeMarker.setTitle("Step "+i);
		                map.getOverlays().add(nodeMarker);
		                nodeMarker.setSnippet(node.mInstructions);
		                nodeMarker.setSubDescription(Road.getLengthDurationText(node.mLength, node.mDuration));
		        }
		        
		        map.invalidate();
			}
			
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_directions, menu);
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
}

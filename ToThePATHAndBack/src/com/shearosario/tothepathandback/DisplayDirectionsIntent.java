/**
 * 
 */
package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.views.MapView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author shea
 *
 */
public class DisplayDirectionsIntent
{
	private List<Maneuver> maneuvers = new ArrayList<Maneuver>();
	private String directions;
	private Context context;
	private Activity activity;
	private LatLng origin;
	private Station station;
	private LatLng destination;
	
	private void parseDirections()
	{
		JSONObject jObject = null;
		JSONObject jRoute = null;
		JSONArray jLegs = null;
		JSONObject jLeg = null;
		JSONArray jManeuvers = null;
		
		try
		{
			jObject = new JSONObject(directions);
			jRoute = jObject.getJSONObject("route");
			jLegs = jRoute.getJSONArray("legs");
			jLeg = jLegs.getJSONObject(0);
			jManeuvers = jLeg.getJSONArray("maneuvers");
			
			for (int i = 0; i < jManeuvers.length(); i++)
			{
				JSONObject tempManeuver = jManeuvers.getJSONObject(i);
				maneuvers.add(new Maneuver(tempManeuver.getString("narrative"), tempManeuver.getString("iconUrl"), 
						tempManeuver.getDouble("distance"), tempManeuver.getString("mapUrl")));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DisplayDirectionsIntent(Context c, Activity a, LatLng o, Station s) 
	{
		context = c;
		activity = a;
		origin = o;
		station = s;
		destination = new LatLng(station.getStationLocation()[0], station.getStationLocation()[1]);
		
		// Create URL
		String [] allJSONURLs = Directions.getEntranceURLS(origin, station.getEntranceList());
		
		// Download JSON
		Directions.DownloadAllData downloadDirections = new Directions.DownloadAllData();
		downloadDirections.execute(allJSONURLs);
		
		String[] directionsMatrix = null;
		try {
			directionsMatrix = downloadDirections.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// get sort measures
		double[] allSortMeasures = null;
		try {
			allSortMeasures = DirectionsJSONParser.getDistanceOrDuration(directionsMatrix);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// sort all entrances
		ArrayList<ObjectSort<Entrance>> allEntrancesSort = new ArrayList<ObjectSort<Entrance>>();
		for (int i = 0; i < station.getEntranceList().size(); i++)
			allEntrancesSort.add(new ObjectSort<Entrance>(station.getEntranceList().get(i), allSortMeasures[i]));
		Collections.sort(allEntrancesSort);
		
		// prepare to send closest entrance to DisplayDirectionsActivity via intent
		Entrance closestEntrance = allEntrancesSort.get(0).getObject();
				
		// set up map
		Intent intent = new Intent (context, DisplayDirectionsActivity.class);
		intent.putExtra("origin", new double[] {origin.latitude, origin.longitude});
		intent.putExtra("destination", new double[] {closestEntrance.getEntranceLocation()[0], closestEntrance.getEntranceLocation()[1]});
		activity.startActivity(intent);
	}
	
	private class Maneuver
	{
		private String narrative;
		private String iconURL;
		private double distance;
		private String mapURL;
		private String textDisplay;
		
		public Maneuver(String n, String i, double d, String m) 
		{
			narrative = n;
			iconURL = i;
			distance = d;
			mapURL = m;
			textDisplay = this.getNarrative() + " for " + this.getDistance() + " km";
		}

		/**
		 * @return the narrative
		 */
		private String getNarrative() {
			return narrative;
		}

		/**
		 * @return the iconURL
		 */
		private String getIconURL() {
			return iconURL;
		}

		/**
		 * @return the distance
		 */
		private double getDistance() {
			return distance;
		}

		/**
		 * @return the mapURL
		 */
		private String getMapURL() {
			return mapURL;
		}

		/**
		 * @return the textDisplay
		 */
		private String getTextDisplay() {
			return textDisplay;
		}
	}
}

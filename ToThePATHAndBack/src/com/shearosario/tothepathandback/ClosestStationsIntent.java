package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * Serves to prepare an intent, that'll be sent to the next activity (ClosestStationsAcitivity) when we start it. 
 * We call to create the URLs for each station, call on them and save what is returned. If it is viable, then 
 * extract a sort measure (distance/duration) specified by the user, and we will use it to select and sort three stations 
 * for use. After we follow a similar process for all the appropariate entrances at each of the three stations. However, 
 * we only selected one entrance for each station. The sort measure used, the stations and their entrance, are then put into an 
 * intent and the activity is called.
 * 
 * @author shea
 *
 */

public class ClosestStationsIntent 
{	
	/**
	 * If the user has selected in the main activity that they need to enter a station through a handicap entrance, 
	 * then here we select the stations that have that specific type of entrance.
	 * 
	 * @return an arraylist of stations that have a specific handicap entrance selected by the user
	 */
	private ArrayList<Station> getHandicapAccessStations()
	{
		ArrayList<Station> handicapStations = new ArrayList<Station>();
		
		for (int i = 0; i < MainActivity.getAllStations().size(); i++)
		{
			if (MainActivity.getAllStations().get(i).isHandicapAccessible(MainActivity.getHandicapAccess()))
				handicapStations.add(MainActivity.getAllStations().get(i));
		}
		
		return handicapStations;
	}
	
	/**
	 * To download the state of the origin
	 * 
	 * @author shea
	 *
	 */
	private static class downloadReverseGeoCode extends AsyncTask<String, Void, String> 
	{
		@Override
		protected String doInBackground(String... params) {
			String tempURL = params[0];
			String data = null;
						
			try {
				data = Directions.downloadFromURL(tempURL);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
									
			return data;
		}
	}
	
	/**
	 * Calls on downloadReverseGeoCode class to obtain the state where the origin is at. If it is in New York or New Jersey, 
	 * then we return the abbreviation of it. Else, we return the full name of the state. The reason for this is to eliminate 
	 * URL calls for all stations. This way, we only call on stations that are in the same state as the origin location (provided 
	 * they're in NY or NJ).
	 * 
	 * @param origin the origin of the user
	 * @return the state where the origin is located at
	 */
	private String getOriginState(LatLng origin)
	{
		String tempJSON = null;
		String originState = null;
		String url = "http://open.mapquestapi.com/nominatim/v1/reverse.php?format=json&lat=" + origin.latitude + "&lon=" + origin.longitude;
				
		downloadReverseGeoCode dataAsync = new downloadReverseGeoCode();
		dataAsync.execute(url);
		try {
			tempJSON = dataAsync.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			JSONObject jObject = new JSONObject(tempJSON);
			JSONObject jAddress = jObject.getJSONObject("address");
			originState = jAddress.getString("state");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (originState.compareToIgnoreCase("New York") == 0)
			return "NY";
		else if (originState.compareToIgnoreCase("New Jersey") == 0)
			return "NJ";
		else
			return originState;
	}
	
	/**
	 * Create an array list of Strings. Each element is a URL for each station in the array list. We'll use the URL to 
	 * call and obtain a route matrix.
	 * 
	 * @param allStations an array list of stations 
	 * @param origin the origin location
	 * @return an array of String, a URL for each station object  
	 */
	private static String[] createURLs (ArrayList<Station> allStations, LatLng origin)
	{
		return Directions.getAllURLS(origin, allStations);
	}
	
	/**
	 * Using DownloadAllData in Directions, we call each url and then save whatever returns.
	 * 
	 * @param urls an array of string urls
	 * @return an array of strings, each the result of calling and saving what the url returns
	 */
	private String[] downloadJSON (String[] urls)
	{
		/* Get direction data from origin to each station from URLs */
		Directions.DownloadAllData downloadAllData = new Directions.DownloadAllData();
		downloadAllData.execute(urls);
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
		
		return allJSONdata;
	}
	
	/**
	 * Using getDistanceOrDuration in DirectionsJSONParser, we parse each string and extract the value of either 
	 * distance or duration from the origin to each location searched.
	 * 
	 * @param data an array of string with each element being whatever is returned from their respective URL calls 
	 * @return a double array for the sort measure that is obtained from each data string
	 */
	private double[] getAllSortMeasures(String [] data)
	{
		double[] allSortMeasures = null; 
		try {
			allSortMeasures = DirectionsJSONParser.getDistanceOrDuration(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allSortMeasures;
	}
	
	/**
	 * This is possible because we have kept the order of whatever is being searched for with API calls to Open Mapquest 
	 * and what has returned. We can now compare each location with their respective distance or duration from the origin. 
	 * The work is done by storing the index of the smallest element value possible in allSortMeasures into an array of size 1 to 3. 
	 * We traverse the sort measures as many times as there are indices that we must save. For example, if we are working with an Entrance object, 
	 * then we only need to save one index, the smallest element in allSortMeasures.
	 * <p>
	 * After we obtain each index, we then get the object in the array list that corresponds with the index we stored and add them to an array list that'll 
	 * be returned. The result is a limited array list of objects that are in order based on their distance or duration from the origin.
	 * 
	 * @param allObjects an array list of objects
	 * @param allSortMeasures a double array of sort measures
	 * @return an array list of ordered object from allObjects based on the ascending order of allSortMeasures 
	 */
	private <T> ArrayList<T> sortWithMeasures (ArrayList<T> allObjects, double[] allSortMeasures)
	{
		int[] sortIndex;
		if (allObjects.get(0).getClass().toString().equalsIgnoreCase("class com.shearosario.tothepathandback.Entrance"))
			sortIndex = new int[1];
		else if (allObjects.size() < 3)
			sortIndex = new int[allObjects.size()];	
		else
			sortIndex = new int[3];
		Arrays.fill(sortIndex, 0);
		
		double[] tempAllSortMeasures = allSortMeasures.clone();
		
		for (int i = 0; i < sortIndex.length; i++)
		{
			/*
			 * to avoid a bug that occurs when the first element(s) of 
			 * allSortMeasures are -1. 
			 * 
			 * All the values in sortIndex are 0 by 
			 * default. If there is a -1 in allSortMeasures[0], then no 
			 * element at or after 0 will be valued less than -1, which would cause 
			 * the constant selection of the first element from allSortMeasures.
			 */
			for (int j = 0; j < tempAllSortMeasures.length; j++)
			{
				if (tempAllSortMeasures[j] > 0)
				{
					sortIndex[i] = j;
					break;
				}
			}
			
			if (sortIndex[i] == tempAllSortMeasures.length)
				continue;

			for (int j = 0; j < tempAllSortMeasures.length; j++)
			{
				/*
				 * if the value at the  j index of tempAllSortMeasures is greater than 0 
				 * and is less than the value at the sortIndex[i] index of allSortMeasures, then 
				 * save the j index in sortIndex[i]
				 */				

				if (tempAllSortMeasures[j] <= 0)
					continue;
				
				if(tempAllSortMeasures[j] <= tempAllSortMeasures[sortIndex[i]])
					sortIndex[i] = j;
			}
			
			tempAllSortMeasures[sortIndex[i]] = -1;
		}
		
		ArrayList<T> allObjectsSort = new ArrayList<T>();
		
		for (int i = 0; i < sortIndex.length; i++)
			allObjectsSort.add(allObjects.get(sortIndex[i]));
		
		return allObjectsSort;
	}
	
	public ClosestStationsIntent (final double[] latlng, final Context c, final Activity a)
	{
		CurrentLocationHandler.getLocationManager().removeUpdates(CurrentLocationHandler.gpsListener);
		CurrentLocationHandler.getLocationManager().removeUpdates(CurrentLocationHandler.networkListener);
		final ProgressDialog myDialog = ProgressDialog.show(c, null, "Determining your three closest stations...", true);
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				Intent intent = new Intent (c, ClosestStationsActivity.class);
				ArrayList<Station> allStations = null;
				LatLng origin = new LatLng(latlng[0],latlng[1]);
				
				/*
				 * if the user requires it, we get stations that have a specific handicap entrance. Else, we get all stations.
				 */
				if (MainActivity.getHandicapAccess() == null)
					allStations = MainActivity.getAllStations();
				else
					allStations = getHandicapAccessStations();
				
				/*
				 * If the origin is in New York or New Jersey, it'll remove all stations that are not in their state. Else, it'll 
				 * use all the stations so far. 
				 */
				String state = getOriginState(origin);
				if (state.compareToIgnoreCase("NY") == 0 || state.compareToIgnoreCase("NJ") == 0)
				{
					for (int i = 0; i < allStations.size();)
					{
						if (allStations.get(i).getStationState().compareToIgnoreCase(state) != 0)
						{	
							allStations.remove(i);
						}
						else
							i++;
					}
				}
				
				/* obtain all necessary info for each station */
				String[] urls = createURLs(allStations, origin);
				String[] json = downloadJSON(urls);
				double[] sortMeasures = getAllSortMeasures(json);
				/* get the <=3 closest stations to the origin */ 
				ArrayList<Station> allStationsSort = sortWithMeasures(allStations, sortMeasures);
				
				/*
				 * At this point, we have the three closest stations. Now we will go through all appropriate 
				 * entrances at each station, get the closest one at each, and report those numbers as their 
				 * sort measures for the next activity.
				 */
				
				double[] closestSortMeasures = new double[allStationsSort.size()];
				ArrayList<Station> closestStations = new ArrayList<Station>();
				ArrayList<Entrance> closestEntrances = new ArrayList<Entrance>();
				
				for (int i = 0; i < allStationsSort.size(); i++)
				{
					Station tempStation = allStationsSort.get(i);
										
					ArrayList<Entrance> tempEntrances = new ArrayList<Entrance>();
					if (MainActivity.getHandicapAccess() == null)
						tempEntrances = tempStation.getEntranceList();
					else
						tempEntrances = tempStation.getHandicapAccessEntrances();
					
					/* get all necessary info */
					String[] entranceURLS = Directions.getEntranceURLS(origin, tempEntrances);
					String[] entranceJSON = downloadJSON(entranceURLS);
					double[] entranceSortMeasures = getAllSortMeasures(entranceJSON);
					
					/* obtain the closest entrance to the origin at this station */
					ArrayList<Entrance> closeE = sortWithMeasures(tempEntrances, entranceSortMeasures);
					closestEntrances.add(closeE.get(0));
					
					/* 
					 * sort the array of entranceSortMeasures, the measure of distance or duration from the origin 
					 * to each entrance. Then obtain the smallest element that is not less than or equal to zero. 
					 * Because we have maintained an order, we can confidently say that the smallest element is the sort 
					 * measure for this station and the closest entrance saved above.
					 */
					Arrays.sort(entranceSortMeasures);
					
					int sortedIndex;					
					for (sortedIndex = 0; sortedIndex < entranceSortMeasures.length; sortedIndex++)
					{
						if (entranceSortMeasures[sortedIndex] > 0)
							break;
					}
					
					if (sortedIndex == entranceSortMeasures.length)
						closestSortMeasures[i] = 0;					
					else		
						closestSortMeasures[i] = entranceSortMeasures[sortedIndex];					
				}
				
				closestStations = sortWithMeasures(allStationsSort, closestSortMeasures);
				closestEntrances = sortWithMeasures(closestEntrances, closestSortMeasures);
				Arrays.sort(closestSortMeasures);
				
				intent.putExtra("Manual", latlng);
				intent.putExtra("closestSortMeasures", closestSortMeasures);
				intent.putParcelableArrayListExtra("closestStations", closestStations);
				intent.putParcelableArrayListExtra("closestEntrances", closestEntrances);
				
				myDialog.dismiss();
				a.startActivity(intent);
			}
		}).start();
	}
}

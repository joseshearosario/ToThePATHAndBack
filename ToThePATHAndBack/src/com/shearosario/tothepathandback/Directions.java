package com.shearosario.tothepathandback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * We use the Directions class for multiple tasks, but they all relate to the obtaining and 
 * parsing of the JSON data retrieved by an Directions API call. With Directions, we can 
 * create the URL(s) that we would need to connect with. Then we're able to connect to the given URL(s) 
 * in order to download the directions in JSON format. We can then call a way to parse through 
 * all those directions in order to later create a Map view of the waypoints and street directions.
 * 
 * @author shea
 */
public class Directions {	
	/**
	 * Extends AsyncTask to try and fetch JSON data from each String URL of each station 
	 * in background when executed. Will get a array of strings each with JSON info.   
	 */
	public static class DownloadAllData extends AsyncTask<String, Void, String> 
	{
		@Override
		protected String doInBackground(String... params) 
		{
			String data = downloadFromURL(params[0]);
						
			return data;
		}
	}
	
	// http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/
	
	public static String getRouteMatrixURL (LatLng origin, ArrayList<LatLng> destinations)
	{
		String str_key = "key=" + MainActivity.getAPP_KEY();
		String str_origin = "&from="+origin.latitude+","+origin.longitude;
		
		String str_destination = "";
		for (int i = 0; i < destinations.size(); i++)
		{
			str_destination = str_destination + "&to=" +destinations.get(i).latitude + "," + destinations.get(i).longitude;	
		}		
		
		String str_unit = "&unit=" + MainActivity.getUnitMeasurement();
		String str_routeType = "&routeType="+ClosestStationFragment.getMatrixMode();
		String str_oneToMany = "&oneToMany=true";
		
		String parameters = str_key + str_origin + str_destination + str_unit + str_routeType + str_oneToMany;
		
		String url = "http://open.mapquestapi.com/directions/v2/routematrix?" + parameters;
				
		return url;
	}
	
	public static String getGuidanceURL (LatLng origin, LatLng destination)
	{		
		String str_key = "key=" + MainActivity.getAPP_KEY();
		String str_origin = "&from="+origin.latitude+","+origin.longitude;
		String str_destination = "&to="+destination.latitude+","+destination.longitude;
		String str_routeType = "&routeType="+ClosestStationFragment.getTransportMode();
		String str_timeType = "&timeType=1";
		String str_shapeFormat = "&shapeFormat=raw";
		String str_narrativeType = "&narrativeType=text";
		String str_unit = "&unit=" + MainActivity.getUnitMeasurement();
		String str_fishbone = "&fishbone=false";
		
		String parameters = str_key + str_origin + str_destination + str_unit + 
				str_routeType + str_narrativeType + str_shapeFormat + str_timeType + str_fishbone;
		
		String url = "http://open.mapquestapi.com/guidance/v1/route?" + parameters;
		
		return url;
	}
	
	/**
	 * <p>With a given URL string, returns JSON data retrieved after an attempt to open and connect 
	 * with the URL and saving it with a string buffer.</p>
	 * 
	 * @param str_URL - String containing Directions API call URL  
	 * @return String containing all JSON directional data returned from calling URL
	 */
	public static String downloadFromURL (String str_URL)
	{
		String data = "";
		URL url;
		HttpURLConnection connection = null;
		InputStream iStream = null;
				
		try {
			// Opens connection to Google
			url = new URL (str_URL);
			
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();
			iStream = connection.getInputStream();
			
			//Convert the input stream from Google to a string
			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
			StringBuffer sb = new StringBuffer();
			String line = "";
			
			while ((line = br.readLine()) != null)
				sb.append(line);
			data = sb.toString();
			
			// Close/disconnect
			br.close();
			iStream.close();
			connection.disconnect();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d("url", "url download fail");
			e.printStackTrace();
		}
		
		return data;
	}
}
package com.shea.tothepathandback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;

public class Directions {
	private final static String API_KEY = "AIzaSyCvgz5H4JZycSSQ8YYQwmONvqwce9JZEwg";
	private final static String OUTPUT = "json";
	
	// Starts fetching/downloading JSON data from URL passed
	// Called in the main activity
	// In background, it will download the JSON data provided by the URL passed through
	// getURL is called in main activity before DownloadTask is called, which is passed in the execute method
	public static class DownloadTask extends AsyncTask<String, Void, String>
	{
		String JSONdata = "";
		
		@Override
		protected String doInBackground(String... url) 
		{
			JSONdata = downloadFromURL(url[0]);
			return JSONdata;
		}
		
		// Once data is saved in string, it will be parsed through
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			ParserTask parserTask = new ParserTask();
			parserTask.execute(result);
		}

		/**
		 * @return the JSON data
		 */
		public String getJSONData() {
			return JSONdata;
		}
	}
	
	public static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>>
	{
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) 
		{
			JSONObject jObject;
			List<List<HashMap<String, String>>> route = null;
			
			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();
				route = parser.parse(jObject);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return route;
		}

		// http://wptrafficanalyzer.in/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android-api-v2/
	}
	
	public static String getUrl (LatLng origin, LatLng destination)
	{
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		String str_destination = "destination="+destination.latitude+","+destination.longitude;
		String sensor = "sensor=false";
		
		String parameters = str_origin+"&"+str_destination+"&"+sensor
				+"&key="+API_KEY;;
		
		String url = "https://maps.googleapis.com/maps/api/directions/"+OUTPUT+"?"+parameters;
		return url;
	}
	
	public static String getUrl (LatLng origin, LatLng destination, boolean sensor, String mode, String avoid)
	{
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		String str_destination = "destination="+destination.latitude+","+destination.longitude;
		String str_sensor = "sensor=";
		String str_avoid = "avoid="+avoid;
		String str_mode = "mode="+mode;
		String str_departure = "departure_time="+String.valueOf(System.currentTimeMillis());
		
		if (sensor)
			str_sensor = sensor+"true";
		else
			str_sensor = sensor+"false";
		
		String parameters = str_origin+"&"+str_destination+"&"+str_sensor
				+"&"+str_avoid+"&"+str_mode+"&"+str_departure
				+"&key="+API_KEY;
		
		String url = "https://maps.googleapis.com/maps/api/directions/"+OUTPUT+"?"+parameters;
		return url;
	}
	
	private static String downloadFromURL (String str_URL)
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
			e.printStackTrace();
		}
		
		return data;
	}

	public static String[] getAllJSONdata (LatLng origin, Station[] stations)
	{
		String[] allJSONs = new String[stations.length];

		for (int i = 0; i < stations.length; i++)
		{
			String url = Directions.getUrl(origin, stations[i].getStationLocation());	
			allJSONs[i] = downloadFromURL(url);
		}
		
		return allJSONs;
	}
}
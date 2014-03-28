package com.shearosario.tothepathandback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONObject;
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
	public static class DownloadAllData extends AsyncTask<String[], Void, String[]> 
	{
		@Override
		protected String[] doInBackground(String[]... params) {
			String[] tempArray = params[0];
			String[] data = new String[tempArray.length];
			for (int i = 0; i < tempArray.length; i++)
			{
				try {
					data[i] = downloadFromURL(tempArray[i]);
					/*Log.d("transportRoute", "transportRoute: " + MainActivity.transportMode);
					Log.d("matrixRoute", "matrixRoute: " + MainActivity.matrixMode);
					Log.d("downloadAllData", data[i]);*/
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return data;
		}
	}
	
	/**
	 * Starts fetching/downloading JSON data from single URL passed
	 * In background, it will download the JSON data provided by the URL passed through
	 */
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
	
	
	/**
	 * In the background parse through the JSON data contained in a passed String
	 */
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
	
	public static String getRouteMatrixURL (LatLng origin, LatLng destination)
	{
		String str_origin = "from="+origin.latitude+","+origin.longitude;
		String str_destination = "to="+destination.latitude+","+destination.longitude;
		String str_unit = "unit=k";
		String str_routeType = "routeType="+MainActivity.matrixMode;
		String str_key = "key=" + MainActivity.APP_KEY;
		
		String parameters = str_key + "&" +
							str_origin + "&" +
							str_destination + "&" + 
							str_unit + "&" +
							str_routeType;
		
		String url = "http://open.mapquestapi.com/directions/v2/routematrix?" + parameters;
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

	/**
	 * <p>With a passed array of stations and the latitude and longitude of the current location, we 
	 * call getURL() to create a String of the URL that we'll use to obtain directions. We call getURL() 
	 * in each iteration.</p> 
	 * 
	 * @see #getUrl(LatLng, LatLng)
	 * @param origin - LatLng object that we'll use as the origin for the URL
	 * @param stations - An array of Station objects that we'll use each as a destination for the URL
	 * @return an array of Strings, each represents a Directions API URL from the origin to each station 
	 */
	public static String[] getAllURLS (LatLng origin, Station[] stations)
	{
		String[] allURLs = new String [stations.length];
		for (int i = 0; i < stations.length; i++)
			allURLs[i] = Directions.getRouteMatrixURL(origin, stations[i].getStationLocation());
		return allURLs;
	}
}
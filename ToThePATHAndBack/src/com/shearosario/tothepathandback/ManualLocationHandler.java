package com.shearosario.tothepathandback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.EditText;

/**
 * Called when the user presses the search button on their keyboard, and there is 
 * text, here we try to convert the text input from the user into geo-coordinates 
 * that we can use in the next activity to get directions.  
 * 
 * @author shea
 *
 */
public class ManualLocationHandler
{		
	/**
	 * private variables
	 */
	private static Context context;
	private static Activity activity;
	private static String manualLocation;
	private static String state; 
	private static String county;
	
	/**
	 * A private class that is instantiated in geocode, and it is used to download 
	 * a call to the Open Mapquest Nominatim search. It'll try and convert a location to their 
	 * geo-coordinates. Calls downloadFromURL to try and download and return that data. 
	 * 
	 * @author shea
	 *
	 */
	private static class downloadGeocode extends AsyncTask<String, Void, String> 
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
	 * Create a URL string and calls it using the private class downloadGeocode, then get whatever is returned. 
	 * If nothing was downloaded, that means the JSONArray that was converted from the downloaded string has no elements and 
	 * will return null. This means that the location the user gave could not be converted to geo-coordinates by Nominatim and OpenStreetMap. 
	 * Else, we extract each element and store them in our latitude and longitude array. That double array is then returned.  
	 * 
	 * @return A double array for latitude and longitude respectively, if available. Else null.
	 * @throws UnsupportedEncodingException
	 */
	private static double[] geocode () throws UnsupportedEncodingException
	{
		/* 
		 * They're are no limits to using Nominatim, but if an approximate geolocation
		 * cannot be found, then we might need to use Mapquest or Google.
		 */		
		double[] latlng = new double[2];
		String tempJSON = null;
		String tempManualLocation = URLEncoder.encode(manualLocation, "UTF-8");
		String geocodeURL = "http://open.mapquestapi.com/nominatim/v1/search.php?format=json&q="+tempManualLocation+"&limit=1&addressdetails=1";
		
		downloadGeocode geocodeData = new downloadGeocode();
		geocodeData.execute(geocodeURL);
		try {
			tempJSON = geocodeData.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		try {
			JSONArray jArray = new JSONArray(tempJSON);
						
			/* 
			 * Nominatim could not covert manualLocation to latitude and longitude.
			 */
			if (jArray.length() == 0)
				latlng = null;
			else
			{
				for (int i = 0; i < jArray.length(); i++)
				{
					/*
					 * get value from array element casted as a JSONObject
					 * then save into latlng
					 * first latitude then longitude
					 */
					latlng[0] = ((JSONObject) jArray.get(i)).getDouble("lat");
					latlng[1] = ((JSONObject) jArray.get(i)).getDouble("lon");
					state = ((JSONObject) jArray.get(i)).getJSONObject("address").getString("state");
					county = ((JSONObject) jArray.get(i)).getJSONObject("address").getString("county");
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return latlng;
	}
	
	/**
	 * Calls geocode, which then instantiates downloadGeocode, to try and obtain the latitude and longitude of the location the 
	 * user manually inputted. If a valid array is returned from geocode, then we call ClosestStationsIntent and there is where the intent 
	 * for the next activity is initialized. Else, an alert is shown to tell the user that the location they input could not be converted. 
	 * They'll be directed back to the main activity to input another location, or use their current location.
	 * 
	 * @param c context of the activity that called
	 * @param a activity that called
	 * @param manual user input location
	 * @throws UnsupportedEncodingException
	 */
	public static void createClosestStationsIntent(Context c, Activity a, String manual) throws UnsupportedEncodingException
	{	
		context = c;
		activity = a;
		manualLocation = manual;
		state = null;
		county = null;
		double[] latlng = null;
				
		try {
			latlng = geocode();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
							
		/*
		 * If no latitude and longitude is returned, which means that the location the user inputted is invalid for OpenStreetMap and nothing was downloaded, 
		 * then alert the user that the location is bad and to try again. Else, ClosestStationsIntent is called and the intent for the next activity is created.
		 */
		if (latlng == null)
		{
			new AlertDialog.Builder(context)
			.setTitle("Typed Location Does Not Exist")
			.setMessage("The location you entered could not be found. You may need to be more or less specific.")
			.setPositiveButton("Okay", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					EditText text = (EditText) activity.findViewById(R.id.origin_manual);
					text.setText("");
					dialog.cancel();
				}
			}).setCancelable(false).show();
		}
		
		else
			new ClosestStationsIntent(latlng, context, activity, state, county);
	}
}

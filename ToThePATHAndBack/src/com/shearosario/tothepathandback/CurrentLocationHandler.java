package com.shearosario.tothepathandback;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.app.AlertDialog;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.provider.Settings;

/**
 * 
 * Handles obtaining and passing the current location of the user to the first intent, for the second activity. 
 * If available, the last known location is saved. It needs to be within 50 meters in accuracy and less than two minutes. 
 * Listeners are activated for GPS and Network location, and they're saved as the current location. After a listener obtains 
 * a location, it is removed. Once a location is obtained, from whatever source, the button in MainActivity to use the current 
 * location as the origin is enabled. When the location button is clicked, the class concerning this intent is instantiated.
 * 
 * @author shea
 */
public class CurrentLocationHandler {

	/**
	 * Public variables for the userLocation class
	 */
	private static Activity activity;
	private static Context context;
	private static final int TWO_MINUTES = 1000*60*2;
	private static LocationManager locationManager;
	private static Location currentLocation;

	/**
	 * When a location is obtained and stored as the current location of the
	 * user, the location button in the main activity is activated. If the user
	 * wishes to use their current location and click on that button, this is
	 * called. First we determine the state of the current location, in order to
	 * draw down the number of stations we'd have to checked. Calls on
	 * downloadReverseGeoCode class to obtain the state where the origin is at.
	 * It will pass the current location, context, the state, and activity, to
	 * be used to create an intent and start the ClosestStations activity.
	 * Although, all of that is handled by ClosestStationsIntent.
	 */
	public static void createClosestStationsIntent() 
	{		
		String tempJSON = null;
		String originState = null;
		String originCounty = null;
		String url = "http://open.mapquestapi.com/nominatim/v1/reverse.php?format=json&lat=" + 
				currentLocation.getLatitude() + "&lon=" + currentLocation.getLongitude();
				
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
			originCounty = jAddress.getString("county");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new ClosestStationsIntent(new double[]{currentLocation.getLatitude(), currentLocation.getLongitude()}, context, activity, originState, originCounty);
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
	 * This location listener is unique for the GPS. The overridden method
	 * onLocationChanged will update the current location of the user. Once 
	 * a location is obtained, the listener is removed and the location is set.
	 */
	public static LocationListener gpsListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(gpsListener);
			setCurrentLocation(location, 2);
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("gpsDisabled", provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("gpsEnabled", provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	};

	/**
	 * This location listener is unique for the Network. The overridden method
	 * onLocationChanged will update the current location of the user. Once 
	 * a location is obtained, the listener is removed and the location is set.
	 */
	public static LocationListener networkListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			locationManager.removeUpdates(networkListener);
			setCurrentLocation(location, 3); 
		}

		@Override
		public void onProviderDisabled(String provider) {
			Log.d("networkDisabled", provider);
		}

		@Override
		public void onProviderEnabled(String provider) {
			Log.d("networkEnabled", provider);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}
	};
	private static View view;


	/**
	 * Constructor to handle obtaining the current location. Will call getLocation() in order to
	 * create a location manager.
	 * 
	 * @param c the context of the main activity
	 * @param a the main activity
	 * @param rootView 
	 */
	public CurrentLocationHandler(Context c, Activity a, View rootView) {
		context = c;
		activity = a;
		currentLocation = null;
		locationManager = null;
		view = rootView;
		getLocation();
	}

	/**
	 * Will try to create a location manager from the passed context. From that,
	 * we will try and check if GPS or network is available and enabled. If
	 * we're able to obtain a location from some source that's on, first we try
	 * and obtain a last known location from the GPS and Network providers. If
	 * both are available, we compare and determine if they're good enough for
	 * us to use. By default we'll always choose GPS over Network. Regardless if
	 * we can obtain a last known location, we'll start a listener for GPS and
	 * Network.
	 */
	private void getLocation() 
	{
		try {
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			/*
			 * If neither GPS or Network is enabled, the user has the option to activate it or be forced to 
			 * manually input a location. The button is never enabled.
			 */
			if (!isGPSEnabled && !isNetworkEnabled) 
			{
				new AlertDialog.Builder(context)
				.setTitle("No Location Provider Enabled")
				.setMessage("You currently do not have GPS and mobile network location provider turned on.")
				.setPositiveButton("Turn on GPS/Network", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		                context.startActivity(intent);
		                activity.finish();
					}
				})
				.setNegativeButton("Enter a manual location", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Button originCurrentView = (Button) view.findViewById(R.id.origin_current);
						originCurrentView.setText("Location not availble");
					}
				}).setCancelable(false).show();
			}
			/* We're able to get the current location from some source. */
			else 
			{
				Location lastGPS = null, lastNetwork = null;
				
				/* Obtain the last known location if GPS and Network is available. */
				if (isGPSEnabled)
					lastGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (isNetworkEnabled)
					lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				
				/* If there is a lastGPS and a lastNetork, return either best last known location. */ 
				if (lastGPS != null && lastNetwork != null)
				{
					Location tempLocation = isBetterLastKnownLocation(lastGPS, lastNetwork);

					boolean isTempNull = (tempLocation == null);
					if (isTempNull == false)
					{
						if (tempLocation.getProvider().compareToIgnoreCase("gps") == 0)
							setCurrentLocation(tempLocation, 0);
						else
							setCurrentLocation(tempLocation, 1);
					}
				}
				
				/* Else if there is no lastNetwork but there is a lastGPS. */ 
				else if (lastGPS != null && lastNetwork == null)
				{
					boolean isGPSGood = isLastKnownLocationGood(lastGPS);
					if (isGPSGood)
						setCurrentLocation(lastGPS, 0);
				}
				
				/* Else if there is a lastGPS but no lastNetwork. */ 
				else if (lastGPS == null && lastNetwork != null)
				{
					boolean isNetworkGood = isLastKnownLocationGood(lastNetwork);
					if (isNetworkGood)
						setCurrentLocation(lastNetwork, 1);
				}
				
				/*
				 * We'll activate the listeners, even if we can use the last
				 * known location. It's quite possible that we can still call
				 * the listeners with currentLocation null.
				 */				
				if (isGPSEnabled && currentLocation == null)			
				{
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, gpsListener);
				}
				if (isNetworkEnabled && currentLocation == null)
				{
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, networkListener);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when there is a last known GPS and Network provider location. We'll be using the criteria that 
	 * the location was obtained within the last two minutes, and then if they have an accuracy of less than or equal to 
	 * 50 meters. If both are viable candidates, then we'll always choose GPS over Network.
	 * 
	 * @param lG GPS last known location to be compared
	 * @param lN Network last known location to be compared
	 * @return The location that best meets the standards of "good enough". If neither, then null.
	 */
	private Location isBetterLastKnownLocation (Location lG, Location lN)
	{ 
		long gpsTimeDelta = System.currentTimeMillis() - lG.getTime();
		long networkTimeDelta = System.currentTimeMillis() - lN.getTime();
	    boolean gpsNew = ((gpsTimeDelta > 0) && (gpsTimeDelta < TWO_MINUTES));
	    boolean networkNew = ((networkTimeDelta > 0) && (networkTimeDelta < TWO_MINUTES));
	    
	    /* if gps or network location is within two minutes */
	    if (gpsNew || networkNew)
	    {	    
	    	/* if both gps and network are within two minutes */
	    	if (gpsNew && networkNew)
	    	{
	    		float gpsAccuracy = lG.getAccuracy();
	    		boolean gpsAccurate = ((gpsAccuracy <= (float) 50) && (gpsAccuracy > 0));
	    		float networkAccuracy = lN.getAccuracy();
	    		boolean networkAccurate = ((networkAccuracy <= (float) 50) && (networkAccuracy > 0));
	    		
	    		/* if both are accurate, choose GPS */
	    		if (gpsAccurate && networkAccurate)
	    		{
	    			if (gpsAccuracy <= networkAccuracy)
	    				return lG;
	    			else
	    				return lN;
	    		}
	    		/* else if only gps is accurate */
	    		else if (gpsAccurate && !networkAccurate)
	    			return lG;
	    		/*else if only network is accurate */
	    		else if (!gpsAccurate && networkAccurate)
	    			return lN;
	    		/* neither are accurate */
	    		else 
	    			return null;
	    	}
	    		
	    	/* else if only gps is within two minutes */
	    	else if (gpsNew && !networkNew)
	    	{
	    		float gpsAccuracy = lG.getAccuracy();
	    		boolean gpsAccurate = gpsAccuracy <= (float) 50;
	    		
	    		/* if the GPS location is accurate */
	    		if (gpsAccurate)
	    			return lG;
	    		else
	    			return null;
	    	}
	    	
	    	/* else if only network is within two minues */
	    	else if (networkNew && !gpsNew)
	    	{
	    		float networkAccuracy = lN.getAccuracy();
	    		boolean networkAccurate = networkAccuracy <= (float) 50;
	    		
	    		/* if the network location is accurate */
	    		if (networkAccurate)
	    			return lN;
	    		else
	    			return null;
	    	}
	    }
	    
	    return null;
	}
	
	/**
	 * Called when a last known location is found, it will return true if the location 
	 * was obtained in the last two minutes and then if its accuracy is within 50 meters.
	 * 
	 * @param lastLocationlocation to be judged
	 * @return whether the given location meets our standards of "good enough"
	 */
	private boolean isLastKnownLocationGood (Location lastLocation)
	{
		long timeDelta = System.currentTimeMillis() - lastLocation.getTime();
	    boolean isTimely = ((timeDelta > 0) && (timeDelta < TWO_MINUTES));
		
	    /* if lastLocation is within two minutes */
	    if (isTimely)
	    {	    	
	    	float accuracy = lastLocation.getAccuracy();
	    	boolean isAccurate = ((accuracy <= (float) 50) && (accuracy > 0));

	    	/* if lastLocation is within 50 meters */ 
	    	if (isAccurate)
	    		return true;
	    	else
	    		return false;
	    }
		
		return false;
	}

	/**
	 * Called every time there is a location that we can use. It'll be stored in currentLocation, ready to be used 
	 * if the user requests to. Not only is the current location stored, but the text in the button view changes based on 
	 * where the location is obtained. For example, if the location saved is the last known GPS location then we will inform 
	 * the user by changing the text on the button to reflect that. 
	 * 
	 * @param currentLocation the currentLocation to set
	 * @param text the index of the location based on where it is obtained from
	 */
	private static void setCurrentLocation(Location currentLocation, int text) 
	{
		String[] currentLocationText = new String[] 
				{"Use your last known (GPS) location", "Use your last nown (Network) location",
				"Use your current (GPS) location", "Use your current (Network) location"};		
		CurrentLocationHandler.currentLocation = new Location (currentLocation);
		Button originCurrentView = (Button) view.findViewById(R.id.origin_current);
		originCurrentView.setText(currentLocationText[text]);
		originCurrentView.setEnabled(true);
	}

	/**
	 * @return the currentLocation
	 */
	public static Location getCurrentLocation() {
		return currentLocation;
	}

	
	/**
	 * @return the locationManager
	 */
	public static LocationManager getLocationManager() {
		return locationManager;
	}
}

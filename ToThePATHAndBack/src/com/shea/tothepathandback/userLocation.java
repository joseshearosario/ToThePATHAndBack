package com.shea.tothepathandback;

import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * <p>The userLocation class will help the application locate the user's current location.
 * We will be using the network and GPS location, based on the hardware limitations and what 
 * the user has turned on. The GPS location is the best choice if available. Two location listeners are used, 
 * one for GPS and the other for the network. GPS is found first, but if it is not possible then it will 
 * search for a network signal.</p>
 * 
 * @author shea
 */
public class userLocation {

	/**
	 * Public variables for the userLocation class
	 */
	private final Context context;
	public boolean isGPSEnabled = false;
	public boolean isNetworkEnabled = false;
	public boolean canGetLocation = true;
	private static final long MIN_DISTANCE = 10; // 10 meters
	private static final long MIN_TIME = 1000 * 60 * 1; // 1 minute
	protected LocationManager locationManager;
	
	/**
	 * <p>This location listener is unique for the GPS. The overridden method onLocationChanged 
	 * will update the currentLocation static variable in MainActivity. It will also output the 
	 * latitude and longitude into LogCat.</p>
	 * 
	 * @see com.shea.tothepathandback.MainActivity#currentLocation
	 */
	public LocationListener gpsListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d("gpsChanged", "location: " + location.getLatitude() + ", " + location.getLongitude());
			// Update currentLocation
			MainActivity.currentLocation = new LatLng (location.getLatitude(), location.getLongitude());
			Log.d("gpsChanged", "currentLocation: " + 
					MainActivity.currentLocation.latitude + ", " + MainActivity.currentLocation.longitude);
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
	 * <p>This location listener is unique for the network. The overridden method onLocationChanged 
	 * will update the currentLocation static variable in MainActivity. It will also output the 
	 * latitude and longitude into LogCat.</p>
	 * 
	 * @see com.shea.tothepathandback.MainActivity#currentLocation
	 */
	public LocationListener networkListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d("networkChanged", "location: " + location.getLatitude() + ", " + location.getLongitude());
			// Update current location
			MainActivity.currentLocation = new LatLng (location.getLatitude(), location.getLongitude());
			Log.d("networkChanged", "currentLocation: " + 
					MainActivity.currentLocation.latitude + ", " + MainActivity.currentLocation.longitude);
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
	
	/**
	 * Constructor for the userLocation. Will call getLocation() in order to create a 
	 * location manager.
	 *  
	 * @param c - The context to be used to obtain the location service system service
	 */
	public userLocation (Context c)
	{
		this.context = c;
		this.getLocation();
	}
	
	/**
	 * <p>Will try to create a location manager from the passed context. From that, we will 
	 * try and check if GPS or network is available and enabled. The user will either have to manually 
	 * input an origin, or if possible enable the network or GPS provider. If one is available and enabled 
	 * we will call the location manager to make frequent location updates, and what will occur on an 
	 * location update will be based on what we defined in the location listener corresponding to GPS. 
	 * Finally, we will get the last known location of the user while we await an updated location. 
	 * If only the network provider is enabled, then we will follow the same process as if it was GPS. 
	 * The last known location is saved in currentLocation in MainActivity.</p>  
	 */
	public void getLocation ()
	{
		try {
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isGPSEnabled && !isNetworkEnabled)
			{
				// Show dialog detailing that there is no way to get the current location
				// The user will have to input a starting location manually
			}
			else
			{
				// We're able to get a location from some source
				this.canGetLocation = true;
				if (isGPSEnabled)
				{
					// Enable the location manager to obtain location updates utilizing our respective 
					// location listener
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, gpsListener);
					Log.d("Request Location", "GPS Enabled");
					if (locationManager != null) 
					{
						// Obtain the last known location
                        Location tempLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (tempLocation != null) 
                        {
                        	Log.d("initial gps", "tempLocation: " + tempLocation.getLatitude() + ", " + tempLocation.getLongitude());
                            // Update the current location with the last known location
                        	MainActivity.currentLocation = new LatLng(tempLocation.getLatitude(),tempLocation.getLongitude());
                            Log.d("initial gps", "currentLocation: " + MainActivity.currentLocation.latitude + ", " + MainActivity.currentLocation.longitude);
                        }
                    }
				}
					
				else if (isNetworkEnabled) 
				{
					// Enable the location manager to obtain location updates utilizing our respective 
					// location listener
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE, networkListener);
	                Log.d("Request Location", "Network Enabled");
	                if (locationManager != null) 
	                {
						// Obtain the last known location
	                	Location tempLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                   	if (tempLocation != null) 
	                   	{
	                   		Log.d("initial network", "tempLocation: " + tempLocation.getLatitude() + ", " + tempLocation.getLongitude());
                            // Update the current location with the last known location
                            MainActivity.currentLocation = new LatLng(tempLocation.getLatitude(),tempLocation.getLongitude());
                            Log.d("initial network", "currentLocation: " + MainActivity.currentLocation.latitude + ", " + MainActivity.currentLocation.longitude);
	                    }
	                }
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

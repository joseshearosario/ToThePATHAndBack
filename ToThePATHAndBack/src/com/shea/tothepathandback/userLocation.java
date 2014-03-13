package com.shea.tothepathandback;

import com.google.android.gms.maps.model.LatLng;
import android.location.Location;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class userLocation {
	private final Context context;
	public boolean isGPSEnabled = false;
	public boolean isNetworkEnabled = false;
	public boolean canGetLocation = true;
	private static final long MIN_DISTANCE = 10; // 10 meters
	private static final long MIN_TIME = 1000 * 60 * 1; // 1 minute
	protected LocationManager locationManager;
	public LocationListener gpsListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d("gpsChanged", "location: " + location.getLatitude() + ", " + location.getLongitude());
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

	public LocationListener networkListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d("networkChanged", "location: " + location.getLatitude() + ", " + location.getLongitude());
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
	
	public userLocation (Context c)
	{
		this.context = c;
		this.getLocation();
	}
	
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
				this.canGetLocation = true;
				if (isGPSEnabled)
				{
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, gpsListener);
					Log.d("Request Location", "GPS Enabled");
					if (locationManager != null) 
					{
                        Location tempLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (tempLocation != null) 
                        {
                        	Log.d("initial gps", "tempLocation: " + tempLocation.getLatitude() + ", " + tempLocation.getLongitude());
                            MainActivity.currentLocation = new LatLng(tempLocation.getLatitude(),tempLocation.getLongitude());
                            Log.d("initial gps", "currentLocation: " + MainActivity.currentLocation.latitude + ", " + MainActivity.currentLocation.longitude);
                        }
                    }
				}
					
				else if (isNetworkEnabled) 
				{
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,MIN_TIME,MIN_DISTANCE, networkListener);
	                Log.d("Request Location", "Network Enabled");
	                if (locationManager != null) 
	                {
	                	Location tempLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                   	if (tempLocation != null) 
	                   	{
	                   		Log.d("initial network", "tempLocation: " + tempLocation.getLatitude() + ", " + tempLocation.getLongitude());
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

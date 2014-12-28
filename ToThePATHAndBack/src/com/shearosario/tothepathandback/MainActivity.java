package com.shearosario.tothepathandback;

import java.util.ArrayList;

import com.google.android.gms.ads.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.shearosario.tothepathandback.adapter.TabsPagerAdapter;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener
{
	private static DatabaseHandler pathStations;
	private static ArrayList<Stop> allStations;
	private static ArrayList<Trip> allTrips;
	private static String APP_KEY;
	private AdView adView;
	private final String PAYPAL_DONATE_SITE = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=48RSECA7UC5ME";
	private String[] tabs = {"Closest Station", "Map", "Schedules", "Advisories"};
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	private static String unitMeasurement;
	/*private String advertisingID;
	private boolean isLAT;*/
	
	/**
	 * Adds tabs below the action bar. 
	 */
	private void addTabs()
	{
		viewPager = (ViewPager) findViewById(R.id.pager_main);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);  
        
        // temp fix, to make sure that PATHAlerts isn't refreshing all the time
        viewPager.setOffscreenPageLimit(4);
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
        	
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
 
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
 
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
	}
		
	/**
	 * Adds a donate button to the action bar, and when pressed opens the donate web page.
	 * @param mI the menu item pressed
	 */
	public void donate (MenuItem mI)
	{
		Intent paypal = new Intent(Intent.ACTION_VIEW, Uri.parse(PAYPAL_DONATE_SITE));
		startActivity(paypal);
	}
	
	/**
	 * Changes unit of measurement between kilometers and miles
	 * @param mI the menu item pressed
	 */
	public void setMeasurement (MenuItem mI)
	{
		final SharedPreferences myPreferences = getApplicationContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
		String uM = myPreferences.getString("unitMeasurement", "");
				
		if (uM.equalsIgnoreCase("m") || uM.isEmpty())
		{			
			myPreferences.edit().putString("unitMeasurement", "k").commit(); // Changed to the metric system
			unitMeasurement = myPreferences.getString("unitMeasurement", "");
			mI.setTitle("Change to Miles");
		}
		else if (uM.equalsIgnoreCase("k"))
		{
			myPreferences.edit().putString("unitMeasurement", "m").commit(); // Changed to imperial units
			unitMeasurement = myPreferences.getString("unitMeasurement", "");
			mI.setTitle("Change to Kilometers");
		}
	}
	
	/**
	 * Checks to see if Google Play Services is installed on the phone, which is required.
	 */
	private void checkGooglePlayServices()
	{
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if (status != ConnectionResult.SUCCESS) 
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(status))
			{
				Dialog googlePlayError = GooglePlayServicesUtil.getErrorDialog(status, this, 0);
				googlePlayError.setCancelable(false);
				googlePlayError.setCanceledOnTouchOutside(false);
				googlePlayError.show();
		    } 
			else 
			{
				Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG).show();
				finish();
		    }
		}
	}
	
	/**
	 * Try and obtain the app key from Assets folder in order to use Open Mapquest APIs.
	 * If string from api_keys.xml cannot be found, an AlerDialog will force the user to close the application.
	 */
	private void getAppKey ()
	{
		if (APP_KEY == null) {
			try {
				APP_KEY = getResources().getString(R.string.mapquest_key);
			} catch (NotFoundException e) {
				e.printStackTrace();
				new AlertDialog.Builder(this)
						.setTitle("No App Key Found")
						.setMessage("No app key was found.")
						.setPositiveButton("Exit application",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										finish();
									}
								}).setCancelable(false).show();
			}
		}
	}
	
	/**
	 * All stations in the database are stored, and for 
	 * each of those stations their list of entrances are 
	 * also saved. 
	 */
	private void getAllStopsAndTrips()
	{
		if (allStations == null) {
			allStations = pathStations.getAllStations();
		}
		if (allTrips == null) {
			// allTrips = pathStations.getAllTrips();
		}
	}
	
	/**
	 * Gets the Advertising ID
	 * - Never used because this app does not use personalized ads or stores a personally-identifying number -  
	 */
	/*public void getIdThread()
	{
		Info adInfo = null;
		  
		try {
			adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GooglePlayServicesRepairableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GooglePlayServicesNotAvailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		advertisingID = adInfo.getId();
		isLAT = adInfo.isLimitAdTrackingEnabled();
	}*/
	
	@Override
	protected void onResume()
	{
		checkGooglePlayServices();
		
		if (adView != null) 
			adView.resume();
		
    	super.onResume();
	}
	
	@Override
	public void onPause() 
	{
	    if (adView != null)
	      adView.pause();
	    super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() 
	{
		// Destroy the AdView.
	    if (adView != null)
	      adView.destroy();
	    super.onDestroy();
	}
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
						
		SharedPreferences myPreferences = getApplicationContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE);
		
		boolean firstrun = myPreferences.getBoolean("firstrun", true);
	    if (firstrun)
	    {
	    	new AlertDialog.Builder(this).setTitle("Warning").setMessage("This app and its developer(s) are not affliated or endorsed by " +
	    			"the Port Authority of New York and New Jersey.\n\nThis application obtains your current location " +
	    			"and can use it to obtain your three closest stations, then directions to the station you select. To The PATH And Back does not " +
	    			"use your location for any other purpose. In addition, neither the location obtained by your device or location " +
	    			"you manually entered are stored anywhere by this application.\n\nThe directions provided by this application is obtained from " +
	    			"OpenStreetMap via Open MapQuest. The developer(s) of this app, MapQuest and OpenStreetMap, " +
	    			"cannot guarentee that the directions given to you are 100% accurate. Use them at your own risk.\n\nFinally, "
	    			+ "please be aware that the developer(s) cannot guarantee that the station and/or entrance that you want " +
	    			"directions for will be open or accurately placed. Again, use the directions and locations provided by this app at your own risk.\n\nAds "
	    			+ "provided by Google's AdMob/AdSense will be displayed under the tabs in the action bar as well " +
	    			"as in the bottom of the screen where you'll select the station you want directions to.").setNeutralButton("OK", null).show();

	    	myPreferences.edit().putBoolean("firstrun", false).commit();
	    	myPreferences.edit().putBoolean("imperialUnits", true).commit();
	    }
	    
	    unitMeasurement = myPreferences.getString("unitMeasurement", "");
	    if (unitMeasurement.isEmpty())
	    {
	    	myPreferences.edit().putString("unitMeasurement", "m").commit(); // Imperial units is the default unit of measurement
	    	unitMeasurement = myPreferences.getString("unitMeasurement", "");
	    }
	    	    		
	    AppRater.app_launched(this);    
	    
		pathStations = new DatabaseHandler(this);
		getAllStopsAndTrips();
		getAppKey();
		addTabs();
		
		adView = (AdView) this.findViewById(R.id.adViewMain);
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("949F5429A9EC251C1DD4395558D33531").build();
		// AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		if (unitMeasurement.equalsIgnoreCase("m") || unitMeasurement.isEmpty())
	    {
	    	menu.findItem(R.id.action_measurement).setTitle("Change to Kilometers");
	    }
	    else if (unitMeasurement.equalsIgnoreCase("k"))
	    {
	    	menu.findItem(R.id.action_measurement).setTitle("Change to Miles");
	    }
		
		return true;
	}

	/**
	 * @return the allStations
	 */
	public static ArrayList<Stop> getAllStations() {
		return allStations;
	}

	/**
	 * @return the APP_KEY
	 */
	public static String getAPP_KEY() {
		return APP_KEY;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the pathStations
	 */
	public static DatabaseHandler getPathStations() {
		return pathStations;
	}

	public static String getUnitMeasurement() {
		return unitMeasurement;
	}
}

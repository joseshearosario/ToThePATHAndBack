package com.shearosario.tothepathandback;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import com.shearosario.tothepathandback.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class MainActivity extends Activity
{
	private DatabaseHandler pathStations;
	
	private static String transportMode;
	private static String matrixMode;
	private static String distance_duration;
	private static String handicapAccess;
	private static ArrayList<Station> allStations;
	private static String APP_KEY;
	
	/**
	 * Try and obtain the app key from Assets folder in order to use Open Mapquest APIs.
	 * If file cannot be open, or the app key cannot be read, an AlerDialog will force the user to close the application.
	 */
	private void getAppKey ()
	{
		try {
			InputStream input = this.getAssets().open("Open_Mapquest_AppKey.json");
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();
			String tempJSON = new String(buffer);
			JSONObject jObject = new JSONObject(tempJSON);	
			APP_KEY = new String(jObject.getString("app_key"));
		} catch (IOException e) {
			e.printStackTrace();
			new AlertDialog.Builder(this)
				.setTitle("No App Key Found")
				.setMessage("An app key is necessary for this application to query any map service. No " +
						"app key was found. Please sign up for one with Mapquest, then hard-code your app key.")
				.setPositiveButton("Exit application", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int id)
					{
						finish();
					}
				})
				.show();
		} catch (JSONException e) {
			e.printStackTrace();
			new AlertDialog.Builder(this)
			.setTitle("App Key Cannot be Read")
			.setMessage("An app key is necessary for this application to query any map service. No " +
					"app key could be read from the file. Please sign up for one with Mapquest, then hard-code your app key.")
			.setPositiveButton("Exit application", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int id)
				{
					finish();
				}
			})
			.show();
		}
	}
	
	/**
	 * When the location button is pressed, the text of button changes to show
	 * the user that we'll be using their current location. Finally,
	 * createClosestStationsIntent() is called.
	 * 
	 * @param view
	 */
	public void setOriginCurrent (View view)
	{			
		((Button) view).setText("Using your current location...");
			
		/*
		 * A new intent is created when called.
		 */
		CurrentLocationHandler.createClosestStationsIntent();
	}
	
	/**
	 * When the text field is pressed, a ManualLocationText object is created. All
	 * processes dealing with the inputted text is handled there.
	 * 
	 * @param view
	 */
	public void setOriginText(View view)
	{		
		/*
		 * ManualLocationText will only instantiate if the user has selected a mode of 
		 * transport and the way they'll like to sort the stations.
		 */
		new ManualLocationText (this, this);
	}
	
	/**
	 * Based on what radio button is selected the option will either set 
	 * whether to use distance or duration to sort location, or will set 
	 * what mode of transportation the user is going to use. After this, 
	 * based on other circumstances, either the text field or current location 
	 * button will activate.
	 * 
	 * @param view
	 */
	public void onRadioButtonClicked (View view)
	{
		// Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	    	case R.id.radio_Escalator:
	    		if (checked)
	    		{
	    			handicapAccess = "escalator";
	    		}
	    		break;
	    	case R.id.radio_Elevator:
	    		if (checked)
	    		{
	    			handicapAccess = "elevator";
	    		}
	    		break;
	        case R.id.radio_Distance:
	            if (checked)
	            {
	            	distance_duration = "distance";
	            }
	            break;
	        case R.id.radio_Duration:
	            if (checked)
	            {
	            	distance_duration = "time";
	            }
	            break;
	        case R.id.radio_Quickest:
	            if (checked)
	            {
	            	transportMode = "fastest";
	            	matrixMode = "fastest";
	            }
	            break;
	        case R.id.radio_Shortest:
	            if (checked)
	            {
	            	transportMode = "shortest";
	            	matrixMode = "shortest";
	            }
	            break;
	        case R.id.radio_Walking:
	        	if (checked)
	        	{
	        		transportMode = "pedestrian";
	        		matrixMode = "pedestrian";
	        	}
	        	break;
	        case R.id.radio_PublicTransit:
	        	if (checked)
	        	{
	        		transportMode = "multimodal";
	        		matrixMode = "pedestrian";
	        	}
	        	break;
	        case R.id.radio_Bicycle:
	        	if (checked)
	        	{
	        		transportMode = "bicycle";
	        		matrixMode = "pedestrian";
	        	}
	        	break;
	    }
	}
	
	/**
	 * All stations in the database are stored, and for 
	 * each of those stations their list of entrances are 
	 * also saved. 
	 */
	private void getAllStationsAndEntrances()
	{
		allStations = pathStations.getAllStations();
		for (int i = 0; i < allStations.size(); i++)
			allStations.get(i).setEntranceList(pathStations);
	}
	
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		/*
		 * default options
		 */
		transportMode = "fastest";
    	matrixMode = "fastest";
    	distance_duration = "distance";
    	handicapAccess = null;
		
		getAppKey();
		pathStations = new DatabaseHandler(this);
		getAllStationsAndEntrances();
		
		new CurrentLocationHandler(this, this);
	}
	
	/**
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * @return the allStations
	 */
	public static ArrayList<Station> getAllStations() {
		return allStations;
	}

	/**
	 * @return the transportMode
	 */
	public static String getTransportMode() {
		return transportMode;
	}

	/**
	 * @return the matrixMode
	 */
	public static String getMatrixMode() {
		return matrixMode;
	}

	/**
	 * @return the distance_duration
	 */
	public static String getDistance_duration() {
		return distance_duration;
	}

	/**
	 * @return the APP_KEY
	 */
	public static String getAPP_KEY() {
		return APP_KEY;
	}

	/**
	 * @return the handicapAccess
	 */
	public static String getHandicapAccess() {
		return handicapAccess;
	}
}

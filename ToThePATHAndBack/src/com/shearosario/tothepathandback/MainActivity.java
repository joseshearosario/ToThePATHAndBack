package com.shearosario.tothepathandback;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.shearosario.tothepathandback.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class MainActivity extends Activity
{
	public static Station[] allStations;
	public static DatabaseHandler pathStations;
	public static String transportMode;
	public static String matrixMode;
	public static String distance_duration;
	public boolean clickedTransport = false;
	public boolean clickedSort = false;
	public static String APP_KEY;
	
	/**
	 * Try and obtain the app key from Assets folder in order to use Open Mapquest APIs.
	 * If file cannot be open, or the app key cannot be read, the application will end.
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
	
	public void setOriginCurrent (View view)
	{
		if (clickedSort && clickedTransport) 
		{
			Button originCurrentView = (Button) findViewById(R.id.origin_current);
			EditText originManualView = (EditText) findViewById(R.id.origin_manual);
			originManualView.setEnabled(false);
			originManualView.setFocusable(false);
			originCurrentView.setText("Getting your current location...");
			new currentLocation(this, this);
		}
	}
	
	public void setOriginManual(View view)
	{		
		if (clickedTransport && clickedSort)
		{	
			new manualLocation (this, this);
		}
	}
	
	public void onTransportButtonClicked (View view)
	{
		// Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_Quickest:
	            if (checked)
	            {
	            	transportMode = "fastest";
	            	matrixMode = "fastest";
	            	clickedTransport = true;
	            }
	            break;
	        case R.id.radio_Shortest:
	            if (checked)
	            {
	            	transportMode = "shortest";
	            	matrixMode = "shortest";
	            	clickedTransport = true;
	            }
	            break;
	        case R.id.radio_Walking:
	        	if (checked)
	        	{
	        		transportMode = "pedestrian";
	        		matrixMode = "pedestrian";
	        		clickedTransport = true;
	        	}
	        	break;
	        case R.id.radio_PublicTransit:
	        	if (checked)
	        	{
	        		transportMode = "multimodal";
	        		matrixMode = "pedestrian";
	        		clickedTransport = true;
	        	}
	        	break;
	        case R.id.radio_Bicycle:
	        	if (checked)
	        	{
	        		transportMode = "bicycle";
	        		matrixMode = "pedestrian";
	        		clickedTransport = true;
	        	}
	        	break;
	    }
	}
	
	public void onDDButtonClicked (View view)
	{
		// Is the button now checked?
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    // Check which radio button was clicked
	    switch(view.getId()) {
	        case R.id.radio_Distance:
	            if (checked)
	            {
	            	distance_duration = "distance";
	            	clickedSort = true;
	            }
	            break;
	        case R.id.radio_Duration:
	            if (checked)
	            {
	            	distance_duration = "time";
	            	clickedSort = true;
	            }
	            break;
	    }
	}
	
	private void createPATHDatabase()
	{
		pathStations = new DatabaseHandler(this);
		try {
			pathStations.createDatabase();
		} catch (IOException e) {
			Log.d("Database", "Database: Not created/opened");
			e.printStackTrace();
		}
	}
	
	private void getAllStationsAndEntrances()
	{
		allStations = pathStations.getAllStations();
		for (int i = 0; i < allStations.length; i++)
			allStations[i].setEntranceList(pathStations);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getAppKey();
		createPATHDatabase();
		getAllStationsAndEntrances();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}

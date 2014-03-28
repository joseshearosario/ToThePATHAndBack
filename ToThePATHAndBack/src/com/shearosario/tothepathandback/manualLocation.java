package com.shearosario.tothepathandback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * @author shea
 *
 */
public class manualLocation
{
	public Context context;
	public String manualLocation;
	public Activity activity;
	public EditText etManualView;	
	
	public manualLocation (Context c, Activity a)
	{
		context = c;
		activity = a; 
		
		etManualView = (EditText) activity.findViewById(R.id.origin_manual);
		etManualView.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				if (actionId == EditorInfo.IME_ACTION_SEARCH)
				{					
					InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
					manualLocation = etManualView.getText().toString();
					try {
						createClosestStationsIntent();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return true;
				}
				return false;
			}
		});
	}
	
	public static class downloadGeocode extends AsyncTask<String, Void, String> 
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
	
	public double[] geocode () throws UnsupportedEncodingException
	{
		/* 
		 * They're are no limits to using Nominatim, but if an approximate geolocation
		 * cannot be found, then we might need to use Mapquest or Google.
		 */		
		double[] latlng = new double[2];
		String tempJSON = null;
		String tempManualLocation = URLEncoder.encode(manualLocation, "UTF-8");
		String geocodeURL = "http://open.mapquestapi.com/nominatim/v1/search.php?format=json&q="+tempManualLocation;

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
			for (int i = 0; i < jArray.length(); i++)
			{
				// get value from array element casted as a JSONObject
				latlng[0] = ((JSONObject) jArray.get(i)).getDouble("lat");
				latlng[1] = ((JSONObject) jArray.get(i)).getDouble("lon");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return latlng;
	}
	
	public void createClosestStationsIntent() throws UnsupportedEncodingException
	{
		Intent intent = new Intent (context, ClosestStationsActivity.class);		
		intent.putExtra("Manual", geocode());
		activity.startActivity(intent);
	}
}

/**
 * 
 */
package com.shearosario.tothepathandback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author shea
 *
 */
public class DisplayDirectionsIntent
{	
	public DisplayDirectionsIntent(final Context c, final Activity a, LatLng o, final Entrance s) 
	{
		final LatLng origin = o;
		final LatLng destination = new LatLng(s.getEntranceLocation()[0], s.getEntranceLocation()[1]);
					
		final ProgressDialog myDialog = ProgressDialog.show(c, null, "Fetching directions...", true);

		new Thread (new Runnable()
		{
			@Override
			public void run() 
			{
				Intent intent = new Intent (c, DisplayDirectionsActivity.class);
				// intent.putExtra("origin", new double[] {origin.latitude, origin.longitude});
				// intent.putExtra("destination", new double[] {destination.latitude, destination.longitude});
				
				String url = Directions.getGuidanceURL(origin, destination);
				String json_url = Directions.downloadFromURL(url);
								
				JSONArray jGuidanceLinkCollection = null;
				JSONArray jGuidanceNodeCollection = null;
				JSONArray jShapePoints = null;
				
				try {
					JSONObject jObject = new JSONObject (json_url);
					JSONObject jGuidance = jObject.getJSONObject("guidance");
					
					jGuidanceLinkCollection = jGuidance.getJSONArray("GuidanceLinkCollection");
					jGuidanceNodeCollection = jGuidance.getJSONArray("GuidanceNodeCollection");
					jShapePoints = jGuidance.getJSONArray("shapePoints");
					
				} catch (JSONException e) {
					e.printStackTrace();
					
					myDialog.dismiss();		

					a.runOnUiThread(new Runnable()
					{
						@Override
						public void run() {
							new AlertDialog.Builder(c)
							// .setTitle("Cannot get directions to " + s.getStationName())
							.setTitle("Cannot Get Directions")
							.setMessage("Feel free to try again or to use another station.")
							.setPositiveButton("Okay", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int id)
								{
									dialog.cancel();
								}
							}).setCancelable(false).show();							
						}
						
					});
					
					return;
				}
				
				if (jGuidanceLinkCollection == null || jGuidanceNodeCollection == null || jShapePoints == null)
				{
					myDialog.dismiss();		

					a.runOnUiThread(new Runnable()
					{
						@Override
						public void run() {
							new AlertDialog.Builder(c)
							// .setTitle("Cannot get directions to " + s.getStationName())
							.setTitle("Cannot Get Directions")
							.setMessage("Feel free to try again or to use another station.")
							.setPositiveButton("Okay", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int id)
								{
									dialog.cancel();
								}
							}).setCancelable(false).show();							
						}
						
					});
					
					return;					
				}
				
				intent.putExtra("GuidanceLinkCollection", jGuidanceLinkCollection.toString());
				intent.putExtra("GuidanceNodeCollection", jGuidanceNodeCollection.toString());
				intent.putExtra("shapePoints", jShapePoints.toString());
				
				myDialog.dismiss();		
				a.startActivity(intent);
			}
		}).start();				
	}
}

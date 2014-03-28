/**
 * 
 */
package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Parses any JSON data in order to obtain information so that a map can be drawn, or 
 * to determine the nearest or quickest stations based on the user's location. 
 * 
 * @author shea
 */
public class DirectionsJSONParser {
	
	/**
	 * Passing an array of Strings holding the JSON data from the given origin to all stations, we parse 
	 * through and obtain the distance from origin to destination in meters.
	 * 
	 * @param JSONString - Array of Strings holding JSON data
	 * @return an array of integers in order of stations array representing their distance (m) from given origin
	 */
	public static double[] getDistanceOrDuration (String[] JSONString)
	{
		JSONArray jMeasure = null;
		JSONObject jObject = null;
		double[] sortMeasures = new double[JSONString.length];
		
		try
		{
			for (int i = 0; i < JSONString.length; i++)
			{
				jObject = new JSONObject(JSONString[i]);
				jMeasure = jObject.getJSONArray(MainActivity.distance_duration);
				sortMeasures[i] = jMeasure.getDouble(1);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sortMeasures;
	}
	
	/** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject)
    {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
 
        try
        { 
            jRoutes = jObject.getJSONArray("routes");
 
            /** Traversing all routes */
            for(int i=0; i<jRoutes.length(); i++)
            {
                jLegs = ((JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();
 
                /** Traversing all legs */
                for(int j=0; j<jLegs.length(); j++)
                {
                	// In each leg it is possible to get the duration and distance of the trip between points
                	jSteps = ((JSONObject)jLegs.get(j)).getJSONArray("steps");
 
                    /** Traversing all steps */
                    for(int k=0; k<jSteps.length(); k++)
                    {
                    	// Add ways to get transit details from each step element
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);
 
                        /** Traversing all points */
                        for(int l=0;l<list.size();l++)
                        {
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
        } 
        catch (JSONException e) 
        {
        	e.printStackTrace();
        }
        catch (Exception e)
        {
   
        }
    return routes;
    }

	private List<LatLng> decodePoly(String polyline) {
		// TODO Auto-generated method stub
		return null;
	}
}

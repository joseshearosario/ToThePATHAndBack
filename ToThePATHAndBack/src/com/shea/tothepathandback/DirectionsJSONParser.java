/**
 * 
 */
package com.shea.tothepathandback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

/**
 * @author shea
 *
 */
public class DirectionsJSONParser {
	
	public int[] getDistance (String[] JSONString)
	{
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONObject jObject = null;
		int[] distance = new int[JSONString.length];
		
		try
		{
			for (int i = 0; i < JSONString.length; i++)
			{
				jObject = new JSONObject(JSONString[i]);
				jRoutes = jObject.getJSONArray("routes");
				for (int j = 0; j < jRoutes.length(); j++)
				{
					jLegs = ((JSONObject)jRoutes.get(j)).getJSONArray("legs");
					for (int k = 0; k < jLegs.length(); k++)
					{
						JSONObject json_duration = ((JSONObject)jLegs.get(j)).getJSONObject("distance");
						distance[i] = json_duration.getInt("value");
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return distance;
	}
	
	public int[] getDuration (String[] JSONString)
	{
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONObject jObject = null;
		int[] duration = new int[JSONString.length];
				
		try
		{
			for (int i = 0; i < JSONString.length; i++)
			{
				jObject = new JSONObject(JSONString[i]);
				jRoutes = jObject.getJSONArray("routes");
				for (int j = 0; j < jRoutes.length(); j++)
				{
					jLegs = ((JSONObject)jRoutes.get(j)).getJSONArray("legs");
					for (int k = 0; k < jLegs.length(); k++)
					{
						JSONObject json_duration = ((JSONObject)jLegs.get(j)).getJSONObject("duration");
						duration[i] = json_duration.getInt("value");
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return duration;
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

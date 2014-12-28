/**
 * 
 */
package com.shearosario.tothepathandback;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author shea
 *
 */
public class ClosestStationFragment extends Fragment {
	
	private View rootView;
	private static String transportMode;
	private static String matrixMode;
	private static String distance_duration;
	private static String handicapAccess;
	private static Stop selectedStation;
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
				
		/*
		 * default values
		 */
		transportMode = "fastest";
    	matrixMode = "fastest";
    	distance_duration = "distance";
    	handicapAccess = null;
    	selectedStation = null;
 
        rootView = inflater.inflate(R.layout.fragment_closest_station, container, false);
       
        new CurrentLocationHandler(rootView.getContext(), getActivity(), rootView);
        Button currentLocationButton = (Button) rootView.findViewById(R.id.origin_current);
        currentLocationButton.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				/*
				 * To check if the phone is currently using a network connection. Listens to broadcasts when the the device is or is not connected to 
				 * a network
				 */
				ConnectivityManager cm = (ConnectivityManager) (rootView.getContext()).getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
				boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
				if (!isConnected)
				{
					Toast.makeText(rootView.getContext(), "No network connection", Toast.LENGTH_SHORT).show();
					return;
				}
				
				 /* A new intent is created when called. */
				CurrentLocationHandler.createClosestStationsIntent();
			}
		});
               
        EditText manualLocationText = (EditText) rootView.findViewById(R.id.origin_manual);
        manualLocationText.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new ManualLocationText (rootView.getContext(), getActivity());				
			}
		});
        
        setSpinners(rootView);
        
        return rootView;
    }
	
	/**
	 * Sets the adapter for each spinner, as well as create listeners for each.
	 * 
	 * @param view the current fragment view
	 */
	private void setSpinners(View view)
	{
		Spinner spinner_Transport = (Spinner) view.findViewById(R.id.spinner_TransportMode);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.transport_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_Transport.setAdapter(adapter);
		
		Spinner spinner_Sort = (Spinner) view.findViewById(R.id.spinner_DistanceDuration);
		adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.sort_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_Sort.setAdapter(adapter);
		
		Spinner spinner_Handicap = (Spinner) view.findViewById(R.id.spinner_HandicapAccess);
		adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.handicap_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_Handicap.setAdapter(adapter);		
		
		/*
		 * See if it is possible to add the stations here, based on the order currently in MainActivity.getAllStations()
		 */		
		String[] spinnerArray = new String[MainActivity.getAllStations().size() + 1];
		spinnerArray[0] = "No, Not Yet";
		for (int y = 0, z = 1; y < MainActivity.getAllStations().size() && z < spinnerArray.length; z++, y++)
		{
			spinnerArray[z] = MainActivity.getAllStations().get(y).getStopName();
		}
		
		Spinner spinner_Stations = (Spinner) view.findViewById(R.id.spinner_Stations);
		// adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.stations_array, android.R.layout.simple_spinner_item);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// spinner_Stations.setAdapter(adapter);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, spinnerArray); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_Stations.setAdapter(spinnerArrayAdapter);
				
		spinner_Handicap.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position) {
					case 0:
						handicapAccess = null;
						break;
					case 1:
						handicapAccess = "escalator";
						break;
					case 2:
						handicapAccess = "elevator";
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		spinner_Sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position) {
	    	    	case 0:
	    	    		distance_duration = "distance";
	    	    		break;
	    	    	case 1:
	    	    		distance_duration = "time";
	    	    		break;			
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		spinner_Transport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position) {
	        		case 0:
	        			transportMode = "fastest";
	        			matrixMode = "fastest";
	        			break;
	        		case 1:
	        			transportMode = "shortest";
	        			matrixMode = "shortest";
	        			break;
	        		case 2:
	        			transportMode = "pedestrian";
	        			matrixMode = "pedestrian";
	        			break;
	        		case 3:
	        			transportMode = "multimodal";
	        			matrixMode = "pedestrian";
	        			break;
	        		case 4:
	        			transportMode = "bicycle";
	        			matrixMode = "pedestrian";
	        			break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		spinner_Stations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (position == 0)
				{
					selectedStation = null;
				}
				else if (position > 0)
				{
					selectedStation = new Stop(MainActivity.getAllStations().get(position - 1));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				// TODO Auto-generated method stub
				
			}
		});
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
	 * @return the handicapAccess
	 */
	public static String getHandicapAccess() {
		return handicapAccess;
	}

	
	/**
	 * @return the selectedStation
	 */
	public static Stop getSelectedStation() {
		return selectedStation;
	}
}

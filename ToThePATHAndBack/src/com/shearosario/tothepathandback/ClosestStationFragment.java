/**
 * 
 */
package com.shearosario.tothepathandback;

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

/**
 * @author shea
 *
 */
public class ClosestStationFragment extends Fragment {
	
	private static View rootView;
	private static String transportMode;
	private static String matrixMode;
	private static String distance_duration;
	private static String handicapAccess;
	
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
 
        rootView = inflater.inflate(R.layout.fragment_closest_station, container, false);
        
        new CurrentLocationHandler(rootView.getContext(), getActivity(), rootView);
        Button currentLocationButton = (Button) rootView.findViewById(R.id.origin_current);
        currentLocationButton.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				((Button) v).setText("Using your current location...");
				
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
}

/**
 * 
 */
package com.shearosario.tothepathandback;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Jose Andres Rosario
 *
 */
public class SystemSchedulesFragment extends Fragment {
	private View rootView;
	private static Stop departingStation;
	private static Stop arrivingStation;
	private static String schedule;
	private ArrayList<String> scheduledStops;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_system_schedules,
				container, false);
		
		Button schedulesButton = (Button) rootView.findViewById(R.id.start_SystemSchedulesActivity);
		
        schedulesButton.setOnClickListener(new View.OnClickListener() 
        {
			@Override
			public void onClick(View v) 
			{
				if (arrivingStation != null)
				{
					if (departingStation.getStopID() == arrivingStation.getStopID())
						Toast.makeText(v.getContext(), "The departing and arriving stations are the same", Toast.LENGTH_SHORT).show();
					else
					{
						Toast.makeText(v.getContext(), "Coming soon!", Toast.LENGTH_SHORT).show();
						scheduledStops = new ArrayList<String>(MainActivity.getPathStations().getSchedule(departingStation, arrivingStation, schedule));
					}
				}
				else
				{
					scheduledStops = new ArrayList<String>(MainActivity.getPathStations().getSchedule(departingStation, schedule));
					
					Intent intent = new Intent (rootView.getContext(), SystemSchedulesActivity.class);
					
					intent.putExtra("i", scheduledStops.size());					
					
					for (int i = 0; i < scheduledStops.size(); i++)
			        {
			        	String[] stop_split = scheduledStops.get(i).split(";");
			        	String extra_name = "split_" + Integer.toString(i);
			        	intent.putExtra(extra_name, stop_split);
			        }
					
					startActivity(intent);
				}
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
		/*
		 * Add all departing stations into spinner array
		 */		
		final ArrayList<Stop> allStations = new ArrayList<Stop> (MainActivity.getAllStations());
		
		/*
		 * default values for schedule options
		 */
		departingStation = new Stop(allStations.get(0));
		arrivingStation = null;
		schedule = "mon";
		
		String[] spinnerArray_DepartingStations = new String[allStations.size()];
		
		for (int y = 0; y < allStations.size(); y++)
			spinnerArray_DepartingStations[y] = allStations.get(y).getStopName();
		
		Spinner spinner_DepartingStations = (Spinner) view.findViewById(R.id.spinner_scheduleDepartingStations);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, spinnerArray_DepartingStations); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_DepartingStations.setAdapter(spinnerArrayAdapter);
				
		spinner_DepartingStations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				departingStation = new Stop(allStations.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				
			}
		});		
		
		/*
		 * Add all arriving stations
		 */
		/*String[] spinnerArray_ArrivingStations = new String[allStations.size() + 1];
		spinnerArray_ArrivingStations[0] = "None";
		for (int y = 1, z = 0; y < spinnerArray_ArrivingStations.length && z < allStations.size(); y++, z++)
		{
			spinnerArray_ArrivingStations[y] = allStations.get(z).getStopName();
		}
		
		Spinner spinner_ArrivingStations = (Spinner) view.findViewById(R.id.spinner_scheduleArrivingStations);
		spinnerArrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, spinnerArray_ArrivingStations); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_ArrivingStations.setAdapter(spinnerArrayAdapter);
		
		spinner_ArrivingStations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				if (position == 0)
				{
					arrivingStation = null;
				}
				else if (position > 0)
				{
					arrivingStation = new Stop(allStations.get(position - 1));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
				
			}
		});	*/	
		
		
		/*
		 * Add schedules 
		 */
		final ArrayList<String> calendar_dates = MainActivity.getPathStations().getDistinctCalendarDates();
		
		Spinner spinner_Dates = (Spinner) view.findViewById(R.id.spinner_scheduleDates);
		spinnerArrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, calendar_dates); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_Dates.setAdapter(spinnerArrayAdapter);
		
		spinner_Dates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch(position) {
					case 0:
						schedule = "mon";
						break;
					case 1:
						schedule = "tue";
						break;
					case 2:
						schedule = "wed";
						break;
					case 3:
						schedule = "thu";
						break;
					case 4:
						schedule = "fri";
						break;
					case 5:
						schedule = "sat";
						break;
					case 6:
						schedule = "sun";
						break;
					default:
						String temp_calendar_date = calendar_dates.get(position);
						if (temp_calendar_date.contains("Holiday schedule for"))
						{
							String temp_date = temp_calendar_date.substring(21);
							String[] temp_date_split = temp_date.split("/");
							schedule = temp_date_split[2] + temp_date_split[0] + temp_date_split[1];
						}
						else
						{
							String temp_date = temp_calendar_date.substring(1, 11);
							String[] temp_date_split = temp_date.split("/");
							schedule = temp_date_split[2] + temp_date_split[0] + temp_date_split[1];
						}
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
						
			}
		});
	}
}

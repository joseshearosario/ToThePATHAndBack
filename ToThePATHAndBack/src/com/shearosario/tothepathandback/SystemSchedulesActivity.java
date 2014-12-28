package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author Jose Andres Rosario
 *
 */
public class SystemSchedulesActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_schedules);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		int size_stops = 0;
		
		ArrayList<String> allDepartureTimes = new ArrayList<String>();
		ArrayList<String> allHeadsigns = new ArrayList<String>();
		
		if(intent.hasExtra("i"))
		{
			size_stops = intent.getIntExtra("i", 0);
		}
		
		for (int i = 0; i < size_stops; i++)
		{
			String extra_name = "split_" + Integer.toString(i);
						
			if(intent.hasExtra(extra_name))
			{
				String[] stop_split = intent.getStringArrayExtra(extra_name);
				
				// make it compatible for departure schedule and departure/arrival schedule by using the size of stop_split. It'll
				// be three if departure/arrival, two otherwise. 
				
				if (!DateFormat.is24HourFormat(this))
				{
					String[] time_split = stop_split[0].split(":");
					
					int hour = Integer.parseInt(time_split[0]);
					
					if (hour < 12)
						time_split[1] = time_split[1] + " AM";
					else
						time_split[1] = time_split[1] + " PM";
					
					if (hour > 12)
						time_split[0] = Integer.toString(hour - 12);
					else if (hour == 0)
						time_split[0] = "12";
					else 
						time_split[0] = Integer.toString(hour);
					
					stop_split[0] = time_split[0] + ":" + time_split[1];
					
					if (stop_split.length == 3)
					{
						time_split = stop_split[2].split(":");
						
						hour = Integer.parseInt(time_split[0]);
						
						if (hour < 12)
							time_split[1] = time_split[1] + " AM";
						else
							time_split[1] = time_split[1] + " PM";
						
						if (hour > 12)
							time_split[0] = Integer.toString(hour - 12);
						else if (hour == 0)
							time_split[0] = "12";
						else 
							time_split[0] = Integer.toString(hour);
						
						stop_split[2] = time_split[0] + ":" + time_split[1];
					}
				}				
				
				allDepartureTimes.add(stop_split[0]);
				allHeadsigns.add(stop_split[1]);
				// allArrivalTimes
			}			
		}
		
		if (allDepartureTimes.size() == allHeadsigns.size())
		{
			ArrayList<Map<String, String>> list = convertToListItems(allDepartureTimes, allHeadsigns);
			String[] from = {"time", "headsign"};
			int[] to = {android.R.id.text1, android.R.id.text2};
			SimpleAdapter adapter = new SimpleAdapter(this, list, android.R.layout.simple_list_item_2, from, to);
			ListView listview = (ListView) findViewById(R.id.list_system_schedules); 
			listview.setAdapter(adapter);
		}
	}
	
	private ArrayList<Map<String, String>> convertToListItems (ArrayList<String> times, ArrayList<String> headsigns)
	{
		ArrayList<Map<String, String>> listItems = new ArrayList<Map<String, String>>(headsigns.size());

		if (times.size() == 0 && headsigns.size() == 0)
		{
			HashMap<String, String> listItemMap = new HashMap<String, String>();
			listItemMap.put("time", "No scheduled stops");
			listItemMap.put("headsign", "");
			listItems.add(listItemMap);
			return listItems;
		}
		
		for (int i = 0; i < times.size() && i < headsigns.size(); i++) 
		{
			HashMap<String, String> listItemMap = new HashMap<String, String>();
			listItemMap.put("time", times.get(i));
			listItemMap.put("headsign", headsigns.get(i));
			listItems.add(listItemMap);
		}

		return listItems;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    // navigate up to parent activity (MainActivity)
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	// navigate back to MainActivity
	@Override
	public void onBackPressed() {
		NavUtils.navigateUpFromSameTask(this);
	}
}

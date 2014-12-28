package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.TextView;

public class SystemMapFragment extends Fragment {

	private GoogleMap gMap;
	private View rootView;
	private ArrayList<Stop> allStations;
	private ArrayList<Marker> openEntrances;
	private LatLngBounds.Builder bounds;
	private String currentMap;
	
	/**
	 * Removes from an ArrayList of entrances those that are not open at the current moment.
	 * 
	 * @param entrances
	 * @return entrances that are open
	 */
	private ArrayList<Entrance> removeEntrancesTimeAndDay(ArrayList<Entrance> entrances)
	{
		ArrayList<Entrance> tempEntrances = new ArrayList<Entrance>(entrances);
		for (int x = 0; x < tempEntrances.size();)
		{
			Entrance tEntrance = tempEntrances.get(x);
			if (tEntrance.getEntranceNotes() == null || tEntrance.getEntranceNotes().isEmpty() == true)
				x++;
			else
			{			
				String[] notes = tEntrance.getEntranceNotes().split(" ");
				ArrayList<String> timePeriods = new ArrayList<String>();
				boolean remove = false;
				
				for (int i = 0; i < notes.length; i++)
				{
					if (notes[i].matches("(1--|2--|3--|4--|5--|6--|7--)[0-9]{8}"))
					{
						timePeriods.add(notes[i]);
					}
				}				
				
				/*
				 * if there are time periods to evaluate
				 */
				if (timePeriods.size() != 0)
				{
					remove = true;
					/*
					 * the current day (1-7), hour (0-23), and minute
					 */
					int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
					int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
					int minute = Calendar.getInstance().get(Calendar.MINUTE);
					
					/*
					 * go through each time period
					 */
					for (int i = 0; i < timePeriods.size(); i++)
					{
						String periodDay = timePeriods.get(i).substring(0, 1);
						int dayNumber = Integer.parseInt(periodDay);
						
						/*
						 * if we're evaluating a time period that cooresponds with the current day
						 */
						if (dayNumber == day)
						{
							// time period when the entrance is open
							String periodStart = timePeriods.get(i).substring(3, 7);
							String periodEnd = timePeriods.get(i).substring(7, 11);
							
							if (periodStart.equalsIgnoreCase("0000") && periodEnd.equalsIgnoreCase("0000"))
								break;
							
							int startHour = Integer.parseInt(periodStart.substring(0, 2));
							int startMinute = Integer.parseInt(periodStart.substring(2,4));
							int endHour = Integer.parseInt(periodEnd.substring(0, 2));
							int endMinute = Integer.parseInt(periodEnd.substring(2, 4));
							
							if (hour >= startHour && hour <= endHour)
							{
								if (hour == startHour)
								{
									if (minute >= startMinute)
									{
										remove = false;
										break;
									}
								}
								else if (hour == endHour)
								{
									if (minute <= endMinute)
									{
										remove = false;
										break;
									}
								}
								else
								{
									remove = false;
									break;
								}
							}
						}
						
					}
				}
											
				/*
				 * this entrance is removed if it is not open right now 
				 */
				if (remove)
				{
					tempEntrances.remove(x);
				}
				else
					x++;
			}
		}
		return tempEntrances;
	}
	
	/**
	 * Calls which map to display based on the current time, and sets the current map to a string
	 */
	private void currentMap()
	{		
		/*
		 * Eventually, this will also take into account select holidays
		 */
		Calendar c = Calendar.getInstance();
		int day = c.get(Calendar.DAY_OF_WEEK);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		if (day >= 2 && day <= 6)
		{
			if (hour >= 6 && hour <= 22)
			{
				currentMap = "weekday";
				weekdayMap();
				((Button) rootView.findViewById(R.id.button_otherMap)).setText(R.string.string_nightsweekends);
			}
			
			else
			{
				currentMap = "nights";
				nightsWeekendsMap();	
				((Button) rootView.findViewById(R.id.button_otherMap)).setText(R.string.string_weekdays);
			}
		}
		else
		{
			currentMap = "nights";
			nightsWeekendsMap();
			((Button) rootView.findViewById(R.id.button_otherMap)).setText(R.string.string_weekdays);
		}
	}
	
	/**
	 * Using a string noting the current map, this calls the map that is not the current configuration.
	 */
	private void otherMap()
	{
		/*((Button) rootView.findViewById(R.id.button_currentMap)).setEnabled(true);
		((Button) rootView.findViewById(R.id.button_otherMap)).setEnabled(false);*/
		
		if (currentMap.equalsIgnoreCase("weekday"))
			nightsWeekendsMap();
		else
			weekdayMap();
	}
	
	/**
	 * Tries to zoom and show the entire PATH system if called
	 */
	private void systemMapCamera()
	{
		// http://stackoverflow.com/questions/13692579/movecamera-with-cameraupdatefactory-newlatlngbounds-crashes
		
		try {
		       gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
		} catch (IllegalStateException e) {
			//layout not yet initialized
		    final View mapView = getFragmentManager().findFragmentById(R.id.systemMap).getView();
		    if (mapView.getViewTreeObserver().isAlive()) 
		    {
		    	mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
		        {
		    		public void onGlobalLayout()
		            {
		    			mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		                gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 50));
		            }
		        });
	    	}
		}
	}
	
	/**
	 * Activates listeners for the three buttons on screen as well as two listeners for the map
	 */
	private void listeners()
	{
		/*
		 * Button listeners
		 */
		
		Button currentMap = (Button) rootView.findViewById(R.id.button_currentMap);
		Button otherMap = (Button) rootView.findViewById(R.id.button_otherMap);
			
		currentMap.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				gMap.clear();				
				currentMap();
			}
		});
		
		otherMap.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				gMap.clear();
				otherMap();
			}
		});
		
		/*
		 * Marker click listener that'll open the info window, displaying the station name and services and any
		 * service advisories. For markers that are entrances, it'll display any necessary info about the entrance (i.e., handicap access). 
		 * Also zooms in to level 15.
		 */
		gMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker arg0) 
			{
				if (arg0.getTitle().contains("Entrance"))
				{
					return false;
				}
				else
				{
					gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arg0.getPosition(), 15));					
					return false;
				}
			}
		});
		
		/*
		 * Info window click listener that'll open an AlertDialog if there is a service advisory
		 */
		gMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener()
		{
			@Override
			public void onInfoWindowClick(Marker arg0) 
			{
				
			}
		});
	
		/*
		 * Camera change listener that will make visible all the entrances open at this time when the zoom level is at or more than 15  
		 */
		gMap.setOnCameraChangeListener(new OnCameraChangeListener()
		{
			@Override
			public void onCameraChange(CameraPosition arg0) 
			{
				for (int z = 0; z < openEntrances.size(); z++)
				{
					Marker m = openEntrances.get(z);
					m.setVisible(arg0.zoom >= 15);
				}
			}					
		});
	}
		
	/**
	 * Draws the polylines for the weekday map, enables/disables appropriate buttons, and zooms to the entire system. 
	 * Finally, calls to add the station markers and passes an integer representing what map it is.
	 */
	private void weekdayMap()
	{		
		if (currentMap.equals("weekday"))
			addStationMarkers(0, true);
		else
			addStationMarkers(0, false);
		
		double[] HBK_33 = {
				40.735886,-74.029208,
				40.735006,-74.029395,
				40.734348,-74.029546,
				40.733959,-74.029616,
				40.733755,-74.029611,
				40.733432,-74.029576,
				40.733110,-74.029494,
				40.732619,-74.029326,
				40.732512,-74.029234,
				40.732392,-74.029088,
				40.732290,-74.028861,
				40.732225,-74.028513,
				40.732017,-74.027352,
				40.731790,-74.024167,
				40.731651,-74.022283,
				40.731715,-74.018199,
				40.731854,-74.015851,
				40.732044,-74.013174,
				40.732521,-74.009464,
				40.732952,-74.007061,
				40.733125,-74.005930,
				40.733413,-74.004093,
				40.733839,-74.001204,
				40.734072,-73.999547,
				40.734183,-73.999135,
				40.734244,-73.999110,
				40.737345,-73.996844,
				40.742890,-73.992803,
				40.749102,-73.988256
		};
		
		PolylineOptions rectOptions = new PolylineOptions().color(Color.parseColor("#4d92fb")).width(15).zIndex(0);
		
		for (int i = 0; i < (HBK_33.length-1); i++)
		{
			LatLng location = new LatLng(HBK_33[i], HBK_33[++i]);
			rectOptions.add(location);
		}
		
		gMap.addPolyline(rectOptions);
		
		double[] HBK_WTC = {
				40.735873,-74.029219,
				40.732331,-74.031045,
				40.727990,-74.033300,
				40.727055,-74.033811,
				40.726620,-74.033890,
				40.725363,-74.034120,
				40.722558,-74.034630,
				40.720270,-74.035068,
				// 40.717321,-74.035586, replaced with...
				40.718723,-74.035315,
				// 40.717131,-74.034594, replaced with...
				40.718097,-74.034982,
				// added
				40.717381,-74.034349,
				40.717018,-74.033870,
				40.716806,-74.032616,
				40.716763,-74.032389,
				40.716759,-74.032358,
				40.715006,-74.023613,
				40.712850,-74.012678,
				40.712719,-74.011941
		};
		
		rectOptions = new PolylineOptions().color(Color.parseColor("#65c100")).zIndex(2);
		
		for (int i = 0; i < (HBK_WTC.length-1); i++)
		{
			LatLng location = new LatLng(HBK_WTC[i], HBK_WTC[++i]);
			rectOptions.add(location);
		}
		
		gMap.addPolyline(rectOptions);
		
		double[] JSQ_33 = {
				40.749111,-73.988276,
				40.742890,-73.992803,
				40.737345,-73.996844,
				40.734244,-73.999110,
				40.734228,-73.999114,
				40.734179,-73.999146,
				40.734070,-73.999537,
				40.733839,-74.001189,
				40.733596,-74.002847,
				40.732950,-74.007070,
				40.732870,-74.007520,
				40.732440,-74.010037,
				40.732042,-74.013119,
				40.731706,-74.017895,
				40.731657,-74.022248,
				40.732014,-74.027340,
				40.731953,-74.028839,
				40.731861,-74.029253,
				40.731646,-74.029719,
				40.731202,-74.030385,
				40.729743,-74.031848,
				40.728463,-74.033009,
				40.727093,-74.033788,
				40.726620,-74.033890,
				40.727000,-74.033821,
				40.724932,-74.034214,
				40.720369,-74.035033,
				// 40.717318,-74.035609, replaced with...
				40.718723,-74.035637,
				// 40.717533,-74.036509, replaced with...
				40.718146,-74.036173,
				40.717749,-74.037274,
				40.719187,-74.041963,
				40.719314,-74.042392,
				40.719528,-74.043082,
				40.720585,-74.046403,
				40.722060,-74.050976,
				40.722733,-74.053320,
				40.723020,-74.053867,
				40.723671,-74.054726,
				40.724281,-74.055468,
				40.725069,-74.055990,
				40.725767,-74.056432,
				40.726035,-74.056480,
				40.726440,-74.056594,
				40.727055,-74.056978,
				40.728096,-74.058140,
				40.728887,-74.059040,
				40.729627,-74.059861,
				40.730053,-74.060339,
				40.730835,-74.061241,
				40.731173,-74.061650,
				40.731963,-74.062589,
				40.732331,-74.063049
		};
		
		rectOptions = new PolylineOptions().color(Color.parseColor("#FF9900")).zIndex(1);
		
		for (int i = 0; i < (JSQ_33.length-1); i++)
		{
			LatLng location = new LatLng(JSQ_33[i], JSQ_33[++i]);
			rectOptions.add(location);
		}
		
		gMap.addPolyline(rectOptions);
		
		double[] NWK_WTC = {
				40.712713,-74.011963,
				40.713561,-74.016213,
				40.714564,-74.021281,
				40.715442,-74.025719,
				40.716761,-74.032375,
				40.716918,-74.032988,
				40.717170,-74.034820,
				40.717360,-74.035920,
				40.717713,-74.037241,
				40.719160,-74.041876,
				40.719314,-74.042392,
				40.719528,-74.043082,
				40.720585,-74.046403,
				40.722060,-74.050976,
				40.722733,-74.053320,
				40.723020,-74.053867,
				40.723671,-74.054726,
				40.724281,-74.055468,
				40.725069,-74.055990,
				40.725767,-74.056432,
				40.726035,-74.056480,
				40.726440,-74.056594,
				40.727055,-74.056978,
				40.728096,-74.058140,
				40.728887,-74.059040,
				40.729627,-74.059861,
				40.730053,-74.060339,
				40.730835,-74.061241,
				40.731173,-74.061650,
				40.731963,-74.062589,
				40.732331,-74.063049,
				40.733719,-74.064240,
				40.734226,-74.065091,
				40.734596,-74.065585,
				40.735637,-74.067340,
				40.736200,-74.068739,
				40.737021,-74.071379,
				40.738381,-74.076542,
				40.739776,-74.081847,
				40.740623,-74.084924,
				40.741153,-74.087380,
				40.741307,-74.091689,
				40.741494,-74.096962,
				40.741732,-74.102578,
				40.741795,-74.104117,
				40.741907,-74.105110,
				40.742452,-74.110874,
				40.743031,-74.116604,
				40.743195,-74.122483,
				40.743227,-74.123931,
				40.743313,-74.126464,
				40.743422,-74.128876,
				40.743189,-74.131670,
				40.743040,-74.133609,
				40.742705,-74.137631,
				40.742343,-74.141866,
				40.741907,-74.146797,
				40.741776,-74.148237,
				40.741379,-74.151334,
				40.741050,-74.152678,
				40.739462,-74.155694,
				40.738693,-74.157311,
				40.737901,-74.158780,
				40.736340,-74.161117,
				40.734537,-74.163756
		};
		
		rectOptions = new PolylineOptions().color(Color.parseColor("#d93a30")).width(15).zIndex(0);
		
		for (int i = 0; i < (NWK_WTC.length-1); i++)
		{
			LatLng location = new LatLng(NWK_WTC[i], NWK_WTC[++i]);
			rectOptions.add(location);
		}
		
		gMap.addPolyline(rectOptions);
		
		systemMapCamera();
	}
	
	/**
	 * Draws the polylines for the nights/weekend map, enables/disables appropriate buttons, and zooms to the entire system. 
	 * Finally, calls to add the station markers and passes an integer representing what map it is.
	 */
	private void nightsWeekendsMap()
	{
		if (currentMap.equals("nights"))
			addStationMarkers(1, true);
		else
			addStationMarkers(1, false);
		
		double[] NWK_WTC = {
				40.712713,-74.011963,
				40.713561,-74.016213,
				40.714564,-74.021281,
				40.715442,-74.025719,
				40.716761,-74.032375,
				40.716918,-74.032988,
				40.717170,-74.034820,
				40.717360,-74.035920,
				40.717713,-74.037241,
				40.719160,-74.041876,
				40.719314,-74.042392,
				40.719528,-74.043082,
				40.720585,-74.046403,
				40.722060,-74.050976,
				40.722733,-74.053320,
				40.723020,-74.053867,
				40.723671,-74.054726,
				40.724281,-74.055468,
				40.725069,-74.055990,
				40.725767,-74.056432,
				40.726035,-74.056480,
				40.726440,-74.056594,
				40.727055,-74.056978,
				40.728096,-74.058140,
				40.728887,-74.059040,
				40.729627,-74.059861,
				40.730053,-74.060339,
				40.730835,-74.061241,
				40.731173,-74.061650,
				40.731963,-74.062589,
				40.732331,-74.063049,
				40.733719,-74.064240,
				40.734226,-74.065091,
				40.734596,-74.065585,
				40.735637,-74.067340,
				40.736200,-74.068739,
				40.737021,-74.071379,
				40.738381,-74.076542,
				40.739776,-74.081847,
				40.740623,-74.084924,
				40.741153,-74.087380,
				40.741307,-74.091689,
				40.741494,-74.096962,
				40.741732,-74.102578,
				40.741795,-74.104117,
				40.741907,-74.105110,
				40.742452,-74.110874,
				40.743031,-74.116604,
				40.743195,-74.122483,
				40.743227,-74.123931,
				40.743313,-74.126464,
				40.743422,-74.128876,
				40.743189,-74.131670,
				40.743040,-74.133609,
				40.742705,-74.137631,
				40.742343,-74.141866,
				40.741907,-74.146797,
				40.741776,-74.148237,
				40.741379,-74.151334,
				40.741050,-74.152678,
				40.739462,-74.155694,
				40.738693,-74.157311,
				40.737901,-74.158780,
				40.736340,-74.161117,
				40.734537,-74.163756
		};
		
		PolylineOptions rectOptions = new PolylineOptions().color(Color.parseColor("#d93a30")).width(15).zIndex(0);
		
		for (int i = 0; i < (NWK_WTC.length-1); i++)
		{
			LatLng location = new LatLng(NWK_WTC[i], NWK_WTC[++i]);
			rectOptions.add(location);
		}
		
		gMap.addPolyline(rectOptions);
		
		double[] JSQ_HOBOKEN_33 = {
				40.749111,-73.988276,
				40.742890,-73.992803,
				40.737345,-73.996844,
				40.734244,-73.999110,
				40.734228,-73.999114,
				40.734179,-73.999146,
				40.734070,-73.999537,
				40.733839,-74.001189,
				40.733596,-74.002847,
				40.732957,-74.007072,
				40.732639,-74.008638,
				40.732297,-74.011037,
				40.732026,-74.013335,
				40.731725,-74.017805,
				40.731666,-74.021147,
				40.731642,-74.022234,
				40.732022,-74.027358,
				40.732287,-74.028822,
				40.732370,-74.029064,
				40.732535,-74.029251,
				40.732659,-74.029344,
				40.732901,-74.029446,
				40.733179,-74.029516,
				40.733551,-74.029578,
				40.733793,-74.029625,
				40.733970,-74.029617,
				40.735873,-74.029219,
				40.732331,-74.031045,
				40.727990,-74.033300,
				40.727055,-74.033811,
				40.727000,-74.033821,
				40.724932,-74.034214,
				40.720369,-74.035033,
				40.717318,-74.035609,
				40.717533,-74.036509,
				40.717749,-74.037274,
				40.719187,-74.041963,
				40.719314,-74.042392,
				40.719528,-74.043082,
				40.720585,-74.046403,
				40.722060,-74.050976,
				40.722733,-74.053320,
				40.723020,-74.053867,
				40.723671,-74.054726,
				40.724281,-74.055468,
				40.725069,-74.055990,
				40.725767,-74.056432,
				40.726035,-74.056480,
				40.726440,-74.056594,
				40.727055,-74.056978,
				40.728096,-74.058140,
				40.728887,-74.059040,
				40.729627,-74.059861,
				40.730053,-74.060339,
				40.730835,-74.061241,
				40.731173,-74.061650,
				40.731963,-74.062589,
				40.732331,-74.063049
		};
		
		rectOptions = new PolylineOptions().color(Color.parseColor("#FF9900")).zIndex(1);
		
		for (int i = 0; i < (JSQ_HOBOKEN_33.length-1); i++)
		{
			LatLng location = new LatLng(JSQ_HOBOKEN_33[i], JSQ_HOBOKEN_33[++i]);
			rectOptions.add(location);
		}
		
		gMap.addPolyline(rectOptions);
		
		systemMapCamera();
	}
	
	/**
	 * Depending on the parameter, this will add markers representing the stations to the map.
	 * It'll also add the name of the station, the services there, and any service advisory harming 
	 * this station into the info window. Finally, it will add entrance markers that are open at the current time if the 
	 * map is the current system configuration.
	 * 
	 * @param map 0 if weekday map, 1 if weekends/night map
	 * @param isCurrent true if the map is the system configuration at this time 
	 */
	private void addStationMarkers (int map, boolean isCurrent)
	{		
		for (int i = 0; i < allStations.size(); i++)
		{
			Stop tempStation = allStations.get(i);
			LatLng location = new LatLng(tempStation.getStopLocation()[0], tempStation.getStopLocation()[1]);
			String stop_id = tempStation.getStopID();
			String title = tempStation.getStopName();
							
			MarkerOptions marker = new MarkerOptions().position(location).title(title).snippet(stop_id);			
			
			gMap.addMarker(marker);
			bounds.include(location);
		}	
		
		/*
		 * add entrances that are open if it is the current map
		 */
		openEntrances = new ArrayList<Marker>();
		
		if (isCurrent)
		{
			for (int j = 0; j < allStations.size(); j++)
			{
				Stop s = allStations.get(j);
				ArrayList<Entrance> entrances = s.getEntranceList();
				entrances = removeEntrancesTimeAndDay(entrances);
				
				 /*
				  *  add any other checks here
				  *  after this, adds markerOptions to map and add the marker to ArrayList
				  */			 
				for (int x = 0; x < entrances.size(); x++)
				{
					Entrance e = entrances.get(x);
					String snip = "";
															
					/*
					 * note if handicap accessible
					 */
					if (e.isElevator() && e.isEscalator())
					{
						snip = snip + "Escalator and elevator access to platform\n";
					}
					else if (e.isElevator() && !e.isEscalator())
					{
						snip = snip + "Elevator access to platform\n";
					}
					else if (!e.isElevator() && e.isEscalator())
					{
						snip = snip + "Escalator access to platform\n";
					}
					
					/*
					 * if harrison, 23rd street, or 14th street, note what platform the entrance leads to
					 */
					if (e.getStopid().equalsIgnoreCase("har"))
					{
						if (e.isNjbound())
							snip = snip + "Trains to Newark\n";
						else if (e.isNybound())
							snip = snip + "Trains to World Trade Center\n";
					}
					else if (e.getStopid().equalsIgnoreCase("14th") || e.getStopid().equalsIgnoreCase("23rd"))
					{
						if (e.isNjbound())
							snip = snip + "Trains to New Jersey\n";
						else if (e.isNybound())
							snip = snip + "Trains to 33rd Street\n";
					}
					
					/*
					 * note if entrance is inaccessible via some transport option
					 */
					if (e.getEntranceNotes().contains("No Driving"))
					{
						snip = snip + "Not accessible via car\n";
					}
					else if (e.getEntranceNotes().contains("No Biking"))
					{
						snip = snip + "Not accessible via bike\n";
					}
					else if (e.getEntranceNotes().contains("No Public Transit"))
					{
						snip = snip + "Not accessible via public transit\n";
					}
					else if (e.getEntranceNotes().contains("No Walking"))
					{
						snip = snip + "Not accessible via walking\n";
					}
									
					LatLng location = new LatLng (e.getEntranceLocation()[0], e.getEntranceLocation()[1]);
					MarkerOptions markerO = new MarkerOptions()
												.position(location)
												.title("Entrance for " + s.getStopName())
												.snippet(snip)
												.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_node))
												.flat(true)
												.visible(false);
					Marker m = gMap.addMarker(markerO);
					openEntrances.add(m);
				}
			}
		}
	}
		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		
		allStations = MainActivity.getAllStations();
        rootView = inflater.inflate(R.layout.fragment_system_map, container, false);
        
        gMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.systemMap)).getMap();		
        gMap.setMyLocationEnabled(false);
		gMap.setBuildingsEnabled(false);
		gMap.getUiSettings().setZoomControlsEnabled(false);
		gMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
		
		bounds = new LatLngBounds.Builder();
				
		currentMap();
		listeners();
		        		
        return rootView;
    }

	/**
	 * @author shea
	 * 
	 * Implements InfoWindow Adapter, it allows us to create a custom info window for the markers 
	 * on the map. We can now show the station name, the name of each line that passes through this station, and 
	 * inform the user if there is a service advisory affecting this station.
	 */
	class MyInfoWindowAdapter implements InfoWindowAdapter {

		private final View myContentsView;

		MyInfoWindowAdapter() {
			LayoutInflater inflater = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myContentsView = inflater.inflate(R.layout.custom_info_contents, null);
		}

		@Override
		public View getInfoContents(Marker marker) {

			TextView stationName = ((TextView) myContentsView
					.findViewById(R.id.info_title));
			stationName.setText(marker.getTitle());
			
			TextView routeService1 = ((TextView) myContentsView.findViewById(R.id.route_service1));
			TextView routeService2 = ((TextView) myContentsView.findViewById(R.id.route_service2));
			TextView routeService3 = ((TextView) myContentsView.findViewById(R.id.route_service3));
			routeService1.setTextColor(Color.BLACK);
			routeService2.setTextColor(Color.BLACK);
			routeService3.setTextColor(Color.BLACK);
			routeService1.setText(null);
			routeService2.setText(null);
			routeService3.setText(null);
			routeService1.setVisibility(View.GONE);
			routeService2.setVisibility(View.GONE);
			routeService3.setVisibility(View.GONE);
			
			ArrayList<TextView> routeServicesArrayList = new ArrayList<TextView>();
			routeServicesArrayList.add(routeService1);
			routeServicesArrayList.add(routeService2);
			routeServicesArrayList.add(routeService3);
			
			TextView stopTimeService1 = ((TextView) myContentsView.findViewById(R.id.stoptime_service1));
			TextView stopTimeService2 = ((TextView) myContentsView.findViewById(R.id.stoptime_service2));
			TextView stopTimeService3 = ((TextView) myContentsView.findViewById(R.id.stoptime_service3));
			stopTimeService1.setTextColor(Color.BLACK);
			stopTimeService2.setTextColor(Color.BLACK);
			stopTimeService3.setTextColor(Color.BLACK);
			stopTimeService1.setText(null);
			stopTimeService2.setText(null);
			stopTimeService3.setText(null);
			stopTimeService1.setVisibility(View.GONE);
			stopTimeService2.setVisibility(View.GONE);
			stopTimeService3.setVisibility(View.GONE);
			
			ArrayList<TextView> stopTimeServicesArrayList = new ArrayList<TextView>();
			stopTimeServicesArrayList.add(stopTimeService1);
			stopTimeServicesArrayList.add(stopTimeService2);
			stopTimeServicesArrayList.add(stopTimeService3);
			
			/*
			 * for markers that are entrances
			 */
			if (marker.getTitle().contains("Entrance"))
			{
				String[] services = marker.getSnippet().split("\\r?\\n");
				
				for (int i = 0; i < services.length && services.length < routeServicesArrayList.size(); i++)
				{
					if (!services[i].isEmpty())
					{
						routeServicesArrayList.get(i).setText(services[i]);
						routeServicesArrayList.get(i).setVisibility(View.VISIBLE);
					}
				}
				
				return myContentsView;
			}
			/*
			 * for station markers
			 */
			else
			{
				Calendar now = Calendar.getInstance();
				
				TimeStampsAndRoutes timeStampsAndRoutes = MainActivity
						.getPathStations().getStopTimesForStop(
								marker.getSnippet(),
								now, false);
				String[] allRoutesWithRouteColor = timeStampsAndRoutes.getAllRoutesWithDirection();
				StopTime[] allTimeStamps = timeStampsAndRoutes.getAllTimeStamps();
				Arrays.sort(allTimeStamps);
				
				/*
				 * Gets the stop times for the next day
				 */
				Calendar tomorrow = (Calendar) now.clone();
				tomorrow.add(Calendar.DAY_OF_WEEK, 1);
				
				TimeStampsAndRoutes	timeStampsRoutes_NextDay = MainActivity.getPathStations()
							.getStopTimesForStop(
									marker.getSnippet(),
									tomorrow, true);
				String[] allRoutesAndColor_NextDay = timeStampsRoutes_NextDay.getAllRoutesWithDirection();
				StopTime[] allTimeStamps_NextDay = timeStampsRoutes_NextDay.getAllTimeStamps();
				Arrays.sort(allTimeStamps_NextDay);
					
				Set<String> set = new HashSet<String>();
				set.addAll(Arrays.asList(allRoutesWithRouteColor));
				set.addAll(Arrays.asList(allRoutesAndColor_NextDay));
				allRoutesWithRouteColor = set.toArray(new String[0]);								
				
				int l_limit = 2, m_limit = 2;
				
				for (int x = 0; x < allRoutesWithRouteColor.length; x++)
				{
					String[] routeAndRouteColor = allRoutesWithRouteColor[x].split(",");
					ArrayList<StopTime> tempTimes_0 = new ArrayList<StopTime>();
					ArrayList<StopTime> tempTimes_1 = new ArrayList<StopTime>();
					int l = 0, m = 0;
					
					for (int y = 0; y < allTimeStamps.length; y++)
					{
						if (routeAndRouteColor[0].equalsIgnoreCase(allTimeStamps[y].getRoute_id()))
						{
							if (allTimeStamps[y].getDirection_id().equalsIgnoreCase("0") && l < l_limit)
							{
								tempTimes_0.add(allTimeStamps[y]);
								l++;
							}
							else if (allTimeStamps[y].getDirection_id().equalsIgnoreCase("1") && m < m_limit)
							{
								tempTimes_1.add(allTimeStamps[y]);
								m++;
							}
						}
					}
					
					for (int y = 0; y < allTimeStamps_NextDay.length; y++)
					{
						if (routeAndRouteColor[0].equalsIgnoreCase(allTimeStamps_NextDay[y].getRoute_id()))
						{
							String dayOfWeek = null;
							switch (tomorrow.get(Calendar.DAY_OF_WEEK))
							{
								case (1):
									dayOfWeek = "(Sun)";
									break;
								case (2):
									dayOfWeek = "(Mon)";
									break;
								case (3):
									dayOfWeek = "(Tue)";
									break;
								case (4):
									dayOfWeek = "(Wed)";
									break;
								case (5):
									dayOfWeek = "(Thu)";
									break;
								case (6):
									dayOfWeek = "(Fri)";
									break;
								case (7):
									dayOfWeek = "(Sat)";
									break;
							}
							
							if (allTimeStamps_NextDay[y].getDirection_id().equalsIgnoreCase("0") && l < l_limit)
							{					
								StopTime tempST = allTimeStamps_NextDay[y];
								tempST.setDayOfWeek(dayOfWeek);
								tempTimes_0.add(tempST);
								l++;
							}
							else if (allTimeStamps_NextDay[y].getDirection_id().equalsIgnoreCase("1") && m < m_limit)
							{
								StopTime tempST = allTimeStamps_NextDay[y];
								tempST.setDayOfWeek(dayOfWeek);
								tempTimes_1.add(tempST);
								m++;
							}
						}
					}
										
					String route_name;
					String direction_0 = "";
					String direction_1 = "";
					
					for (int t = 0; t < tempTimes_0.size(); t++)
					{
						if (direction_0.isEmpty())
						{
							direction_0 = "To " + tempTimes_0.get(t).getTrip_headsign();
						}
						
						direction_0 = direction_0 + " " + tempTimes_0.get(t).getDeparture_time(rootView.getContext());
					}
					for (int t = 0; t < tempTimes_1.size(); t++)
					{						
						if (direction_1.isEmpty())
						{
							direction_1 = "To " + tempTimes_1.get(t).getTrip_headsign();
						}
						
						direction_1 = direction_1 + " " + tempTimes_1.get(t).getDeparture_time(rootView.getContext());
					}
					
					if (tempTimes_0.size() != 0)
						route_name = tempTimes_0.get(0).getRoute_long_name();
					else if (tempTimes_1.size() != 0)
						route_name = tempTimes_1.get(0).getRoute_long_name();
					else
						break;
					
					if (!direction_0.isEmpty() && !direction_1.isEmpty())
					{
						direction_0 = direction_0 + "\n";
					}
					
					routeServicesArrayList.get(x).setText(route_name);
					routeServicesArrayList.get(x).setVisibility(View.VISIBLE);
					routeServicesArrayList.get(x).setTextColor(Color.parseColor("#" + routeAndRouteColor[1]));
					stopTimeServicesArrayList.get(x).setText(direction_0 + direction_1);
					stopTimeServicesArrayList.get(x).setVisibility(View.VISIBLE);
					stopTimeServicesArrayList.get(x).setTextColor(Color.BLACK);
				}
			}
						
			// if one, then just one line
			/*if (services.length == 1)
			{
				if (services[0].equalsIgnoreCase("Hoboken-World Trade Center"))
				{
					service1.setTextColor(Color.parseColor("#65c100"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Newark-World Trade Center"))
				{
					service1.setTextColor(Color.parseColor("#d93a30"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Hoboken-33rd Street"))
				{
					service1.setTextColor(Color.parseColor("#4d92fb"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Journal Square-33rd Street") || 
						services[0].equalsIgnoreCase("Journal Square-33rd Street (via Hoboken)"))
				{
					service1.setTextColor(Color.parseColor("#FF9900"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
			}			
			// if 2, then one line and either second line or service advisory
			else if (services.length == 2)
			{
				// line 1
				if (services[0].equalsIgnoreCase("Hoboken-World Trade Center"))
				{
					service1.setTextColor(Color.parseColor("#65c100"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Newark-World Trade Center"))
				{
					service1.setTextColor(Color.parseColor("#d93a30"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Hoboken-33rd Street"))
				{
					service1.setTextColor(Color.parseColor("#4d92fb"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Journal Square-33rd Street") || 
						services[0].equalsIgnoreCase("Journal Square-33rd Street (via Hoboken)"))
				{
					service1.setTextColor(Color.parseColor("#FF9900"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
							
				// line 2 or advisory
				if (services[1].equalsIgnoreCase("Hoboken-World Trade Center"))
				{
					service2.setTextColor(Color.parseColor("#65c100"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}
				else if (services[1].equalsIgnoreCase("Newark-World Trade Center"))
				{
					service2.setTextColor(Color.parseColor("#d93a30"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}
				else if (services[1].equalsIgnoreCase("Hoboken-33rd Street"))
				{
					service2.setTextColor(Color.parseColor("#4d92fb"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}
				else if (services[1].equalsIgnoreCase("Journal Square-33rd Street") || 
						services[1].equalsIgnoreCase("Journal Square-33rd Street (via Hoboken)"))
				{
					service2.setTextColor(Color.parseColor("#FF9900"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}			
				else
				{
					service3.setVisibility(View.VISIBLE);
					service3.setText("Click for service advisory");
				}
			}			
			// if 3, then two lines and advisory
			else if (services.length == 3)
			{				
				// line 1
				if (services[0].equalsIgnoreCase("Hoboken-World Trade Center"))
				{
					service1.setTextColor(Color.parseColor("#65c100"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Newark-World Trade Center"))
				{
					service1.setTextColor(Color.parseColor("#d93a30"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Hoboken-33rd Street"))
				{
					service1.setTextColor(Color.parseColor("#4d92fb"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
				else if (services[0].equalsIgnoreCase("Journal Square-33rd Street") || 
						services[0].equalsIgnoreCase("Journal Square-33rd Street (via Hoboken)"))
				{
					service1.setTextColor(Color.parseColor("#FF9900"));
					service1.setText(services[0]);
					service1.setVisibility(View.VISIBLE);
				}
						
				// line 2
				if (services[1].equalsIgnoreCase("Hoboken-World Trade Center"))
				{
					service2.setTextColor(Color.parseColor("#65c100"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}
				else if (services[1].equalsIgnoreCase("Newark-World Trade Center"))
				{
					service2.setTextColor(Color.parseColor("#d93a30"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}
				else if (services[1].equalsIgnoreCase("Hoboken-33rd Street"))
				{
					service2.setTextColor(Color.parseColor("#4d92fb"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}
				else if (services[1].equalsIgnoreCase("Journal Square-33rd Street") || 
						services[1].equalsIgnoreCase("Journal Square-33rd Street (via Hoboken)"))
				{
					service2.setTextColor(Color.parseColor("#FF9900"));
					service2.setText(services[1]);
					service2.setVisibility(View.VISIBLE);
				}				
				
				// service advisory
				service3.setVisibility(View.VISIBLE);
				service3.setText("Click for service advisory");
			}*/
			
			return myContentsView;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}

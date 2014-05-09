package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.Calendar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.app.AlertDialog;
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
	private ArrayList<Station> allStations;
	private LatLngBounds.Builder bounds;
	
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
		
		Button weekdays = (Button) rootView.findViewById(R.id.button_weekdays);
		Button nightsWeekends = (Button) rootView.findViewById(R.id.button_nightsweekends);
		Button zoomToMap = (Button) rootView.findViewById(R.id.button_zoomToMap);
			
		weekdays.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				gMap.clear();				
				weekdayMap();
			}
		});
		
		nightsWeekends.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				gMap.clear();
				nightsWeekendsMap();
			}
		});
		
		zoomToMap.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				systemMapCamera();
			}
		});
		

		/*
		 * Marker click listener that'll open the info window, displaying the station name and services and any
		 * service advisories. Also zooms in to level 15.
		 */
		gMap.setOnMarkerClickListener(new OnMarkerClickListener()
		{
			@Override
			public boolean onMarkerClick(Marker arg0) 
			{
				if (arg0 != null)
					gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(arg0.getPosition(), 15));

				return false;
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
				if (arg0 != null)
				{
					String[] services = arg0.getSnippet().split("\\r?\\n");
					
					if (services.length == 3)
					{
						new AlertDialog.Builder(rootView.getContext())
							.setTitle("Service Advisory")
							.setMessage(services[2])
							.setNeutralButton("Close", null)
							.setCancelable(false)
							.show();
					}
					
					else if (services.length == 2)
					{
						if (services[1].equalsIgnoreCase("Hoboken-World Trade Center") == false && 
								services[1].equalsIgnoreCase("Newark-World Trade Center") == false &&
								services[1].equalsIgnoreCase("Hoboken-33rd Street") == false &&
								services[1].equalsIgnoreCase("Journal Square-33rd Street") == false && 
								services[1].equalsIgnoreCase("Journal Square-33rd Street (via Hoboken)") == false)
						{
							new AlertDialog.Builder(rootView.getContext())
								.setTitle("Service Advisory")
								.setMessage(services[1])
								.setNeutralButton("Close", null)
								.setCancelable(false)
								.show();
						}
					}
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
		((Button) rootView.findViewById(R.id.button_weekdays)).setEnabled(false);
		((Button) rootView.findViewById(R.id.button_nightsweekends)).setEnabled(true);
		
		addStationMarkers(0);
		
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
		((Button) rootView.findViewById(R.id.button_weekdays)).setEnabled(true);
		((Button) rootView.findViewById(R.id.button_nightsweekends)).setEnabled(false);
		
		addStationMarkers(1);
		
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
	 * this station into the info window.
	 * 
	 * @param map 0 if weekday map, 1 if weekends/night map
	 */
	private void addStationMarkers (int map)
	{		
		if (map == 0)
		{
			for (int i = 0; i < allStations.size(); i++)
			{
				Station tempStation = allStations.get(i);
				LatLng location = new LatLng(tempStation.getStationLocation()[0], tempStation.getStationLocation()[1]);
				String weekdayService = "";
				String title = tempStation.getStationName();
				
				for (int j = 0; j < tempStation.getWeekdayService().size(); j++)
				{
					weekdayService = weekdayService + tempStation.getWeekdayService().get(j) + "\n";
				}
								
				MarkerOptions marker = new MarkerOptions().position(location).title(title).snippet(weekdayService);			
				
				gMap.addMarker(marker);
				bounds.include(location);
			}			
		}
		
		else if (map == 1)
		{
			for (int i = 0; i < allStations.size(); i++)
			{
				Station tempStation = allStations.get(i);
				LatLng location = new LatLng(tempStation.getStationLocation()[0], tempStation.getStationLocation()[1]);
				String weekendService = "";
				String title = tempStation.getStationName();
				MarkerOptions marker = new MarkerOptions().position(location).title(title);
				
				for (int j = 0; j < tempStation.getWeekendService().size(); j++)
				{
					weekendService = weekendService + tempStation.getWeekendService().get(j) + "\n";
				}
				
				/*
				 * Code to tell the user of the closures on the WTC-NWK line throughout 2014 on the weekends
				 */
				if (tempStation.getStationID().equalsIgnoreCase("wtc") || 
					tempStation.getStationID().equalsIgnoreCase("exp") ||
					tempStation.getStationID().equalsIgnoreCase("grv") ||
					tempStation.getStationID().equalsIgnoreCase("jsq") ||
					tempStation.getStationID().equalsIgnoreCase("har") ||
					tempStation.getStationID().equalsIgnoreCase("nwk"))
				{
					weekendService = weekendService + "Weekend Service Changes Through 2014: World Trade Center & Exchange Place Stations CLOSED WEEKENDS. " +
							"PATH operates two lines of weekend service: Newark-Journal Sq & Journal Sq-33 Street (via Hoboken). " +
							"World Trade Center & Exchange Place Stations reopen Mondays at 4:45 a.m.";
				}				
								
				marker.snippet(weekendService);			
				
				gMap.addMarker(marker);
				bounds.include(location);
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
				weekdayMap();
			}
			
			else
			{
				nightsWeekendsMap();	
			}
		}
		else
		{
			nightsWeekendsMap();
		}
		
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
						
			String[] services = marker.getSnippet().split("\\r?\\n");

			TextView service1 = ((TextView) myContentsView.findViewById(R.id.info_service1));
			TextView service2 = ((TextView) myContentsView.findViewById(R.id.info_service2));
			TextView service3 = ((TextView) myContentsView.findViewById(R.id.info_service3));
			service1.setText(null);
			service2.setText(null);
			service3.setText(null);
			service1.setVisibility(View.GONE);
			service2.setVisibility(View.GONE);
			service3.setVisibility(View.GONE);
			
			// if one, then just one line
			if (services.length == 1)
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
			}
			
			return myContentsView;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}

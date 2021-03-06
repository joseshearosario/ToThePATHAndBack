package com.shearosario.tothepathandback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import com.google.android.gms.maps.model.LatLng;

/**
 * 
 * Serves to prepare an intent, that'll be sent to the next activity (ClosestStationsAcitivity) when we start it. 
 * We call to create the URLs for each station, call on them and save what is returned. If it is viable, then 
 * extract a sort measure (distance/duration) specified by the user, and we will use it to select and sort three stations 
 * for use. After we follow a similar process for all the appropriate entrances at each of the three stations. However, 
 * we only selected one entrance for each station. The sort measure used, the stations and their entrance, are then put into an 
 * intent and the activity is called.
 * 
 * @author shea
 *
 */

public class ClosestStationsIntent 
{	
	private static Boolean isNYbound;
	private static Context context;
	private static Activity activity;
	
	/**
	 * Some entrances cannot be reached by certain modes of transportation. For example, you cannot access an entrance 
	 * that's surrounded by bus lanes via automobile. These limitations are put in the notes of each entrance. 
	 * This goes through an ArrayList of entrances, searches through its notes to see if there are any limitations and compare it to 
	 * the selected mode of transport by the user. If there is a conflict then the entrance is removed. The list with all necessary entrances 
	 * removed is returned.
	 * 
	 * @param entrances
	 * @return entrances that can be reached by the transport mode selected by the user
	 */
	private ArrayList<Entrance> removeEntrancesTransportMode(ArrayList<Entrance> entrances)
	{
		ArrayList<Entrance> tempEntrances = new ArrayList<Entrance>(entrances);
		for (int i = 0; i < tempEntrances.size();)
		{
			Entrance  e = tempEntrances.get(i);
			if (e.getEntranceNotes() == null || e.getEntranceNotes().isEmpty() == true)
			{
				i++;
				continue;
			}
			else
			{
				if (e.getEntranceNotes().contains("No Driving") && 
							(ClosestStationFragment.getTransportMode().equalsIgnoreCase("fastest") || 
							 ClosestStationFragment.getTransportMode().equalsIgnoreCase("shortest")))
				{
					tempEntrances.remove(i);
					continue;
				}
				else if (e.getEntranceNotes().contains("No Biking") && 
							ClosestStationFragment.getTransportMode().equalsIgnoreCase("bicycle"))
				{
					tempEntrances.remove(i);
					continue;
				}
				else if (e.getEntranceNotes().contains("No Public Transit") && 
							ClosestStationFragment.getTransportMode().equalsIgnoreCase("multimodal"))
				{
					tempEntrances.remove(i);
					continue;
				}
				else if (e.getEntranceNotes().contains("No Walking") && 
							ClosestStationFragment.getTransportMode().equalsIgnoreCase("pedestrian"))
				{
					tempEntrances.remove(i);
					continue;
				}
			}
			i++;
		}
		return tempEntrances;
	}
	
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
						timePeriods.add(notes[i]);
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
						tempEntrances.remove(x);
				else
					x++;
			}
		}
		return tempEntrances;
	}
	
	/**
	 * Extreme case: Because of the closure at WTC and Exchange Place over
	 * the weekends in 2014, we remove them from the sorted stations if it's the
	 * weekend when this runs. Remove when the station closures have ended (No
	 * time frame has been given, just "through 2014")
	 * 
	 * @param stations
	 * @return all stations except WTC and EXP if they were present
	 */
	/*private ArrayList<Stop> removeWtcExpWeekends2014 (ArrayList<Stop> stations)
	{
		ArrayList<Stop> tempStations = new ArrayList<Stop>(stations);
		for (int y = 0; y < tempStations.size();)
		{
			int day2014 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			int hour2014 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			int minute2014 = Calendar.getInstance().get(Calendar.MINUTE);
			if (tempStations.get(y).getStopID().equalsIgnoreCase("26734") || tempStations.get(y).getStopID().equalsIgnoreCase("26727"))
			{
				if (day2014 == 7 || day2014 == 1)
				{
					tempStations.remove(y);
					continue;
				}
				else if (day2014 == 6)
				{
					if (hour2014 == 23 && minute2014 > 55)
					{
						tempStations.remove(y);
						continue;
					}
				}
				else if (day2014 == 2)
				{
					if (hour2014 <= 4 && minute2014 < 45)
					{
						tempStations.remove(y);
						continue;
					}
				}
			}
			y++;
		}
		
		return tempStations;
	}*/
	
	/**
	 * Harrison serves its commuters on separate platforms, there is no way to change direction. Therefore, we ask the 
	 * user in what direction they are intending on going in order to direct them to the right platform entrance. They are asked 
	 * whether they are going Newark-bound (NJ-bound) or toward the WTC (NY-bound). The entrance corresponding to their decision is returned.
	 * 
	 * @param entrances
	 * @param c context
	 * @param a activity
	 * @return ArrayList of entrances that are bound for either NY or NJ, based on the user input
	 */
	private ArrayList<Entrance> harrisonPlatform (final ArrayList<Entrance> entrances, final Context c, Activity a)
	{
		final ArrayList<Entrance> tempEntrances = new ArrayList<Entrance>(entrances);
		// http://www.avajava.com/tutorials/lessons/how-do-i-use-the-wait-and-notify-methods.html
		Runnable harrisonUI = new Runnable()
		{
			@Override
			public void run() 
			{
				new AlertDialog.Builder(c)
					.setMessage("Which way are you heading?")
					.setPositiveButton("Newark-bound", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							for (int x = 0; x < tempEntrances.size();)
							{
								if (tempEntrances.get(x).isNybound())
								{
									tempEntrances.remove(x);
									continue;
								}
								x++;
							}
							dialog.dismiss();
							synchronized(tempEntrances)
							{
								tempEntrances.notify();
							}
						}
					})	
					.setNegativeButton("WTC-bound", new OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							for (int x = 0; x < tempEntrances.size();)
							{
								if (tempEntrances.get(x).isNjbound())
								{
									tempEntrances.remove(x);
									continue;
								}
								x++;
							}	
							dialog.dismiss();
							synchronized(tempEntrances)
							{
								tempEntrances.notify();
							}
						}	
					})
					.setCancelable(false)
					.show();
			}
		};											

		synchronized (tempEntrances)
		{	
			a.runOnUiThread(harrisonUI);
			try {
				tempEntrances.wait();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		return tempEntrances;
	}

	/**
	 * At 14th and 23rd Street, the platforms are separated. In order to direct the user to the correct platform, we ask 
	 * where the user is intended on going: 33rd Street bound (NY) or toward NJ. Based on the selection by the user, we remove those 
	 * entrances that the user does not need to know.
	 * 
	 * @param entrances
	 * @param c context
	 * @param a activity
	 * @return ArrayList of entrances to platform in the direction the user selected
	 */
	private ArrayList<Entrance> platform14th23rd (final ArrayList<Entrance> entrances, final Context c, Activity a)
	{
		final ArrayList<Entrance> tempEntrances = new ArrayList<Entrance>(entrances);

		if (isNYbound != null)
		{
			for (int x = 0; x < tempEntrances.size();)
			{
				if (isNYbound)
				{
					if (tempEntrances.get(x).isNjbound())
					{
						tempEntrances.remove(x);
						continue;
					}
				}
				else
				{
					if (tempEntrances.get(x).isNybound())
					{
						tempEntrances.remove(x);
						continue;
					}
				}
				
				x++;
			}
		}
		else
		{
			Runnable manhattanUI = new Runnable()
			{
				@Override
				public void run() 
				{
					new AlertDialog.Builder(c)
							.setMessage("Which way are you heading?")
							.setPositiveButton("33rd St-bound", new OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									isNYbound = true;
									dialog.dismiss();
									synchronized(tempEntrances)
									{
										tempEntrances.notify();
									}
								}
							})
							.setNegativeButton("NJ-bound", new OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									isNYbound = false;
									dialog.dismiss();
									synchronized(tempEntrances)
									{
										tempEntrances.notify();
									}
								}
							})
							.setCancelable(false)
							.show();
				}
			};
			
			synchronized (tempEntrances)
			{	
				a.runOnUiThread(manhattanUI);
				try {
					tempEntrances.wait();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			for (int x = 0; x < tempEntrances.size();)
			{
				if (isNYbound)
				{
					if (tempEntrances.get(x).isNjbound())
					{
						tempEntrances.remove(x);
						continue;
					}
				}
				else
				{
					if (tempEntrances.get(x).isNybound())
					{
						tempEntrances.remove(x);
						continue;
					}
				}
				
				x++;
			}
		}
		
		return tempEntrances;
	}
	
	/**
	 * If the user has selected in the main activity that they need to enter a station through a handicap entrance, 
	 * then here we select the stations that have that specific type of entrance.
	 * 
	 * @return an arraylist of stations that have a specific handicap entrance selected by the user
	 */
	private ArrayList<Stop> getHandicapAccessStations()
	{
		ArrayList<Stop> handicapStations = new ArrayList<Stop>();
		
		for (int i = 0; i < MainActivity.getAllStations().size(); i++)
		{
			if (MainActivity.getAllStations().get(i).isHandicapAccessible(ClosestStationFragment.getHandicapAccess()))
				handicapStations.add(MainActivity.getAllStations().get(i));
		}
		
		return handicapStations;
	}
		
	/**
	 * We determine whether to use the stations in either New York or New
	 * Jersey. If the origin is in New York or in any New England state, we'll use
	 * NY stations. Else, we use NJ stations. If the origin is not in the
	 * contiguous United States, we return an empty string.
	 * 
	 * @param origin
	 *            the origin of the user
	 * @return whether we use NY or NJ stations
	 */
	private String getOriginState(String originState, String originCounty)
	{		
		if (originState.compareToIgnoreCase("New Jersey") == 0)
		{
			String[] newJerseyCounties = {
					"Atlantic",
					"Bergen",
					"Camden",
					"Essex",
					"Hudson", 
					"Hunterdon",
					"Mercer",
					"Middlesex",
					"Monmouth",
					"Morris",
					"Ocean",
					"Passaic",
					"Somerset",
					"Union",
					"Warren"
			};
			
			for (int i = 0; i < newJerseyCounties.length; i++)
			{			
				if ((originCounty.toLowerCase(Locale.ENGLISH)).contains(newJerseyCounties[i].toLowerCase(Locale.ENGLISH)))	
				{
					return "NJ";
				}
			}
		}
		else if (originState.compareToIgnoreCase("New York") == 0)
		{
			String[] newYorkEastHudson = {
					"New York",
					"Kings",
					"Queens",
					"Richmond",
					"Bronx",
					"Westchester",
					"Suffolk",
					"Dutchess",
					"Putnam",
					"Nassau"
			};
			
			String[] newYorkWestHudson = {
					"Rockland",
					"Orange",
			};
			
			for (int i = 0; i < newYorkWestHudson.length; i++)
			{
				if ((originCounty.toLowerCase(Locale.ENGLISH)).contains(newYorkWestHudson[i].toLowerCase(Locale.ENGLISH)))
				{
					return "NJ";
				}
			}
			
			for (int i = 0; i < newYorkEastHudson.length; i++)
			{
				if ((originCounty.toLowerCase(Locale.ENGLISH)).contains(newYorkEastHudson[i].toLowerCase(Locale.ENGLISH)))
				{
					return "NY";
				}
			}
		}
		else if (originState.compareToIgnoreCase("Connecticut") == 0)
		{
			String[] ctCounties = {
					"Fairfield",
					"New Haven"
			};
			
			for (int i = 0; i < ctCounties.length; i++)
			{
				if ((originCounty.toLowerCase(Locale.ENGLISH)).contains(ctCounties[i].toLowerCase(Locale.ENGLISH)))
				{
					return "NY";
				}
			}
		}
		else if (originState.compareToIgnoreCase("Pennsylvania") == 0)
		{
			String[] paCounties = {
					"Lackawanna",
					"Monroe",
					"Philadelphia"
			};
			
			for (int i = 0; i < paCounties.length; i++)
			{
				if ((originCounty.toLowerCase(Locale.ENGLISH)).contains(paCounties[i].toLowerCase(Locale.ENGLISH)))
				{
					return "NJ";
				}
			}
		}
		
		return "";
	}
	
	/**
	 * Using DownloadAllData in Directions, we call each url and then save whatever returns.
	 * 
	 * @param urls an array of string urls
	 * @return an array of strings, each the result of calling and saving what the url returns
	 */
	private String downloadJSON (String url)
	{
		/* Get direction data from origin to each station from URLs */
		Directions.DownloadAllData downloadAllData = new Directions.DownloadAllData();
		downloadAllData.execute(url);
		String allJSONdata = null;	
		
		try {
			allJSONdata = downloadAllData.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return allJSONdata;
	}
	
	/**
	 * Parse string and extract the value of either 
	 * distance or duration from the origin to each location searched.
	 * 
	 * @param data an array of string with each element being whatever is returned from their respective URL calls 
	 * @return a double array for the sort measure that is obtained from each data string
	 */
	private double[] getAllSortMeasures(String data)
	{
		double[] allSortMeasures = null; 
		try 
		{
			JSONObject object = new JSONObject (data);
			JSONArray measures = object.getJSONArray(ClosestStationFragment.getDistance_duration());
			allSortMeasures = new double[measures.length() - 1];
			for (int i = 1, j = 0; i < measures.length(); i++, j++)
				allSortMeasures[j] = measures.getDouble(i);
		} 
		catch (Exception e)
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{							
					new AlertDialog.Builder(context)
						.setTitle("Can't Determine Which Is Closer")
						.setMessage("Feel free to try again later.")
						.setPositiveButton("Okay", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int id)
							{
								dialog.cancel();
								activity.recreate();
							}
						}).setCancelable(false).show();
				}
			});
			
			e.printStackTrace();			
		}
		return allSortMeasures;
	}
	
	/**
	 * This is possible because we have kept the order of whatever is being searched for with API calls to Open Mapquest 
	 * and what has returned. We can now compare each location with their respective distance or duration from the origin. 
	 * The work is done by storing the index of the smallest element value possible in allSortMeasures into an array of size 1 to 3. 
	 * We traverse the sort measures as many times as there are indices that we must save. For example, if we are working with an Entrance object, 
	 * then we only need to save one index, the smallest element in allSortMeasures.
	 * <p>
	 * After we obtain each index, we then get the object in the array list that corresponds with the index we stored and add them to an array list that'll 
	 * be returned. The result is a limited array list of objects that are in order based on their distance or duration from the origin.
	 * 
	 * @param allObjects an array list of objects
	 * @param allSortMeasures a double array of sort measures
	 * @return an array list of ordered object from allObjects based on the ascending order of allSortMeasures 
	 */
	private <T> ArrayList<T> sortWithMeasures (ArrayList<T> allObjects, double[] allSortMeasures)
	{
		int[] sortIndex;
		if (allObjects.size() < 3)
			sortIndex = new int[allObjects.size()];	
		else
			sortIndex = new int[3];
		Arrays.fill(sortIndex, 0);
		
		double[] tempAllSortMeasures = allSortMeasures.clone();
		
		for (int i = 0; i < sortIndex.length; i++)
		{
			/*
			 * to avoid a bug that occurs when the first element(s) of 
			 * allSortMeasures are -1. 
			 * 
			 * All the values in sortIndex are 0 by 
			 * default. If there is a 0 or -1 in allSortMeasures[0], then no 
			 * element at or after index 0 will be valued less than -1, which would cause 
			 * the constant selection of the first element from allSortMeasures.
			 */
			for (int j = 0; j < tempAllSortMeasures.length; j++)
			{
				if (tempAllSortMeasures[j] > 0)
				{
					sortIndex[i] = j;
					break;
				}
			}
			
			/*
			 * out of range, continue to next sort index
			 */
			if (sortIndex[i] == tempAllSortMeasures.length)
				continue;

			for (int j = 0; j < tempAllSortMeasures.length; j++)
			{
				/*
				 * if the value at the  j index of tempAllSortMeasures is greater than 0 
				 * and is less than the value at the sortIndex[i] index of tempAllSortMeasures, then 
				 * save the j index in sortIndex[i]
				 */				

				if (tempAllSortMeasures[j] <= 0)
					continue;
				
				if(tempAllSortMeasures[j] <= tempAllSortMeasures[sortIndex[i]])
					sortIndex[i] = j;
			}
			
			tempAllSortMeasures[sortIndex[i]] = -1;
		}
		
		ArrayList<T> allObjectsSort = new ArrayList<T>();
		
		for (int i = 0; i < sortIndex.length; i++)
		{
			if (sortIndex[i] != tempAllSortMeasures.length)
				allObjectsSort.add(allObjects.get(sortIndex[i]));
		}
		
		return allObjectsSort;
	}
	
	public ClosestStationsIntent (final LatLng origin, final Stop station, final Context c, final Activity a, 
			final String originState, final String originCounty)
	{
		CurrentLocationHandler.getLocationManager().removeUpdates(CurrentLocationHandler.gpsListener);
		CurrentLocationHandler.getLocationManager().removeUpdates(CurrentLocationHandler.networkListener);
		isNYbound = null;
		context = a;
		activity = a;
		final ProgressDialog myDialog = ProgressDialog.show(context, null, "Determining your closest entrance...", true);
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				/*
				 * don't continue if WTC or EXP and if it's the weekend
				 * only for 2014
				 */
				/*boolean wtc_exp_closed = false;
				int day2014 = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				int hour2014 = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
				int minute2014 = Calendar.getInstance().get(Calendar.MINUTE);
				if (station.getStopID().equalsIgnoreCase("26734") || station.getStopID().equalsIgnoreCase("26727"))
				{
					if (day2014 == 7 || day2014 == 1)
					{
						wtc_exp_closed = true;
					}
					else if (day2014 == 6)
					{
						if (hour2014 == 23 && minute2014 > 55)
						{
							wtc_exp_closed = true;
						}
					}
					else if (day2014 == 2)
					{
						if (hour2014 <= 4 && minute2014 < 45)
						{
							wtc_exp_closed = true;
						}
					}
				}
				if (wtc_exp_closed)
				{
					myDialog.dismiss();

					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{							
							new AlertDialog.Builder(c)
								.setTitle("Station Closed")
								.setMessage("This station is closed at this moment.")
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
				}*/
				
								
				/*
				 * If the origin is in New York or New Jersey, it'll get directions for it. Else, it'll 
				 * use all the stations so far. 
				 */
				String state = getOriginState(originState, originCounty);
				if (!state.equalsIgnoreCase("NY") && !state.equalsIgnoreCase("NJ"))
				{
					myDialog.dismiss();

					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{							
							new AlertDialog.Builder(c)
								.setTitle("You Should Consider Moving...")
								.setMessage("This features only works in NYC metro area counties served by rail operations (and other select areas).")
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
				
				/*
				 * get handicap entrances for station
				 * if there is none, and the user request handicap access, fail and alert
				 */				
				ArrayList<Entrance> tempEntrances = new ArrayList<Entrance>();
				if (ClosestStationFragment.getHandicapAccess() == null)
					tempEntrances = station.getEntranceList();
				else
				{
					tempEntrances = station.getHandicapAccessEntrances();
					for (int x = 0; x < tempEntrances.size();)
					{
						if (!tempEntrances.get(x).isHandicapAccessible(ClosestStationFragment.getHandicapAccess()))
						{
							tempEntrances.remove(x);
							continue;
						}
						x++;
					}
					
					if (tempEntrances.size() == 0)
					{
						myDialog.dismiss();

						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run() 
							{							
								new AlertDialog.Builder(c)
									.setTitle("No Handicap Entrances")
									.setMessage("There is no entrance at this station that suits your handicap needs.")
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
				}
													
				/*
				 * if station is harrison, 14th, or 23rd, ask the user
				 * to what terminal they're heading to
				 * 
				 * at these stations, the entrances do not serve both directions
				 */
				if (station.getStopID().equalsIgnoreCase("26729"))
				{
					tempEntrances = harrisonPlatform (tempEntrances, c, a);
				}
				else if (station.getStopID().equalsIgnoreCase("26722") || station.getStopID().equalsIgnoreCase("26723"))
				{						
					tempEntrances = platform14th23rd (tempEntrances, c, a);
				}
				
				tempEntrances = removeEntrancesTimeAndDay(tempEntrances);
				tempEntrances = removeEntrancesTransportMode(tempEntrances);
				
				/* get all necessary info */
				ArrayList<LatLng> allEntrancesLatLng = new ArrayList<LatLng>();
				for (int j = 0; j < tempEntrances.size(); j++)
				{
					allEntrancesLatLng.add(new LatLng(tempEntrances.get(j).getEntranceLocation()[0], tempEntrances.get(j).getEntranceLocation()[1]));
				}
				String entranceURL = Directions.getRouteMatrixURL(origin, allEntrancesLatLng);
				String entranceJSON = downloadJSON(entranceURL);
				double[] entranceSortMeasures = getAllSortMeasures(entranceJSON);
				
				/* obtain the closest entrance to the origin at this station */
				ArrayList<Entrance> closeE = sortWithMeasures(tempEntrances, entranceSortMeasures);
				final Entrance closestEntrance = closeE.get(0);
				
				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run() 
					{							
						myDialog.dismiss();
						new DisplayDirectionsIntent(context, activity, origin, closestEntrance);
					}
				});
			}
		}).start();
	}
	
	public ClosestStationsIntent (final double[] latlng, final Context c, final Activity a, final String originState, final String originCounty)
	{
		CurrentLocationHandler.getLocationManager().removeUpdates(CurrentLocationHandler.gpsListener);
		CurrentLocationHandler.getLocationManager().removeUpdates(CurrentLocationHandler.networkListener);
		final ProgressDialog myDialog = ProgressDialog.show(c, null, "Determining your three closest stations and its entrance...", true);
		isNYbound = null;
		context = c;
		activity = a;
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				Intent intent = new Intent (c, ClosestStationsActivity.class);
				ArrayList<Stop> allStations = null;
				LatLng origin = new LatLng(latlng[0],latlng[1]);
				
				/*
				 * if the user requires it, we get stations that have a specific handicap entrance. Else, we get all stations.
				 */
				if (ClosestStationFragment.getHandicapAccess() == null)
					allStations = MainActivity.getAllStations();
				else
					allStations = getHandicapAccessStations();
				
				/*
				 * If the origin is in New York or New Jersey, it'll remove all stations that are not in their state. Else, it'll 
				 * use all the stations so far. 
				 */
				String state = getOriginState(originState, originCounty);
				if (state.equalsIgnoreCase("NY") || state.equalsIgnoreCase("NJ"))
				{
					/*
					 * For testing purposes, I'll be removing the clause that'll
					 * remove stations not in the same state (or area) as the
					 * user and their starting location. This may become
					 * permanant if the time taken to find the <3 closest
					 * stations is not severe.
					 */
					/*for (int i = 0; i < allStations.size();)
					{
						if (!allStations.get(i).getStopState().equalsIgnoreCase(state))
						{	
							allStations.remove(i);
						}
						else
							i++;
					}*/
				}
				else
				{
					myDialog.dismiss();

					a.runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{							
							new AlertDialog.Builder(c)
								.setTitle("You Should Consider Moving...")
								.setMessage("This features only works in NYC metro area counties served by rail operations (and other select areas).")
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
				
				/*
				 * removes WTC and EXP if it's the weekend
				 * only for 2014
				 */
				// allStations = removeWtcExpWeekends2014 (allStations);
				
				/* obtain all necessary info for each station */
				ArrayList<LatLng> allStationsLatLng = new ArrayList<LatLng>();
				for (int i = 0; i < allStations.size(); i++)
					allStationsLatLng.add(new LatLng(allStations.get(i).getStopLocation()[0], allStations.get(i).getStopLocation()[1]));
				String url = Directions.getRouteMatrixURL(origin, allStationsLatLng);
				String json = downloadJSON(url);
				double[] sortMeasures = getAllSortMeasures(json);
				/* get the <=3 closest stations to the origin */ 
				ArrayList<Stop> allStationsSort = sortWithMeasures(allStations, sortMeasures);
				
				/*
				 * At this point, we have <=three closest stations. Now we will go through all appropriate 
				 * entrances at each station, get the closest one at each, and report those numbers as their 
				 * sort measures for the next activity.
				 */
				double[] closestSortMeasures = new double[allStationsSort.size()];
				ArrayList<Entrance> closestEntrances = new ArrayList<Entrance>();

				for (int i = 0; i < allStationsSort.size(); i++)
				{
					Stop tempStation = allStationsSort.get(i);
					
					ArrayList<Entrance> tempEntrances = new ArrayList<Entrance>();
					if (ClosestStationFragment.getHandicapAccess() == null)
						tempEntrances = tempStation.getEntranceList();
					else
					{
						tempEntrances = tempStation.getHandicapAccessEntrances();
						for (int x = 0; x < tempEntrances.size();)
						{
							if (!tempEntrances.get(x).isHandicapAccessible(ClosestStationFragment.getHandicapAccess()))
							{
								tempEntrances.remove(x);
								continue;
							}
							x++;
						}
					}
					
					/*
					 * if station is harrison, 14th, or 23rd, ask the user
					 * to what terminal they're heading to
					 * 
					 * at these stations, the entrances do not serve both directions
					 */
					if (tempStation.getStopID().equalsIgnoreCase("26729"))
					{
						tempEntrances = harrisonPlatform (tempEntrances, c, a);
					}
					else if (tempStation.getStopID().equalsIgnoreCase("26722") || tempStation.getStopID().equalsIgnoreCase("26723"))
					{						
						tempEntrances = platform14th23rd (tempEntrances, c, a);
					}
					
					tempEntrances = removeEntrancesTimeAndDay(tempEntrances);
					tempEntrances = removeEntrancesTransportMode(tempEntrances);
					
					/* get all necessary info */
					ArrayList<LatLng> allEntrancesLatLng = new ArrayList<LatLng>();
					for (int j = 0; j < tempEntrances.size(); j++)
					{
						allEntrancesLatLng.add(new LatLng(tempEntrances.get(j).getEntranceLocation()[0], tempEntrances.get(j).getEntranceLocation()[1]));
					}
					String entranceURL = Directions.getRouteMatrixURL(origin, allEntrancesLatLng);
					String entranceJSON = downloadJSON(entranceURL);
					double[] entranceSortMeasures = getAllSortMeasures(entranceJSON);
					
					/* obtain the closest entrance to the origin at this station */
					ArrayList<Entrance> closeE = sortWithMeasures(tempEntrances, entranceSortMeasures);
					
					/* 
					 * sort the array of entranceSortMeasures, the measure of distance or duration from the origin 
					 * to each entrance. Then obtain the smallest element that is not less than or equal to zero. 
					 * Because we have maintained an order, we can confidently say that the smallest element is the sort 
					 * measure for this station and the closest entrance saved above.
					 */
					
					int sortedIndex = 0;
					while (sortedIndex < entranceSortMeasures.length)
					{
						if (entranceSortMeasures[sortedIndex] <= 0)
							sortedIndex++;
						else
							break;
					}
					
					for (int j = 0; j < entranceSortMeasures.length && sortedIndex != entranceSortMeasures.length; j++)
					{
						if (entranceSortMeasures[j] > 0 && entranceSortMeasures[j] <= entranceSortMeasures[sortedIndex])
							sortedIndex = j;
					}
					
					if (sortedIndex == entranceSortMeasures.length)
						closestSortMeasures[i] = 0;					
					else		
					{
						closestSortMeasures[i] = entranceSortMeasures[sortedIndex];
						Entrance e = closeE.get(0);
						closestEntrances.add(e);
					}				
				}

				/*
				 * if ever the sortedIndex, when checking the entrances at each station, is zero 
				 * the value placed in closestSortMeasures is zero and the entrance is not added. This gives us an 
				 * unequal number of elements in closestEntrances and closestSortMeasures. If this is true, then those values in 
				 * closestSortMeasures that are zero are removed and the rest are put into a temp array the size of closestEntrances.
				 */
				if (closestEntrances.size() != closestSortMeasures.length)
				{
					double[] tempMeasures = new double[closestEntrances.size()];
					for (int x = 0, y = 0; x < closestSortMeasures.length && y < tempMeasures.length; x++)
					{
						if (closestSortMeasures[x] > 0)
						{
							tempMeasures[y] = closestSortMeasures[x];
							y++;
						}
					}
					closestSortMeasures = tempMeasures;
				}
				
				closestEntrances = sortWithMeasures(closestEntrances, closestSortMeasures);
				Arrays.sort(closestSortMeasures);
				
				intent.putExtra("Manual", latlng);
				intent.putExtra("closestSortMeasures", closestSortMeasures);
				intent.putParcelableArrayListExtra("closestEntrances", closestEntrances);
				
				myDialog.dismiss();
				a.startActivity(intent);
			}
		}).start();
	}
}
/**
 * 
 */
package com.shearosario.tothepathandback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * @author shea
 *
 */
public class PATHAlertsFragment extends Fragment
{
	private View rootView;
		
	private SimpleAdapter getAdvisories()
	{
		Document pathAlerts;
		Elements advisoryTimes = null;
		Elements advisoryTexts = null;
		
		/*
		 * Tries to get the HTML source code of the PATHAlerts webpage
		 * Adds the timestamp to each alert to an Elements object and what occured at that timestamp in another Elements object
		 * Ends thread if it fails and returns null
		 */
		try {
			pathAlerts = Jsoup.connect("http://www.paalerts.com/recent_pathalerts.aspx").get();
			advisoryTimes = pathAlerts.select("div.formText");
			advisoryTexts = pathAlerts.select("div.formField");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		/*
		 * Get each timestamp from its Elements object, and add them to a String ArrayList 
		 */
		ArrayList<String> timesArrayList = new ArrayList<String>();
		for (int i = 0; i < advisoryTimes.size(); i++)
		{
			String temp = advisoryTimes.get(i).text();
			if (!temp.isEmpty())
				timesArrayList.add(temp);
		}
		
		/*
		 * Get each advisory from its Elements object, and add them to a String ArrayList
		 */
		ArrayList<String> textsArrayList = new ArrayList<String>();
		for (int j = 0; j < advisoryTexts.size(); j++)
		{
			String temp = advisoryTexts.get(j).text();
			if (!temp.isEmpty())
				textsArrayList.add(temp);
		}
		
		/*
		 * If the size of both ArrayLists are the same then there is a timestamp for each advisory
		 * Match each timestamp with its advisory into a Arraylist of Maps
		 */
		if (timesArrayList.size() == textsArrayList.size())
		{
			ArrayList<Map<String, String>> list = convertToListItems(timesArrayList, textsArrayList);
			String[] from = {"time", "advisory"};
			int[] to = {android.R.id.text1, android.R.id.text2};
			SimpleAdapter adapter = new SimpleAdapter((Context) rootView.getContext(), list, 
					android.R.layout.simple_list_item_2, from, to);
			return adapter;
		}
		
		/*
		 * If there are no advisories then the webpage will announce that there are no advisories and it will display them 
		 * in the form of a timestamp on the page
		 */
		else if (timesArrayList.size() > 0)
		{
			ArrayList<String> tempTexts = new ArrayList<String>();
			for (int x = 0; x < timesArrayList.size(); x++)
			{
				tempTexts.add("");
			}
			
			ArrayList<Map<String, String>> list = convertToListItems(timesArrayList, tempTexts);
			String[] from = {"time", "advisory"};
			int[] to = {android.R.id.text1, android.R.id.text2};
			SimpleAdapter adapter = new SimpleAdapter((Context) rootView.getContext(), list, 
					android.R.layout.simple_list_item_2, from, to);
			return adapter;
		}
		
		return null;
	}
	
	private void setAdvisories(final Activity a, final TextView pathAlertsText)
	{				
		pathAlertsText.setText("Loading PATHAlerts...");
		new Thread(new Runnable()
        {
			@Override
			public void run() 
			{
				final SimpleAdapter adapter = getAdvisories();
				if (adapter == null)
				{
					a.runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							pathAlertsText.setText("Can't Load PATHAlerts (Press to refresh)");
						}
					});
				}
				else
				{
					a.runOnUiThread(new Runnable()
					{
						@Override
						public void run() 
						{
							pathAlertsText.setText("Recent PATHAlerts (Press to refresh)");
							ListView listview = (ListView) rootView.findViewById(R.id.list_PathAlerts); 
							listview.setAdapter(adapter);
						}
					});
				}
			}	
        }).start();	
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		 
        rootView = inflater.inflate(R.layout.fragment_pathalerts, container, false);
        final Activity a = (Activity) rootView.getContext();
        final TextView pathAlertsText = (TextView) rootView.findViewById(R.id.text_PathAlerts);
        
        setAdvisories(a, pathAlertsText);
        
        pathAlertsText.setOnClickListener(new OnClickListener()
        {
			@Override
			public void onClick(View v) 
			{
		        setAdvisories(a, pathAlertsText);
			}
        });
                
        return rootView;
    }
	
	private ArrayList<Map<String, String>> convertToListItems (ArrayList<String> times, ArrayList<String> advisories)
	{
		ArrayList<Map<String, String>> listItems = new ArrayList<Map<String, String>>(advisories.size());

		for (int i = 0; i < times.size() && i < advisories.size(); i++) 
		{
			HashMap<String, String> listItemMap = new HashMap<String, String>();
			listItemMap.put("time", times.get(i));
			listItemMap.put("advisory", advisories.get(i));
			listItems.add(listItemMap);
		}

		return listItems;
	}
}

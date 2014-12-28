/**
 * 
 */
package com.shearosario.tothepathandback.adapter;

import com.shearosario.tothepathandback.ClosestStationFragment;
import com.shearosario.tothepathandback.PATHAlertsFragment;
import com.shearosario.tothepathandback.SystemMapFragment;
import com.shearosario.tothepathandback.SystemSchedulesFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author shea
 * http://www.androidhive.info/2013/10/android-tab-layout-with-swipeable-views-1/
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Fragment getItem(int arg0) {
		switch (arg0) {
        	case 0:
        		return new ClosestStationFragment();
        	case 1:
        		return new SystemMapFragment();
        	case 2:
        		return new SystemSchedulesFragment();
        	case 3:
        		return new PATHAlertsFragment();
        }
		return null;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}
}

/**
 * 
 */
package com.shearosario.tothepathandback;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

/**
 * @author Jose Andres Rosario
 * http://www.androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater *
 */
public class AppRater {
    private final static String APP_TITLE = "To The PATH And Back";
    private final static String APP_PNAME = "com.shearosario.tothepathandback";
    
    // private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 3;
    
    public static void app_launched(Context mContext) 
    {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        
        if (prefs.getBoolean("dontshowagain", false)) 
        {
        	return ; 
        }
        
        SharedPreferences.Editor editor = prefs.edit();
        
        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }
        
        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT)
        {
            /*if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000))
            {
                showRateDialog(mContext, editor);
            }*/
        	
            showRateDialog(mContext, editor);
        }
        
        editor.commit();
    }   
    
    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) 
    {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Rate " + APP_TITLE);
        alertDialog.setMessage("If you enjoy using " + APP_TITLE + ", please take a moment to rate it. Thanks for your support!");

        alertDialog.setPositiveButton("Rate", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
            	if (editor != null) 
            	{
            		editor.putBoolean("dontshowagain", true);
            		editor.commit();
            	}
            	mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();            
            }
        });

        alertDialog.setNegativeButton("No Thanks", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
            	if (editor != null) 
            	{
            		editor.putBoolean("dontshowagain", true);
            		editor.commit();
            	}
            	dialog.dismiss();		
            }
        });

        alertDialog.setNeutralButton("Remind Me Later", new DialogInterface.OnClickListener() 
        {
            public void onClick(DialogInterface dialog, int which) 
            {
            	dialog.dismiss();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
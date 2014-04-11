package com.shearosario.tothepathandback;

import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ManualLocationText {

	private Context context;
	private String manual;
	private Activity activity;
	private EditText textManual;
	
	/**
	 * Assuming that the user typed something in the text field, once they press the search/ok button on their 
	 * keyboard we get whatever the user wrote and pass it on to ManualLocationHandler to be converted to latitude 
	 * and longitude. Else, the user will be informed by a toast notification that the text box is empty.
	 * 
	 * @param c context from the activity that called
	 * @param a the activity that called
	 */
	public ManualLocationText (Context c, Activity a)
	{
		context = c;
		activity = a; 
		
		textManual = (EditText) activity.findViewById(R.id.origin_manual);
		textManual.setOnEditorActionListener(new OnEditorActionListener() 
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
			{
				/*
				 * If the actionId is the Enter/Search key on the keyboard, both location listeners will stop 
				 * similar to MainActivity.SetOriginButton(View). Next the keyboard will disappear from the 
				 * screen. Then whatever is typed into the text view is stored. Finally, whatever is typed is 
				 * passed to ManualLocationHandler.createClosestStationsIntent(Context, Activity, String) in 
				 * order to get the geo-coordinates and pass to next activity.
				 */
				if (actionId == EditorInfo.IME_ACTION_SEARCH)
				{					
					InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
					manual = textManual.getText().toString();
					try 
					{
						if (!manual.isEmpty())
							ManualLocationHandler.createClosestStationsIntent(context, activity, manual);
						else
							(Toast.makeText(context, "No text has been inputted.", Toast.LENGTH_SHORT)).show();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					return true;
				}
				return false;
			}
		});
	}
}

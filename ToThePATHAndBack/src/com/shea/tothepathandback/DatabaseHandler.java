package com.shea.tothepathandback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.shea.tothepathandback.Entrance;
import com.shea.tothepathandback.Station;
import com.shea.tothepathandback.DatabaseContract.EntranceEntry;
import com.shea.tothepathandback.DatabaseContract.StationEntry;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "pathStations";
	// When updating database, make sure to increment/change DATABASE_VERSION
	// Default is 1, last used was 2
	// http://stackoverflow.com/questions/11601573/db-file-in-assets-folder-will-it-be-updated
	private static final int DATABASE_VERSION = 1;
	private static final String KEY_DB_VER = "db_ver";
	private SQLiteDatabase db;
	private final Context c;
	private String databasePath;
	
	@SuppressLint("SdCardPath")
	public DatabaseHandler(Context context)
	{
		super (context, DATABASE_NAME, null, DATABASE_VERSION);
		this.c = context;
		databasePath = "/data/data/" + context.getPackageName() + "/databases/";
	}
	
	public void createDatabase() throws IOException 
	{
		if (checkDatabase())
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c); // getPreferences(c)?
			int dbVersion = prefs.getInt(KEY_DB_VER, 1);
			if (DATABASE_VERSION != dbVersion)
			{
				File dbFile = new File(databasePath + DATABASE_NAME);
				if (!dbFile.delete())
					Log.d("update", "Unable to update database");
			}
		}
		
		if (!checkDatabase())
		{
			this.getReadableDatabase();
			try
			{
				copyDatabase();
			} catch (IOException e) {
				throw new Error ("Error copying database");
			}
		}
		
		this.openDatabase();
	}
	
	 private boolean checkDatabase() 
	 {  
		 File dbFile = new File(databasePath + DATABASE_NAME);  
		 return dbFile.exists();
	 }  
	
	@SuppressWarnings("resource")
	private void copyDatabase() throws IOException
	{
		InputStream myInput = c.getAssets().open(DATABASE_NAME);  
		String outFileName = databasePath + DATABASE_NAME;  
		OutputStream myOutput = new FileOutputStream(outFileName);  
		byte[] buffer = new byte[1024];  
		int length;  
		
		File file = new File(databasePath);
		if (!file.exists())
		{
			if (!file.mkdir())
			{
				Log.d("Database Path","Unable to create database directory");
				return;
			}
		}
		
		while ((length = myInput.read(buffer)) > 0) 
		{  
			myOutput.write(buffer, 0, length);  
		}  
		  
		myOutput.flush();  
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(KEY_DB_VER, DATABASE_VERSION);
		editor.commit();
		
		myOutput.close();  
		myInput.close();  
	}
	
	public void openDatabase()
	{
		String myPath = databasePath + DATABASE_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public Station getStation (String id)
	{
		String selectQuery = "SELECT * FROM " + StationEntry.TABLE_NAME + " WHERE " + StationEntry.COLUMN_STATIONID + " = ?";
		Cursor c = db.rawQuery(selectQuery, new String[] {id});
		
		if (c != null)
			c.moveToFirst();

		Station s = new Station(c.getString(1),c.getString(2),c.getString(5),c.getString(6),c.getDouble(3),c.getDouble(4));
		
		return s;
	}
	
	public Station[] getAllStations()
	{
		String selectQuery = "SELECT * FROM " + StationEntry.TABLE_NAME;
		Cursor c = db.rawQuery(selectQuery, null);
		Station[] stationList = new Station[c.getCount()];		
		
		if (c.moveToFirst())
		{
			int i = 0;
			do
			{
				Station s = new Station(c.getString(1),c.getString(2),c.getString(5),c.getString(6),c.getDouble(3),c.getDouble(4));
				stationList[i] = s;
				i++;
			} while (c.moveToNext() && i < c.getCount());
		}
		
		return stationList;
	}
	
	public Entrance[] getAllEntrancesForStation (Station s)
	{
		String selectQuery = "SELECT * FROM " + EntranceEntry.TABLE_NAME + " WHERE " + EntranceEntry.COLUMN_STATIONID + " = ?";
		Cursor c = db.rawQuery(selectQuery, new String[] {s.getStationID()});
		Entrance[] entranceList = new Entrance[c.getCount()];

		if (c.moveToFirst())
		{
			int i = 0;
			do
			{
				Entrance e = new Entrance (c.getString(0), c.getString(1), c.getString(2), c.getString(5), c.getInt(6),
						c.getInt(7), c.getInt(8), c.getInt(9), c.getDouble(3), c.getDouble(4));
				entranceList[i] = e;
				i++;
			} while (c.moveToNext() && i < c.getCount());
		}
		
		return entranceList;
	}
}

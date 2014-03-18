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

/**
 * Handles all the necessary steps to create, open, update, and save the databases necessary for 
 * this project. It will open a database file already available in the project's assets folder, and 
 * will save that file as well in to the app's data folder on the device. The necessary queries to 
 * obtain the stations and their entrances are also available as methods here.  
 * 
 * @author shea
 */
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
	
	/**
	 * Constructor for our database handler. Saves copy of context from MainActivity and 
	 * sets the save path for our database file. 
	 * 
	 * @param context - Context from MainActivity that'll be copied here 
	 */
	@SuppressLint("SdCardPath")
	public DatabaseHandler(Context context)
	{
		super (context, DATABASE_NAME, null, DATABASE_VERSION);
		this.c = context;
		databasePath = "/data/data/" + context.getPackageName() + "/databases/";
	}
	
	/**
	 * If a database already exists, we'll check if the database has been updated based on the constant 
	 * DATABASE_VERSION. If the database version has not changed, then the current database file will open. 
	 * Else if the database version has changed, the saved file will be deleted. Finally, if the old database 
	 * file was deleted or a database never existed, a new one will be saved based on what is read from the 
	 * one stored in our assets folder. 
	 * 
	 * @see #checkDatabase()
	 * @see #copyDatabase()
	 * @see #openDatabase()
	 * @throws IOException - Error copying database
	 */
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
	
	
	/**
	 * Using the database path created in the constructor, determine if database already exists on file.
	 * 
	 * @return
	 */
	private boolean checkDatabase() 
	 {  
		 File dbFile = new File(databasePath + DATABASE_NAME);  
		 return dbFile.exists();
	 }  
	
	
	/**
	 * Attempts to use a stream buffer to input database from asserts file, create 
	 * the permanent file in the data folder stored on the user's device, and write 
	 * from the buffer to the file. Will also save database version. 
	 * 
	 * @throws IOException if stream cannot be opened 
	 */
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

	
	/**
	 * Create a complete path to database file in app's data folder on device and open it as 
	 * read only. Saved in SQLiteDatabase field.  
	 */
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
	
	/**
	 * Queries the station table for the information associated with the given unique station ID
	 * 
	 * @param id - Unique station identification
	 * @return Station object with same unique ID
	 */
	public Station getStation (String id)
	{
		String selectQuery = "SELECT * FROM " + StationEntry.TABLE_NAME + " WHERE " + StationEntry.COLUMN_STATIONID + " = ?";
		Cursor c = db.rawQuery(selectQuery, new String[] {id});
		
		if (c != null)
			c.moveToFirst();

		Station s = new Station(c.getString(1),c.getString(2),c.getString(5),c.getString(6),c.getDouble(3),c.getDouble(4));
		
		return s;
	}
	
	/**
	 * Query the stations table for all stations, all rows.
	 * 
	 * @return Station array of all in stations table
	 */
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
	
	/**
	 * Query the entrances tables for all entrances that share their station ID with the unique ID 
	 * of the passed station.
	 * 
	 * @param s - Station used for query
	 * @return array of Entrances corresponding to the passed Station based on station ID
	 */
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

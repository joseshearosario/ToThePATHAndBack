package com.shearosario.tothepathandback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import com.shearosario.tothepathandback.DatabaseContract.*;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Handles all the necessary steps to create, open, update, and save the
 * databases. It will open a database file already available in the project's
 * assets folder, and will save that file as well in to the app's data folder on
 * the device. The necessary queries to obtain the stations and their entrances
 * are available as methods here.
 * 
 * @author Jose Andres Rosario
 */
public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "pathStations";
	/*
	 * When updating database, make sure to increment/change DATABASE_VERSION
	 * http
	 * ://stackoverflow.com/questions/116015734/db-file-in-assets-folder-will
	 * -it-be-updated
	 */
	private static final int DATABASE_VERSION = 5;
	private static final String KEY_DB_VER = "db_ver";
	private SQLiteDatabase db;
	private Context c;
	private String databasePath;

	/**
	 * Constructor for our database handler. Saves copy of context from
	 * MainActivity and sets the save path for our database file.
	 * 
	 * @param context
	 *            Context that'll be used here
	 */
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		c = context;
		databasePath = c.getFilesDir().getParentFile().getPath()
				+ "/databases/";
		try {
			createDatabase();
		} catch (IOException e) {
			Log.d("Database", "Database: Not created/opened");
			e.printStackTrace();
		}
	}

	/**
	 * If a database already exists, we'll check if the database has been
	 * updated based on the constant DATABASE_VERSION. If the database version
	 * has not changed, then the current database file will open. Else if the
	 * database version has changed, the saved file will be deleted and a new
	 * database will be created. If a database never existed, a new one will be
	 * saved based on what is read from the one stored in our assets folder.
	 * 
	 * @see #checkDatabase()
	 * @see #copyDatabase()
	 * @see #openDatabase()
	 * @throws IOException
	 *             - Error copying database
	 */
	public void createDatabase() throws IOException {
		boolean newDatabase = false;
		if (checkDatabase()) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(c);
			int dbVersion = prefs.getInt(KEY_DB_VER, 1);
			if (DATABASE_VERSION != dbVersion) {
				File dbFile = new File(databasePath + DATABASE_NAME);
				if (!dbFile.delete())
					Log.d("update", "Unable to update database");
				else {
					this.getReadableDatabase();
					try {
						copyDatabase();
						newDatabase = true;
					} catch (IOException e) {
						throw new Error("Error copying database");
					}
				}
			}
		} else {
			this.getReadableDatabase();
			try {
				copyDatabase();
				newDatabase = true;
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}

		this.openDatabase(newDatabase);
	}

	/**
	 * Using the database path created in the constructor, determine if database
	 * already exists on file.
	 * 
	 * @return true if exists
	 */
	private boolean checkDatabase() {
		File dbFile = new File(databasePath + DATABASE_NAME);
		return dbFile.exists();
	}

	/**
	 * Attempts to use a stream buffer to input database from assets file,
	 * create the permanent file in the data folder stored on the user's device,
	 * and write from the buffer to the file. Will also save database version.
	 * 
	 * @throws IOException
	 *             - stream cannot be opened
	 */
	private void copyDatabase() throws IOException {
		InputStream myInput = c.getAssets().open(DATABASE_NAME);
		String outFileName = databasePath + DATABASE_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;

		/*
		 * Confirm that a path to the database file exists, or create one if
		 * possible.
		 */
		File file = new File(databasePath);
		boolean dirExist = true;
		if (!file.exists()) {
			if (!file.mkdir()) {
				Log.d("Database Path", "Unable to create database directory");
				dirExist = false;
			}
		}

		if (dirExist) {
			/*
			 * Write database to buffer from APK to directory
			 */
			while ((length = myInput.read(buffer)) > 0) {
				myOutput.write(buffer, 0, length);
			}

			myOutput.flush();

			/*
			 * Create a shared preference that will maintain the version number
			 * of the database
			 */
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(c);
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(KEY_DB_VER, DATABASE_VERSION);
			editor.commit();

			myOutput.close();
			myInput.close();
		}
	}

	/**
	 * Create a complete path to database file in app's data folder on device
	 * and open it as read only. Saved in SQLiteDatabase field.
	 * 
	 * @param newDatabase
	 *            if a new database was copied from assets
	 */
	private void openDatabase(boolean newDatabase) {
		String myPath = databasePath + DATABASE_NAME;
		db = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
		if (newDatabase)
			repairDatabase();
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
	 * Query the stations table for all stations (all rows).
	 * 
	 * @return all stations in table in a Station ArrayList
	 */
	public ArrayList<Stop> getAllStations() {
		String selectQuery = "SELECT * FROM " + StopEntry.TABLE_NAME;
		Cursor stationsCursor = db.rawQuery(selectQuery, null);
		ArrayList<Stop> stationList = new ArrayList<Stop>();

		if (stationsCursor.moveToFirst()) {
			int i = 0;
			do {
				/*
				 * Get all entrances for this stop
				 */
				ArrayList<Entrance> entrances = new ArrayList<Entrance>(
						getAllEntrancesForStop(stationsCursor.getString(0)));
				
				Stop s = new Stop(stationsCursor.getString(0),
						stationsCursor.getString(2),
						stationsCursor.getDouble(4),
						stationsCursor.getDouble(5), entrances);
				stationList.add(s);
				i++;
			} while (stationsCursor.moveToNext()
					&& i < stationsCursor.getCount());
		}

		return stationList;
	}

	/**
	 * Query the entrances table for all entrances for a stop.
	 * 
	 * @param stop_id
	 *            identification for the station
	 * @return an arraylist of entrances at the station
	 */
	private ArrayList<Entrance> getAllEntrancesForStop(String stop_id) {
		String selectQuery = "SELECT * FROM " + EntranceEntry.TABLE_NAME
				+ " WHERE " + EntranceEntry.COLUMN_STOPID + " = ?";
		Cursor entranceCursor = db.rawQuery(selectQuery,
				new String[] { stop_id });
		ArrayList<Entrance> entranceList = new ArrayList<Entrance>();

		if (entranceCursor.moveToFirst()) {
			int i = 0;
			do {
				Entrance e = new Entrance(entranceCursor.getString(0),
						entranceCursor.getString(1),
						entranceCursor.getDouble(2),
						entranceCursor.getDouble(3),
						entranceCursor.getString(4), entranceCursor.getInt(5),
						entranceCursor.getInt(6), entranceCursor.getInt(7),
						entranceCursor.getInt(8));
				entranceList.add(e);
				i++;
			} while (entranceCursor.moveToNext()
					&& i < entranceCursor.getCount());
		}

		return entranceList;
	}

	/**
	 * To be used when database has been updated (database version has
	 * incremented). Edits the stop_times and trips table provided by the Port
	 * Authority in order to be 'more compatible' with the queries in
	 * DatabaseHandler. Changes '33rd via Hoboken' to '33rd Street via Hoboken',
	 * as well as change the arrival and departure times from '24' to '00' if
	 * they begin the next day.
	 */
	private void repairDatabase() {
		/*
		 * Change the hour on a stop_time in trips that start at the beginning
		 * of one day, and continue in the same day that used 24 instead on 00
		 */
		String selectQuery = "UPDATE " + StopTimeEntry.TABLE_NAME + " SET "
				+ StopTimeEntry.COLUMN_DEPARTURETIME + "=replace("
				+ StopTimeEntry.COLUMN_DEPARTURETIME + ", '24', '00')"
				+ " WHERE " + StopTimeEntry.COLUMN_DEPARTURETIME
				+ " LIKE '24%' AND " + StopTimeEntry.COLUMN_TRIPID
				+ " IN (SELECT " + StopTimeEntry.COLUMN_TRIPID + " FROM "
				+ StopTimeEntry.TABLE_NAME + " WHERE "
				+ StopTimeEntry.COLUMN_STOPSEQUENCE + "=1" + " AND "
				+ StopTimeEntry.COLUMN_DEPARTURETIME + ">='24:00:00')";
		db.rawQuery(selectQuery, null);

		selectQuery = "UPDATE " + StopTimeEntry.TABLE_NAME + " SET "
				+ StopTimeEntry.COLUMN_ARRIVALTIME + "=replace("
				+ StopTimeEntry.COLUMN_ARRIVALTIME + ", '24', '00')"
				+ " WHERE " + StopTimeEntry.COLUMN_ARRIVALTIME
				+ " LIKE '24%' AND " + StopTimeEntry.COLUMN_TRIPID
				+ " IN (SELECT " + StopTimeEntry.COLUMN_TRIPID + " FROM "
				+ StopTimeEntry.TABLE_NAME + " WHERE "
				+ StopTimeEntry.COLUMN_STOPSEQUENCE + "=1" + " AND "
				+ StopTimeEntry.COLUMN_ARRIVALTIME + ">='24:00:00')";
		db.rawQuery(selectQuery, null);

		/*
		 * Add 'Street' to '33rd via Hoboken' trip headsign, in order to match
		 * with '33rd Street' terminal
		 */
		selectQuery = "UPDATE " + TripEntry.TABLE_NAME + " SET "
				+ TripEntry.COLUMN_TRIPHEADSIGN
				+ "='33rd Street via Hoboken' WHERE "
				+ TripEntry.COLUMN_TRIPHEADSIGN + "='33rd via Hoboken'";
		db.rawQuery(selectQuery, null);
	}

	/**
	 * Get all the times that a train will stop at the station for the rest of
	 * the day, or all of those that will stop the next day.
	 * 
	 * @param stop_id
	 *            the stop selected on the map
	 * @param date
	 *            the date to examine stop times
	 * @param nextDay
	 *            true if the date passed is the next calendar day of the year
	 * @return all stop times after date (or all for the next day) that is
	 *         associated with this stop
	 */
	public TimeStampsAndRoutes getStopTimesForStop(String stop_id,
			java.util.Calendar date, boolean nextDay) {
		TimeStampsAndRoutes allTimeStampsAndRoutes;
		String selectStopTimesQuery = null;
		Cursor stCursor = null;

		/*
		 * Check if the date passed through parameter follows a different
		 * schedule than the one use on that day of the week. Save to a cursor
		 * the service that will be followed on that day.
		 */
		String selectCalendarDateQuery = "SELECT * FROM "
				+ CalendarDateEntry.TABLE_NAME + " WHERE "
				+ CalendarDateEntry.COLUMN_DATE + "=?" + " AND "
				+ CalendarDateEntry.COLUMN_EXCEPTIONTYPE + "=1";
		Cursor calendarDateCursor = db
				.rawQuery(
						selectCalendarDateQuery,
						new String[] { Integer.toString(date
								.get(java.util.Calendar.YEAR))
								+ Integer.toString(date
										.get(java.util.Calendar.MONTH) + 1)
								+ Integer.toString(date
										.get(java.util.Calendar.DATE)) });

		/*
		 * If there is a special service that runs on the date, run a query that
		 * obtains all the stop times, trip information, and route information
		 * that stop at this station. Else get the date's day of week, and run a
		 * query that returns the same information as above as well as finding
		 * the service that run on that day of the week.
		 */
		if (calendarDateCursor.moveToFirst()) {
			int i = 0;
			do {
				String serviceID = calendarDateCursor.getString(0);
				/*
				 * To temporary hold the results of query
				 */
				Cursor tempCursor = null;

				selectStopTimesQuery = "SELECT DISTINCT t1."
						+ StopTimeEntry.COLUMN_DEPARTURETIME + ", t3."
						+ RouteEntry.COLUMN_ROUTELONGNAME + ", t2."
						+ TripEntry.COLUMN_TRIPHEADSIGN + ", t2."
						+ TripEntry.COLUMN_DIRECTIONID + ", t2."
						+ TripEntry.COLUMN_ROUTEID + ", t3."
						+ RouteEntry.COLUMN_ROUTECOLOR + " FROM "
						+ StopTimeEntry.TABLE_NAME + " AS t1 JOIN "
						+ TripEntry.TABLE_NAME + " AS t2 ON t2."
						+ TripEntry.COLUMN_TRIPID + "=t1."
						+ StopTimeEntry.COLUMN_TRIPID + " AND t1."
						+ StopTimeEntry.COLUMN_STOPID + "=?" + " AND t2."
						+ TripEntry.COLUMN_SERVICEID + "=?" + " JOIN "
						+ RouteEntry.TABLE_NAME + " AS t3" + " ON t2."
						+ TripEntry.COLUMN_ROUTEID + "=t3."
						+ RouteEntry.COLUMN_ROUTEID + " JOIN "
						+ StopEntry.TABLE_NAME + " AS t5" + " ON t1."
						+ StopTimeEntry.COLUMN_STOPID + "=t5."
						+ StopEntry.COLUMN_STOPID + " AND t5."
						+ StopEntry.COLUMN_STOPNAME + " NOT LIKE t2."
						+ TripEntry.COLUMN_TRIPHEADSIGN + "||'%'" + " AND t2."
						+ TripEntry.COLUMN_TRIPHEADSIGN + " NOT LIKE t5."
						+ StopEntry.COLUMN_STOPNAME + "||'%'" + " ORDER BY t3."
						+ RouteEntry.COLUMN_ROUTEID + ", t2."
						+ TripEntry.COLUMN_DIRECTIONID + ", t1."
						+ StopTimeEntry.COLUMN_DEPARTURETIME;
				tempCursor = db.rawQuery(selectStopTimesQuery, new String[] {
						stop_id, serviceID });
				
				/*
				 * To hold the rows that will be added from tempCursor 
				 */
				MatrixCursor tempMatrixCursor = new MatrixCursor(tempCursor.getColumnNames());
				if (tempCursor.moveToFirst())
				{
					int j = 0;
					do
					{
						tempMatrixCursor.addRow(new String[] {
								tempCursor.getString(0),
								tempCursor.getString(1),
								tempCursor.getString(2),
								tempCursor.getString(3),
								tempCursor.getString(4),
								tempCursor.getString(5) });
						j++;
					} while (tempCursor.moveToNext() && j < tempCursor.getCount());
				}
				
				/*
				 * Merge what already exists in stCursor with what is in tempMatrixCursor
				 */
				stCursor = new MergeCursor(new Cursor[] {tempMatrixCursor, stCursor});

				i++;
			} while (calendarDateCursor.moveToNext()
					&& i < calendarDateCursor.getCount());
		} else {
			/*
			 * Save in a variable the name of the column representative of the
			 * day of the week selected. To be used to find the id of the
			 * service(s) that run on that day of the week.
			 */
			String column = null;
			switch (date.get(java.util.Calendar.DAY_OF_WEEK)) {
			case (1):
				column = CalendarEntry.COLUMN_SUNDAY;
				break;
			case (2):
				column = CalendarEntry.COLUMN_MONDAY;
				break;
			case (3):
				column = CalendarEntry.COLUMN_TUESDAY;
				break;
			case (4):
				column = CalendarEntry.COLUMN_WEDNESDAY;
				break;
			case (5):
				column = CalendarEntry.COLUMN_THURSDAY;
				break;
			case (6):
				column = CalendarEntry.COLUMN_FRIDAY;
				break;
			case (7):
				column = CalendarEntry.COLUMN_SATURDAY;
				break;
			}

			selectStopTimesQuery = "SELECT DISTINCT t1."
					+ StopTimeEntry.COLUMN_DEPARTURETIME + ", t3."
					+ RouteEntry.COLUMN_ROUTELONGNAME + ", t2."
					+ TripEntry.COLUMN_TRIPHEADSIGN + ", t2."
					+ TripEntry.COLUMN_DIRECTIONID + ", t2."
					+ TripEntry.COLUMN_ROUTEID + ", t3."
					+ RouteEntry.COLUMN_ROUTECOLOR + " FROM "
					+ StopTimeEntry.TABLE_NAME + " AS t1" + " JOIN "
					+ TripEntry.TABLE_NAME + " AS t2" + " ON t2."
					+ TripEntry.COLUMN_TRIPID + "=t1."
					+ StopTimeEntry.COLUMN_TRIPID + " AND t1."
					+ StopTimeEntry.COLUMN_STOPID + "=?" + " JOIN "
					+ CalendarEntry.TABLE_NAME + " AS t4" + " ON t2."
					+ TripEntry.COLUMN_SERVICEID + "=t4."
					+ CalendarEntry.COLUMN_SERVICEID + " AND t4." + column
					+ "=1" + " JOIN " + RouteEntry.TABLE_NAME + " AS t3"
					+ " ON t2." + TripEntry.COLUMN_ROUTEID + "=t3."
					+ RouteEntry.COLUMN_ROUTEID + " JOIN "
					+ StopEntry.TABLE_NAME + " AS t5" + " ON t1."
					+ StopTimeEntry.COLUMN_STOPID + "=t5."
					+ StopEntry.COLUMN_STOPID + " AND t5."
					+ StopEntry.COLUMN_STOPNAME + " NOT LIKE t2."
					+ TripEntry.COLUMN_TRIPHEADSIGN + "||'%'" + " AND t2."
					+ TripEntry.COLUMN_TRIPHEADSIGN + " NOT LIKE t5."
					+ StopEntry.COLUMN_STOPNAME + "||'%'" + " ORDER BY t3."
					+ RouteEntry.COLUMN_ROUTEID + ", t2."
					+ TripEntry.COLUMN_DIRECTIONID + ", t1."
					+ StopTimeEntry.COLUMN_DEPARTURETIME;
			stCursor = db.rawQuery(selectStopTimesQuery,
					new String[] { stop_id });
		}

		ArrayList<StopTime> allTimeStamps = new ArrayList<StopTime>();
		ArrayList<String> allRoutesWithRouteColor = new ArrayList<String>();

		if (stCursor.moveToFirst()) {
			int i = 0;

			do {
				String[] departure_time = stCursor.getString(0).split(":");

				/*
				 * Change the hour from 24 to 00, in order for it to be properly
				 * sorted later
				 */
				if (departure_time[0].equals("24"))
					departure_time[0] = "00";

				GregorianCalendar stopG = new GregorianCalendar(
						date.get(java.util.Calendar.YEAR),
						date.get(java.util.Calendar.MONTH),
						date.get(java.util.Calendar.DAY_OF_MONTH),
						Integer.parseInt(departure_time[0]),
						Integer.parseInt(departure_time[1]));

				/*
				 * If nextDay is true, it means that this query is for all stop
				 * times for a certain day regardless of the current time of
				 * day. This is intended to be used to get the stop times of the
				 * next day for the selected station.
				 * 
				 * Else if the passed date is not the next day and the stop time
				 * is after the passed date and time, add the stop time and trip
				 * information.
				 */
				if (nextDay) {
					StopTime timeStamp = new StopTime(
							Integer.parseInt(departure_time[0]),
							departure_time[1],
							stopG.get(java.util.Calendar.AM_PM),
							stCursor.getString(1), stCursor.getString(2),
							stCursor.getString(3), stCursor.getString(4));

					String routeWithRouteColor = timeStamp.getRoute_id() + ","
							+ stCursor.getString(5);

					if (!allRoutesWithRouteColor.contains(routeWithRouteColor)) {
						allRoutesWithRouteColor.add(routeWithRouteColor);
					}

					allTimeStamps.add(timeStamp);
				} else if (!nextDay
						&& stopG.after(date)) {
					StopTime timeStamp = new StopTime(
							Integer.parseInt(departure_time[0]),
							departure_time[1],
							stopG.get(java.util.Calendar.AM_PM),
							stCursor.getString(1), stCursor.getString(2),
							stCursor.getString(3), stCursor.getString(4));

					String routeWithRouteColor = timeStamp.getRoute_id() + ","
							+ stCursor.getString(5);

					if (!allRoutesWithRouteColor.contains(routeWithRouteColor)) {
						allRoutesWithRouteColor.add(routeWithRouteColor);
					}

					allTimeStamps.add(timeStamp);
				}

				i++;
			} while (stCursor.moveToNext() && i < stCursor.getCount());
		}

		allTimeStampsAndRoutes = new TimeStampsAndRoutes(
				allRoutesWithRouteColor.toArray(new String[0]),
				allTimeStamps.toArray(new StopTime[0]));

		return allTimeStampsAndRoutes;
	}

	/**
	 * Return all calendar dates that have special schedules on that day.
	 * Usually this is in regards to holidays.
	 * 
	 * @return list of all calendar dates that use a different schedule from the
	 *         day of the week
	 */
	public ArrayList<String> getDistinctCalendarDates() {
		ArrayList<String> allCalendarDates = new ArrayList<String>();
		allCalendarDates.add("Monday");
		allCalendarDates.add("Tuesday");
		allCalendarDates.add("Wednesday");
		allCalendarDates.add("Thursday");
		allCalendarDates.add("Friday");
		allCalendarDates.add("Saturday");
		allCalendarDates.add("Sunday");

		String selectQuery = "SELECT DISTINCT " + CalendarDateEntry.COLUMN_DATE
				+ " FROM " + CalendarDateEntry.TABLE_NAME;
		Cursor calendarDates = db.rawQuery(selectQuery, null);

		if (calendarDates.moveToFirst()) {
			int i = 0;
			do {
				String cD = calendarDates.getString(0);
				String cD_Year = cD.substring(0, 4);
				String cD_Month = cD.substring(4, 6);
				String cD_Date = cD.substring(6, 8);
				String cD_Final;

				/*
				 * Typically, there is one major holiday every month of the
				 * year. This should be revisited every year when the new
				 * schedules are made available.
				 */
				if (cD_Month.equalsIgnoreCase("02")) {
					cD_Final = "(" + cD_Month + "/" + cD_Date + "/" + cD_Year
							+ ") Presidents' Day";
				} else if (cD_Month.equalsIgnoreCase("05")) {
					cD_Final = "(" + cD_Month + "/" + cD_Date + "/" + cD_Year
							+ ") Memorial Day";
				} else if (cD_Month.equalsIgnoreCase("07")) {
					cD_Final = "(" + cD_Month + "/" + cD_Date + "/" + cD_Year
							+ ") Independence Day";
				} else if (cD_Month.equalsIgnoreCase("09")) {
					cD_Final = "(" + cD_Month + "/" + cD_Date + "/" + cD_Year
							+ ") Labor Day";
				} else if (cD_Month.equalsIgnoreCase("11")) {
					cD_Final = "(" + cD_Month + "/" + cD_Date + "/" + cD_Year
							+ ") Thanksgiving Day";
				} else if (cD_Month.equalsIgnoreCase("12")) {
					cD_Final = "(" + cD_Month + "/" + cD_Date + "/" + cD_Year
							+ ") Christmas Day";
				} else {
					cD_Final = "Holiday schedule for " + cD_Month + "/"
							+ cD_Date + "/" + cD_Year;
				}

				allCalendarDates.add(cD_Final);

				i++;
			} while (calendarDates.moveToNext() && i < calendarDates.getCount());
		}

		return allCalendarDates;
	}

	public ArrayList<String> getSchedule(Stop stop0, Stop stop1, String date) {
		ArrayList<String> serviceID_Added = new ArrayList<String>();
		ArrayList<String> allStopTimes = new ArrayList<String>();

		if (date.equalsIgnoreCase("mon")) {
			date = CalendarEntry.COLUMN_MONDAY;
		} else if (date.equalsIgnoreCase("tue")) {
			date = CalendarEntry.COLUMN_TUESDAY;
		} else if (date.equalsIgnoreCase("wed")) {
			date = CalendarEntry.COLUMN_WEDNESDAY;
		} else if (date.equalsIgnoreCase("thu")) {
			date = CalendarEntry.COLUMN_THURSDAY;
		} else if (date.equalsIgnoreCase("fri")) {
			date = CalendarEntry.COLUMN_FRIDAY;
		} else if (date.equalsIgnoreCase("sat")) {
			date = CalendarEntry.COLUMN_SATURDAY;
		} else if (date.equalsIgnoreCase("sun")) {
			date = CalendarEntry.COLUMN_SUNDAY;
		} else // for holiday schedules
		{
			String selectCalendarDateQuery = "SELECT * FROM "
					+ CalendarDateEntry.TABLE_NAME + " WHERE "
					+ CalendarDateEntry.COLUMN_DATE + "=?";
			Cursor calendarDateCursor = db.rawQuery(selectCalendarDateQuery,
					new String[] { date });

			if (calendarDateCursor.moveToFirst()) {
				int i = 0;
				do {
					if (calendarDateCursor.getString(2).equalsIgnoreCase("1")) {
						serviceID_Added.add(calendarDateCursor.getString(0));
					}

					i++;
				} while (calendarDateCursor.moveToNext()
						&& i < calendarDateCursor.getCount());
			}
		}

		String selectQuery = null;
		Cursor stCursor = null;

		if (serviceID_Added.isEmpty()) {
			selectQuery = "SELECT stop0."
					+ TripEntry.COLUMN_TRIPID
					+ ", stop0."
					+ TripEntry.COLUMN_ROUTEID
					+ ", stop0."
					+ TripEntry.COLUMN_TRIPHEADSIGN
					+ ", stop0."
					+ StopTimeEntry.COLUMN_DEPARTURETIME
					+ ", stop1."
					+ StopTimeEntry.COLUMN_ARRIVALTIME
					+ " FROM (SELECT t1."
					+ TripEntry.COLUMN_ROUTEID
					+ ", t1."
					+ TripEntry.COLUMN_TRIPID
					+ ", t2."
					+ StopTimeEntry.COLUMN_STOPSEQUENCE
					+ ", t2."
					+ StopTimeEntry.COLUMN_DEPARTURETIME
					+ ", t1."
					+ TripEntry.COLUMN_TRIPHEADSIGN
					+ " FROM "
					+ TripEntry.TABLE_NAME
					+ " AS t1"
					+ " JOIN "
					+ StopTimeEntry.TABLE_NAME
					+ " AS t2"
					+ " ON t2."
					+ StopTimeEntry.COLUMN_TRIPID
					+ "=t1."
					+ TripEntry.COLUMN_TRIPID
					+ " AND t2."
					+ StopTimeEntry.COLUMN_STOPID
					+ "=?" // first stop id
					// + " AND t1." + TripEntry.COLUMN_SERVICEID +
					// "=?) AS stop0" //service id, for calendar_dates
					+ " JOIN "
					+ CalendarEntry.TABLE_NAME
					+ " AS t3"
					+ " ON t1."
					+ TripEntry.COLUMN_SERVICEID
					+ "=t3."
					+ CalendarEntry.COLUMN_SERVICEID
					+ " AND t3."
					+ date
					+ "=1"
					+ " ORDER BY t2."
					+ StopTimeEntry.COLUMN_DEPARTURETIME
					+ ") AS stop0"
					+ " JOIN (SELECT t1."
					+ TripEntry.COLUMN_ROUTEID
					+ ", t1."
					+ TripEntry.COLUMN_TRIPID
					+ ", t2."
					+ StopTimeEntry.COLUMN_STOPSEQUENCE
					+ ", t2."
					+ StopTimeEntry.COLUMN_ARRIVALTIME
					+ " FROM "
					+ TripEntry.TABLE_NAME
					+ " AS t1"
					+ " JOIN "
					+ StopTimeEntry.TABLE_NAME
					+ " AS t2"
					+ " ON t2."
					+ StopTimeEntry.COLUMN_TRIPID
					+ "=t1."
					+ TripEntry.COLUMN_TRIPID
					+ " AND t2."
					+ StopTimeEntry.COLUMN_STOPID
					+ "=?" // second stop id
					// + " AND t1." + TripEntry.COLUMN_SERVICEID +
					// "=?) AS stop1" //service id, for calendar_dates
					+ " JOIN " + CalendarEntry.TABLE_NAME + " AS t3"
					+ " ON t1." + TripEntry.COLUMN_SERVICEID + "=t3."
					+ CalendarEntry.COLUMN_SERVICEID + " AND t3." + date + "=1"
					+ " ORDER BY t2." + StopTimeEntry.COLUMN_ARRIVALTIME
					+ ") AS stop1" + " ON stop0." + TripEntry.COLUMN_TRIPID
					+ "=stop1." + TripEntry.COLUMN_TRIPID + " AND stop0."
					+ StopTimeEntry.COLUMN_STOPSEQUENCE + "<stop1."
					+ StopTimeEntry.COLUMN_STOPSEQUENCE + " ORDER BY stop0."
					+ StopTimeEntry.COLUMN_DEPARTURETIME;
			;

			stCursor = db.rawQuery(selectQuery,
					new String[] { stop0.getStopID(), stop1.getStopID() });

			// for trips that don't require transfers
			if (stCursor.getCount() != 0) {
				if (stCursor.moveToFirst()) {
					int j = 0;
					do {
						String departure_time = stCursor.getString(3);
						String arrival_time = stCursor.getString(4);
						String trip_headsign = stCursor.getString(2);

						String[] departure_time_split = departure_time
								.split(":");
						String[] arrival_time_split = arrival_time.split(":");

						// Change the hour from 24 to 00, in order for it to be
						// properly sorted later
						if (departure_time_split[0].equals("24"))
							departure_time_split[0] = "00";
						if (arrival_time_split[0].equals("24"))
							arrival_time_split[0] = "00";
						departure_time = departure_time_split[0] + ":"
								+ departure_time_split[1];
						arrival_time = arrival_time_split[0] + ":"
								+ arrival_time_split[1];

						Log.d("No Transfer",
								departure_time + " from " + stop0.getStopName()
										+ " toward " + trip_headsign + ", "
										+ stop1.getStopName() + " at "
										+ arrival_time);

						String scheduledStop = departure_time + ";Toward "
								+ trip_headsign + ";" + arrival_time;
						allStopTimes.add(scheduledStop);

						j++;
					} while (stCursor.moveToNext() && j < stCursor.getCount());
				}
			}

			// if transfer is required
		}

		Collections.sort(allStopTimes);

		return allStopTimes;
	}

	/**
	 * Get all the times that a train will stop at this station, as well as
	 * toward what terminal stop that train is heading.
	 * 
	 * @param s
	 *            the stop
	 * @param date
	 *            the day of week or holiday selected
	 * @return all stop times at a station on a particular day of week or
	 *         holiday and their trip headsign
	 */
	public ArrayList<String> getSchedule(Stop s, String date) {
		ArrayList<String> allStopTimes = new ArrayList<String>();
		
		/*
		 * Check if the date passed through parameter follows a different
		 * schedule than the one use on that day of the week. Save to a cursor
		 * the service that will be followed on that day.
		 */
		String selectCalendarDateQuery = "SELECT * FROM "
				+ CalendarDateEntry.TABLE_NAME + " WHERE "
				+ CalendarDateEntry.COLUMN_DATE + "=?" + " AND "
				+ CalendarDateEntry.COLUMN_EXCEPTIONTYPE + "=1";
		Cursor calendarDateCursor = db.rawQuery(selectCalendarDateQuery,
				new String[] { date });

		String selectQuery = null;
		Cursor stCursor = null;

		/*
		 * If there is a special service running on the selected day of the
		 * week, get all the stop times that run on that service on the date
		 * selected. Else select all the stop times that service the stop on
		 * that day of the week.
		 */
		if (calendarDateCursor.moveToFirst()) {
			int i = 0;
			do {
				String serviceID = calendarDateCursor.getString(0);
				/*
				 * To temporary hold the results of query
				 */
				Cursor tempCursor = null;
				
				selectQuery = "SELECT DISTINCT t1."
						+ StopTimeEntry.COLUMN_DEPARTURETIME + ", t2."
						+ TripEntry.COLUMN_TRIPHEADSIGN + " FROM "
						+ StopTimeEntry.TABLE_NAME + " AS t1" + " JOIN "
						+ TripEntry.TABLE_NAME + " AS t2" + " ON t2."
						+ TripEntry.COLUMN_TRIPID + "=t1."
						+ StopTimeEntry.COLUMN_TRIPID + " AND t1."
						+ StopTimeEntry.COLUMN_STOPID + "=?" + " AND t2."
						+ TripEntry.COLUMN_SERVICEID + "=?" + " JOIN "
						+ RouteEntry.TABLE_NAME + " AS t3" + " ON t2."
						+ TripEntry.COLUMN_ROUTEID + "=t3."
						+ RouteEntry.COLUMN_ROUTEID + " JOIN "
						+ StopEntry.TABLE_NAME + " AS t5" + " ON t1."
						+ StopTimeEntry.COLUMN_STOPID + "=t5."
						+ StopEntry.COLUMN_STOPID + " AND t5."
						+ StopEntry.COLUMN_STOPNAME + " NOT LIKE t2."
						+ TripEntry.COLUMN_TRIPHEADSIGN + "||'%'" + " AND t2."
						+ TripEntry.COLUMN_TRIPHEADSIGN + " NOT LIKE t5."
						+ StopEntry.COLUMN_STOPNAME + "||'%'" + " ORDER BY t1."
						+ StopTimeEntry.COLUMN_DEPARTURETIME;

				tempCursor = db.rawQuery(selectQuery,
						new String[] { s.getStopID(), serviceID });
				/*
				 * To hold the rows that will be added from tempCursor 
				 */
				MatrixCursor tempMatrixCursor = new MatrixCursor(tempCursor.getColumnNames());
				if (tempCursor.moveToFirst())
				{
					int j = 0;
					do
					{
						tempMatrixCursor.addRow(new String[] {
								tempCursor.getString(0),
								tempCursor.getString(1) });
						j++;
					} while (tempCursor.moveToNext() && j < tempCursor.getCount());
				}
				
				/*
				 * Merge what already exists in stCursor with what is in tempMatrixCursor
				 */
				stCursor = new MergeCursor(new Cursor[] {tempMatrixCursor, stCursor});
								
				i++;
			} while (calendarDateCursor.moveToNext()
					&& i < calendarDateCursor.getCount());
		} else {
			/*
			 * Save in a variable the name of the column representative of the
			 * day of the week selected. To be used to find the id of the
			 * service(s) that run on that day of the week.
			 */
			if (date.equalsIgnoreCase("mon")) {
				date = CalendarEntry.COLUMN_MONDAY;
			} else if (date.equalsIgnoreCase("tue")) {
				date = CalendarEntry.COLUMN_TUESDAY;
			} else if (date.equalsIgnoreCase("wed")) {
				date = CalendarEntry.COLUMN_WEDNESDAY;
			} else if (date.equalsIgnoreCase("thu")) {
				date = CalendarEntry.COLUMN_THURSDAY;
			} else if (date.equalsIgnoreCase("fri")) {
				date = CalendarEntry.COLUMN_FRIDAY;
			} else if (date.equalsIgnoreCase("sat")) {
				date = CalendarEntry.COLUMN_SATURDAY;
			} else if (date.equalsIgnoreCase("sun")) {
				date = CalendarEntry.COLUMN_SUNDAY;
			}

			selectQuery = "SELECT DISTINCT t1."
					+ StopTimeEntry.COLUMN_DEPARTURETIME + ", t2."
					+ TripEntry.COLUMN_TRIPHEADSIGN + " FROM "
					+ StopTimeEntry.TABLE_NAME + " AS t1" + " JOIN "
					+ TripEntry.TABLE_NAME + " AS t2" + " ON t2."
					+ TripEntry.COLUMN_TRIPID + "=t1."
					+ StopTimeEntry.COLUMN_TRIPID + " AND t1."
					+ StopTimeEntry.COLUMN_STOPID + "=?" + " JOIN "
					+ CalendarEntry.TABLE_NAME + " AS t4" + " ON t2."
					+ TripEntry.COLUMN_SERVICEID + "=t4."
					+ CalendarEntry.COLUMN_SERVICEID + " AND t4." + date + "=1"
					+ " JOIN " + RouteEntry.TABLE_NAME + " AS t3" + " ON t2."
					+ TripEntry.COLUMN_ROUTEID + "=t3."
					+ RouteEntry.COLUMN_ROUTEID + " JOIN "
					+ StopEntry.TABLE_NAME + " AS t5" + " ON t1."
					+ StopTimeEntry.COLUMN_STOPID + "=t5."
					+ StopEntry.COLUMN_STOPID + " AND t5."
					+ StopEntry.COLUMN_STOPNAME + " NOT LIKE t2."
					+ TripEntry.COLUMN_TRIPHEADSIGN + "||'%'" + " AND t2."
					+ TripEntry.COLUMN_TRIPHEADSIGN + " NOT LIKE t5."
					+ StopEntry.COLUMN_STOPNAME + "||'%'" + " ORDER BY t1."
					+ StopTimeEntry.COLUMN_DEPARTURETIME;

			stCursor = db.rawQuery(selectQuery, new String[] { s.getStopID() });
		}

		if (stCursor.moveToFirst()) {
			int j = 0;
			do {
				String departure_time = stCursor.getString(0);
				String trip_headsign = stCursor.getString(1);

				String[] departure_time_split = departure_time.split(":");

				/*
				 * Change the hour from 24 to 00, in order for it to be properly
				 * sorted later
				 */
				if (departure_time_split[0].equals("24"))
					departure_time_split[0] = "00";

				departure_time = departure_time_split[0] + ":"
						+ departure_time_split[1];

				String scheduledStop = departure_time + ";To " + trip_headsign;
				allStopTimes.add(scheduledStop);

				j++;
			} while (stCursor.moveToNext() && j < stCursor.getCount());
		}

		Collections.sort(allStopTimes);

		return allStopTimes;
	}
}
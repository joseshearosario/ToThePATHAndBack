package com.shearosario.tothepathandback;

/**
 * Dates for service IDs using a weekly schedule. Specify when service starts
 * and ends, as well as days of the week where service is available.
 * 
 * @author Jose Andres Rosario
 */
public class Calendar 
{
	/**
	 * The service_id contains an ID that uniquely identifies a set of dates
	 * when service is available for one or more routes. This value is dataset
	 * unique.
	 */
	private String service_id;
	/**
	 * A name to describe this service
	 */
	private String service_name;
	/**
	 * Each field contains a binary value that indicates whether the
	 * service is valid for that day of the week.
	 */
	private boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
	/**
	 * The date field contains the start/end date for the service. The field's
	 * value should be in YYYYMMDD format.
	 */
	private String start_date, end_date;
		
	/**
	 * Creates a new Calendar object
	 * 
	 * @param service_id uniquely identifies this set of dates
	 * @param 
	 * @param monday whether service is valid on Mondays
	 * @param tuesday whether service is valid on Tuesdays
 	 * @param wednesday whether service is valid on Wednesdays
	 * @param thursday whether service is valid on Thursdays
	 * @param friday whether service is valid on Fridays
	 * @param saturday whether service is valid on Saturdays
	 * @param sunday whether service is valid on Sundays
	 * @param start_date the start date for this service
	 * @param end_date the end date for this service
	 */
	public Calendar(String service_id, String service_name, int monday, int tuesday,
			int wednesday, int thursday, int friday,
			int saturday, int sunday, String start_date, String end_date) 
	{
		this.service_id = service_id;
		this.service_name = service_name;
		
		if (monday == 1)
			this.monday = true;
		else
			this.monday = false;
		
		if (tuesday == 1)
			this.tuesday = true;
		else
			this.tuesday = false;
		
		if (wednesday == 1)
			this.wednesday = true;
		else
			this.wednesday = false;
		
		if (thursday == 1)
			this.thursday = true;
		else
			this.thursday = false;
		
		if (friday == 1)
			this.friday = true;
		else
			this.friday = false;
		
		if (saturday == 1)
			this.saturday = true;
		else
			this.saturday = false;
		
		if (sunday == 1)
			this.sunday = true;
		else
			this.sunday = false;
		
		
		this.start_date = start_date;
		this.end_date = end_date;
	}
	
	/**
	 * Creates a new Calendar object that is a copy of an already existing Calendar object
	 * 
	 *  @param c a Calendar object
	 */
	public Calendar (Calendar c)
	{
		this.service_id = c.getService_id();
		this.service_name = c.getService_name();
		this.monday = c.isMonday();
		this.tuesday = c.isTuesday();
		this.wednesday = c.isWednesday();
		this.thursday = c.isThursday();
		this.friday = c.isFriday();
		this.saturday = c.isSaturday();
		this.sunday = c.isSunday();
		this.start_date = c.getStart_date();
		this.end_date = c.getEnd_date();
	}

	/**
	 * @return the service_id
	 */
	public String getService_id() {
		return service_id;
	}
	
	/**
	 * @return the service_name
	 */
	private String getService_name() {
		return service_name;
	}

	/**
	 * A value of 1 indicates that service is available for all Mondays in the
	 * date range. (The date range is specified using the start_date and
	 * end_date fields.) A value of 0 indicates that service is not available on
	 * Mondays in the date range.
	 * 
	 * @return the monday
	 */
	public boolean isMonday() {
		return monday;
	}
	
	/**
	 * A value of 1 indicates that service is available for all Tuesdays in the
	 * date range. (The date range is specified using the start_date and
	 * end_date fields.) A value of 0 indicates that service is not available on
	 * Tuesdays in the date range.
	 * 
	 * @return the tuesday
	 */
	public boolean isTuesday() {
		return tuesday;
	}
	
	/**
	 * A value of 1 indicates that service is available for all Wednesdays in the
	 * date range. (The date range is specified using the start_date and
	 * end_date fields.) A value of 0 indicates that service is not available on
	 * Wednesdays in the date range.
	 * 
	 * @return the wednesday
	 */
	public boolean isWednesday() {
		return wednesday;
	}
	
	/**
	 * A value of 1 indicates that service is available for all Thursdays in the
	 * date range. (The date range is specified using the start_date and
	 * end_date fields.) A value of 0 indicates that service is not available on
	 * Thursdays in the date range.
	 * 
	 * @return the thursday
	 */
	public boolean isThursday() {
		return thursday;
	}
	
	/**
	 * A value of 1 indicates that service is available for all Fridays in the
	 * date range. (The date range is specified using the start_date and
	 * end_date fields.) A value of 0 indicates that service is not available on
	 * Fridays in the date range.
	 * 
	 * @return the friday
	 */
	public boolean isFriday() {
		return friday;
	}
	
	/**
	 * A value of 1 indicates that service is available for all Saturdays in the
	 * date range. (The date range is specified using the start_date and
	 * end_date fields.) A value of 0 indicates that service is not available on
	 * Saturdays in the date range.
	 * 
	 * @return the saturday
	 */
	public boolean isSaturday() {
		return saturday;
	}
	
	/**
	 * A value of 1 indicates that service is available for all Sundays in the
	 * date range. (The date range is specified using the start_date and
	 * end_date fields.) A value of 0 indicates that service is not available on
	 * Sundays in the date range.
	 * 
	 * @return the sunday
	 */
	public boolean isSunday() {
		return sunday;
	}
	
	/**
	 * @return the start_date
	 */
	public String getStart_date() {
		return start_date;
	}
	
	/**
	 * @return the end_date
	 */
	public String getEnd_date() {
		return end_date;
	}
}

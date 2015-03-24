package edu.avans.hartigehap.web.controller;

import lombok.Setter;

import org.h2.util.New;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Adapter to convert date and time strings to an DateTime object, using the Object Adapter pattern.
 */
public class DateTimeAdapter implements DateTimeProvider {
	@Setter
	private DateAndTime dateAndTime;

	@Setter
	private DateTime dateTime;
	

	public DateTimeAdapter() {
	}
	
	public DateTimeAdapter(DateTime dateTime) {
		this.dateTime = dateTime; 
	}

    public DateTimeAdapter(DateAndTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    @Override
    public DateTime getDateTime() {
        if (dateAndTime == null) {
            return new DateTime();
        }

        String text = dateAndTime.getDateAndTime();
        return convertStringToDateTime(text);
    }

    private static DateTime convertStringToDateTime(String text) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
        DateTime dateTime = formatter.parseDateTime(text);
        return dateTime;
    }

    /**
     * @return yyyy-MM-dd format
     */
	@Override
	public String getDate() {
		String date= "";
		if(dateTime != null){
			date = dateTime.toString().substring(0,10);
		}
		return date;
	}

	/**
     * @return HH:mm format
     */
	@Override
	public String getTime() {
		String time= "";
		if(dateTime != null){
			time = dateTime.toString().substring(11,16);
		}
		return time;
	}
}

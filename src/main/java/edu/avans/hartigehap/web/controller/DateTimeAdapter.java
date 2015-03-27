package edu.avans.hartigehap.web.controller;

import lombok.Setter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Adapter to convert date and time strings to an DateTime object, using the Object Adapter pattern.
 */
public class DateTimeAdapter implements DateTimeProvider {
	@Setter
	private DateAndTime dateAndTime;
	

	public DateTimeAdapter() {
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
}

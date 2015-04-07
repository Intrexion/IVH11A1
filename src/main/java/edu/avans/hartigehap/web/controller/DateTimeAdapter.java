package edu.avans.hartigehap.web.controller;

import lombok.Setter;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

/**
 * Adapter to convert date and time objects to an DateTime object, using the Object Adapter pattern.
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

        LocalDate date = dateAndTime.getDate();
        LocalTime time = dateAndTime.getTime();
        return date.toDateTime(time);
    }
}

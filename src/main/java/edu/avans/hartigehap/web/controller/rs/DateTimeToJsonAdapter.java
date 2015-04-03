package edu.avans.hartigehap.web.controller.rs;

import lombok.NoArgsConstructor;
import lombok.Setter;

import org.joda.time.DateTime;

@NoArgsConstructor
public class DateTimeToJsonAdapter implements DateTimeToJsonProvider {
	@Setter
	private DateTime dateAndTime;

    public DateTimeToJsonAdapter(DateTime dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    private static String convertDateTimeToString(DateTime dateTime) {
        return dateTime.toString("yyyy-MM-dd HH:mm");
    }
    
	@Override
	public String getJson() {		
		return convertDateTimeToString(dateAndTime);
	}

}
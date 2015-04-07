package edu.avans.hartigehap.web.controller.rs;

import lombok.NoArgsConstructor;
import lombok.Setter;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

@NoArgsConstructor
public class DateTimeToJsonAdapter implements DateTimeToJsonProvider {
	@Setter
	private LocalDate date;
	@Setter
	private LocalTime time;
	
    public DateTimeToJsonAdapter(LocalTime time, LocalDate date) {
        this.date = date;
        this.time = time;
    }

    private String convertDateTimeToString() {
        return date.toString("yyyy-MM-dd") + " " + time.toString("HH:mm");
    }
    
	@Override
	public String getJson() {		
		return convertDateTimeToString();
	}

}
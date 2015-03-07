package edu.avans.hartigehap.web.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DateAndTime {
    /**
     * The date in yyyy-MM-dd format
     */
    private String date;
    /**
     * The time in HH:mm format
     */
    private String time;

    public DateAndTime(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDateAndTime() {
        return toString();
    }

    @Override
    public String toString() {
        return date + " " + time;
    }
}

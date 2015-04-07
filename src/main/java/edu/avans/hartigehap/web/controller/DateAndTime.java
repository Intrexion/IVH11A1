package edu.avans.hartigehap.web.controller;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DateAndTime {
    private LocalDate date;
    private LocalTime time;

    public DateAndTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }
}

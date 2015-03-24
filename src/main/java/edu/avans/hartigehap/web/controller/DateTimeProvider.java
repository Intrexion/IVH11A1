package edu.avans.hartigehap.web.controller;

import org.joda.time.DateTime;

/**
 * Interface for adapters returning an DateTime object.
 */
public interface DateTimeProvider {
    public DateTime getDateTime();
    public String getDate();
    public String getTime();
}

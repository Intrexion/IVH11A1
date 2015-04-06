package edu.avans.hartigehap.web.controller;

import javax.mail.MessagingException;

import edu.avans.hartigehap.domain.Reservation;

public interface MailStrategy {
	public String setMailContent(Reservation reservation, String reservationDate, String reservationTime) throws MessagingException;
}

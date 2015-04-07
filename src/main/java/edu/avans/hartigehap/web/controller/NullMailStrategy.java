package edu.avans.hartigehap.web.controller;

import javax.mail.MessagingException;

import edu.avans.hartigehap.domain.Reservation;

public class NullMailStrategy implements MailStrategy {

	@Override
	public String setMailContent(Reservation reservation,String reservationDate, String reservationTime) throws MessagingException {
		return "Language not available";
	}

}

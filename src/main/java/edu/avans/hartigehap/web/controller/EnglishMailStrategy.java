package edu.avans.hartigehap.web.controller;

import javax.mail.MessagingException;
import edu.avans.hartigehap.domain.Reservation;

public class EnglishMailStrategy implements MailStrategy {

	@Override
	public String setMailContent(Reservation reservation, String reservationDate, String reservationTime) throws MessagingException {
		return "<html><body><p>Dear Sir/Madam " + reservation.getCustomer().getLastName() + ",</p>" +
				"<p>We hereby confirm your reservation at " + reservation.getRestaurant().getId() + " on " + reservationDate + " at " + reservationTime + ".</p>" +
				"<p>Your reservationcode is: <b>" + reservation.getCode() + "</b>.<p>" +
				"<p>Make sure to keep this code safe, it is needed upon your arrival in the restaurant to confirm your reservation. <br> Below you will also find the code as QR code,  which you can also use to confirm your reservation by scanning it at the restaurant.</p>" +
				"<img src=\"cid:qr-code\"/><br>" +
				"<p>Best regards,<br>" +
				"The Hartige Hap team</p></body></html>";
	}
}

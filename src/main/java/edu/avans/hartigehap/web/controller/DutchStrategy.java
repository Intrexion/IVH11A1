package edu.avans.hartigehap.web.controller;

import javax.mail.MessagingException;
import edu.avans.hartigehap.domain.Reservation;

public class DutchStrategy implements MailStrategy {

	@Override
	public String setMailContent(Reservation reservation, String reservationDate, String reservationTime) throws MessagingException {
		return "<html><body><p>Geachte heer/mevrouw " + reservation.getCustomer().getLastName() + ",</p>" +
				"<p>Hierbij bevestigen wij uw reservering bij " + reservation.getRestaurant().getId() + " op " + reservationDate + " om " + reservationTime + ".</p>" +
				"<p>Uw reserveringscode is: <b>" + reservation.getCode() + "</b>.</p>" +
				"<p>Bewaar deze code goed, deze is nodig om uw reservering te bevestigen in het restaurant. <br> Hieronder vindt u de code ook in QR vorm, deze kunt u inscannen in het restaurant om uw reservering te bevestigen</p>" +
				"<img src=\"cid:qr-code\"/><br>" +
				"<p>Met vriendelijke groet,<br>" +
				"Het Hartige Hap management</p></body></html>";
	}
}

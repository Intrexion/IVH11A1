package edu.avans.hartigehap.web.controller;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import edu.avans.hartigehap.domain.Reservation;

public class SMTPController {

	public MailSender mailSender;
	
	public void setMailSender(MailSender mailSender){
		this.mailSender = mailSender;
	}
	
	public void sendMail(Reservation reservation){
		
		DateTime date = reservation.getStartDate();
		DateTimeFormatter dtfd = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter dtft = DateTimeFormat.forPattern("HH:mm");
		String reservationDate = dtfd.print(date);
		String reservationTime = dtft.print(date);
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("hhreserveringen@gmail.com");
		message.setTo(reservation.getCustomer().getEmail());
		message.setSubject("Uw " + reservation.getRestaurant().getId() + " reservering");
		message.setText("Geachte heer/mevrouw " + reservation.getCustomer().getLastName() + ",\n\n" +
						"Hierbij bevestigen wij uw reservering bij " + reservation.getRestaurant().getId() + " op " + reservationDate + " om " + reservationTime + ". \n\n" +
						"Uw reserveringscode is: " + reservation.getCode() + ". \n\n" +
						"Bewaar deze code goed, deze is nodig om uw reservering te bevestigen in het restaurant. \n\n" +
						"Met vriendelijke groet, \n" +
						"Het Hartige Hap management");
		mailSender.send(message);
	}
	
}

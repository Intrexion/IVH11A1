package edu.avans.hartigehap.web.controller;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import edu.avans.hartigehap.domain.Reservation;

public class MailController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailController.class);
	
	@Autowired
	private static JavaMailSender mailSender;

	public void setMailSender(JavaMailSender mailSender){
		this.mailSender = mailSender;
	}
	
	public static void sendMail(Reservation reservation) throws MessagingException {
		DateTime date = reservation.getStartDate();
		DateTimeFormatter dtfd = DateTimeFormat.forPattern("dd/MM/yyyy");
		DateTimeFormatter dtft = DateTimeFormat.forPattern("HH:mm");
		String reservationDate = dtfd.print(date);
		String reservationTime = dtft.print(date);
		
		String locale = LocaleContextHolder.getLocale().toString();
		File qrcode = QRCode.from(reservation.getCode()).to(ImageType.JPG).withSize(250, 250).file();
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mimeMessage, false, "utf-8");
		Multipart multipart = new MimeMultipart("related");
		message.setFrom("hhreserveringen@gmail.com");
		message.setTo(reservation.getCustomer().getEmail());
		message.setSubject("Uw " + reservation.getRestaurant().getId() + " reservering");
		BodyPart htmlPart = new MimeBodyPart();		
		
		MailStrategy context = null;
		
		switch(locale){
			case "nl_NL": context = new DutchMailStrategy();
				break;
			case "en_US": context = new EnglishMailStrategy();
				break;
			default: 
				LOGGER.info("Not supported locale used.");
				context = new NullMailStrategy(); 
				break;
		}
		htmlPart.setContent(context.setMailContent(reservation, reservationDate, reservationTime), "text/html");
		multipart.addBodyPart(htmlPart);
		
		BodyPart imgPart = new MimeBodyPart();
		DataSource ds = new FileDataSource(qrcode);
		imgPart.setDataHandler(new DataHandler(ds));
		imgPart.setHeader("Content-ID", "<qr-code>");
		
		multipart.addBodyPart(imgPart);
		mimeMessage.setContent(multipart);
		
		mailSender.send(mimeMessage);		
	}
	

}

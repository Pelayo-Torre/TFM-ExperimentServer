package com.uniovi.es.business.mail;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderServiceImpl implements MailSenderService{
	
	private static final Logger logger = LoggerFactory.getLogger(MailSenderServiceImpl.class);
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Value("${app.urlMail}")
	private String url;

	@Override
	public void sendMail(String mailReceiver, String subject, String text) throws MessagingException {
		SimpleMailMessage message = new SimpleMailMessage();
		
		message.setSubject(subject);
		message.setTo(mailReceiver);
		message.setText(text + "\n \n\t " + url);
		
		logger.info("\t \t Enviando mail a " + mailReceiver + " con subject ( " + subject + " ) y texto: " + text);

		mailSender.send(message);
	}
	
	

}

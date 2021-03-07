package com.uniovi.es.business.mail;

import javax.mail.MessagingException;

public interface MailSenderService {
	
	/**
	 * Envía un correo electrónico a un usuario
	 * @param mailReceiver mail del usuario receptor del correo
	 * @param subject Título del correo
	 * @param text texto del correo
	 * @throws MessagingException en caso de que se produzca un error en el envío del correo
	 */
	public void sendMail(String mailReceiver, String subject, String text) throws MessagingException;

}

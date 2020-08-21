package com.uniovi.es.business.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.persistence.InvestigatorDAO;

@Component
public class InvestigatorValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(InvestigatorValidator.class);
	
	private static final Integer LONGITUD_CONTRASEÑA = 8;
	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	/**
	 * Realiza la validación de los datos de entrada de un investigador
	 * @param dto parámetros de entrada
	 * @throws InvestigatorException en caso de que salte algún error de validación
	 */
	public void validate(InvestigatorDTO dto) throws InvestigatorException{
		
		if(dto.name == null || dto.name.equals("")) {
			logger.error("[ERROR - 201] -- El nombre del investigador es un campo obligatorio");
			throw new InvestigatorException("201");
		}
		
		if(dto.surname == null || dto.surname.equals("")) {
			logger.error("[ERROR - 202] -- El apellido del investigador es un campo obligatorio");
			throw new InvestigatorException("202");
		}
		
		if(dto.email == null || dto.email.equals("")) {
			logger.error("[ERROR - 203] -- El email del investigador es un campo obligatorio");
			throw new InvestigatorException("203");
		}
		
		if(dto.username == null || dto.username.equals("")) {
			logger.error("[ERROR - 205] -- El nombre de usuario es un campo obligatorio");
			throw new InvestigatorException("205");
		}
		
		Pattern pattern = Pattern.compile(PATTERN_EMAIL);
		Matcher matcher = pattern.matcher(dto.email);
		
		if (!matcher.matches()) {
			logger.error("[ERROR - 209] -- El formato del correo electrónico es incorrecto");
			throw new InvestigatorException("209");
		}
				
	}
	
	/**
	 * Valida que la contraseña sea correcta
	 * @param password campo de entrada
	 * @throws InvestigatorException en caso de que existan errores
	 */
	public void validatePassword(String password) throws InvestigatorException {
		if(password == null) {
			logger.error("[ERROR - 207] -- La contraeña de un investigador es un campo obligatorio");
			throw new InvestigatorException("207");
		}
		
		if(password.length() < LONGITUD_CONTRASEÑA) {
			logger.error("[ERROR - 208] -- La contraseña debe tener al menos " + LONGITUD_CONTRASEÑA + " caracteres");
			throw new InvestigatorException("208");
		}
	}
	
	/**
	 * Valida la existencia de un email que se pasa como parámetro
	 * @param mail parámetro de entrada
	 * @throws InvestigatorException en caso de que el mail ya se encuentre registrado en la aplicación
	 */
	public void validateExistenceOfMail(String mail) throws InvestigatorException {
		if(investigatorDAO.findByMail(mail.toLowerCase()) != null) {
			logger.error("[ERROR - 204] -- El email del investigador ya se encuentra registrado en la aplicación");
			throw new InvestigatorException("204");
		}
	}
	
	/**
	 * Valida la existencia de un nombre de usuario que se pasa como parámetro
	 * @param username parámetro de entrada
	 * @throws InvestigatorException en caso de que el nombre de usuario ya se encuentre registrado en la aplicación
	 */
	public void validateExistenceOfUsername(String username) throws InvestigatorException {
		if(investigatorDAO.findByUsername(username.toLowerCase()) != null) {
			logger.error("[ERROR - 206] -- El nombre del usuario ya se encuentra registrado en la aplicación");
			throw new InvestigatorException("206");
		}
	}

}

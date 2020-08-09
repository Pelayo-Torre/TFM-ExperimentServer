package com.uniovi.es.business.validators;

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
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
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
		
	}
	
	public void validateExistenceOfMail(String mail) throws InvestigatorException {
		if(investigatorDAO.findByMail(mail.toLowerCase()) != null) {
			logger.error("[ERROR - 204] -- El email del investigador ya se encuentra registrado en la aplicación");
			throw new InvestigatorException("204");
		}
	}

}

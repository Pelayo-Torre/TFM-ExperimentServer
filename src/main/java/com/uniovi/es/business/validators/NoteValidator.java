package com.uniovi.es.business.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.exceptions.NoteException;

@Component
public class NoteValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(NoteValidator.class);

	public void validate(NoteDTO dto) throws NoteException{
		
		if(dto.title == null || dto.title.equals("")) {
			logger.error("[ERROR - 401] -- El título de una nota es un campo obligatorio");
			throw new NoteException("401");
		}
		
		if(dto.description == null || dto.description.equals("")) {
			logger.error("[ERROR - 402] -- La descripción de una nota es un campo obligatorio");
			throw new NoteException("402");
		}
		
	}
	
}

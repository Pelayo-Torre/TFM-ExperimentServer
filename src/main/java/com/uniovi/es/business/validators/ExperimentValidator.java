package com.uniovi.es.business.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.StatusExperiment;

public class ExperimentValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentValidator.class);
	
	public void validate(ExperimentDTO dto) throws ExperimentException {
		
		if(dto.title == null || dto.title.isBlank() || dto.title.isEmpty()) {
			logger.error("[ERROR - 105] -- El título del experimento es un campo obligatorio");
			throw new ExperimentException("105");
		}
		
		if(dto.description == null || dto.description.isBlank() || dto.description.isEmpty()) {
			logger.error("[ERROR - 106] -- La descripción del experimento es un campo obligatorio");
			throw new ExperimentException("106");
		}
		
		if(dto.status == null) {
			logger.error("[ERROR - 107] -- El estado del experimento es un campo obligatorio");
			throw new ExperimentException("107");
		}
		
		if(StatusExperiment.valueOf(dto.status) == null) {
			logger.error("[ERROR - 108] -- El estado del experimento especificado no se corresponde con los existentes");
			throw new ExperimentException("108");
		}
		
	}

}

package com.uniovi.es.business.validators;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.types.DemographicDataType;

@Component
public class ExperimentValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentValidator.class);

	public void validate(ExperimentDTO dto) throws ExperimentException {
		
		if(dto.title == null || dto.title.isEmpty()) {
			logger.error("[ERROR - 105] -- El título del experimento es un campo obligatorio");
			throw new ExperimentException("105");
		}
		
		if(dto.description == null || dto.description.isEmpty()) {
			logger.error("[ERROR - 106] -- La descripción del experimento es un campo obligatorio");
			throw new ExperimentException("106");
		}
		
		if(dto.demographicData != null && dto.demographicData.size() > 0) {
			for(DemographicDataDTO dd : dto.demographicData) {
				if(dd == null) {
					logger.error("[ERROR - 110] -- Un campo demográfico no puede ser null.");
					throw new ExperimentException("110");
				}
				else if(dd.name == null || dd.name.equals("")) {
					logger.error("[ERROR - 111] -- El valor de un dato demográfico es un campo obligatorio.");
					throw new ExperimentException("111");
				}
				else if(dd.type == null) {
					logger.error("[ERROR - 112] -- El tipo de un dato demográfico es un campo obligatorio.");
					throw new ExperimentException("112");
				}
				else {
					try {
						DemographicDataType.valueOf(dd.type);
					} catch(IllegalArgumentException e) {
						logger.error("[ERROR - 113] -- El tipo del campo demográfico especificado no se corresponde con los existentes.");
						throw new ExperimentException("113");
					}
				}
			}
		}	
	}
	
}

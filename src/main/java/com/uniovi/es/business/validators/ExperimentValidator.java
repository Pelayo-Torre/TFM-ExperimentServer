package com.uniovi.es.business.validators;


import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.types.Device;
import com.uniovi.es.model.types.Gender;
import com.uniovi.es.model.types.Laterality;
import com.uniovi.es.persistence.DeviceDAO;

@Component
public class ExperimentValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentValidator.class);
	
	@Autowired
	private DeviceDAO deviceDAO;
	
	public void validate(ExperimentDTO dto) throws ExperimentException {
		
		if(dto.title == null || dto.title.isEmpty()) {
			logger.error("[ERROR - 105] -- El título del experimento es un campo obligatorio");
			throw new ExperimentException("105");
		}
		
		if(dto.description == null || dto.description.isEmpty()) {
			logger.error("[ERROR - 106] -- La descripción del experimento es un campo obligatorio");
			throw new ExperimentException("106");
		}
		
	}
	
	public void validateDemographicData(ExperimentDTO dto) throws ExperimentException {
		if(dto.gender == null) {
			logger.error("[ERROR - 110] -- El género de los usuarios de un experimento es un campo obligatorio");
			throw new ExperimentException("110");
		}
		else {
			try {
				Gender.valueOf(dto.gender);
			}catch(IllegalArgumentException e) {
				logger.error("[ERROR - 110] -- El género de los usuarios de un experimento es un campo obligatorio");
				throw new ExperimentException("110");
			}
		}
		
		if(dto.birthDate == null) {
			logger.error("[ERROR - 111] -- La edad de los usuarios de un experimento es un campo obligatorio");
			throw new ExperimentException("111");
		}
		else {
			if(dto.birthDate.after(new Date())) {
				logger.error("[ERROR - 118] -- La fecha de nacimiento debe no debe ser posterior al dia actual");
				throw new ExperimentException("118");
			}
		}
		
		if(dto.laterality == null) {
			logger.error("[ERROR - 112] -- La lateralidad de los usuarios de un experimento es un campo obligatorio");
			throw new ExperimentException("112");
		}
		else {
			try {
				Laterality.valueOf(dto.laterality);
			}catch(IllegalArgumentException e) {
				logger.error("[ERROR - 112] -- La lateralidad de los usuarios de un experimento es un campo obligatorio");
				throw new ExperimentException("112");
			}
		}
		
		if(dto.idDevice == null) {
			logger.error("[ERROR - 113] -- El dispositivo sobre el que se realizará un experimento es un campo obligatorio");
			throw new ExperimentException("113");
		}
		
		Optional<Device> optional = deviceDAO.findById(dto.idDevice);
		if(!optional.isPresent()) {
			logger.error("[ERROR - 114] -- El dispositivo especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("114");
		}
	}
	
}

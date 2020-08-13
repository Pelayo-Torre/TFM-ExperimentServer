package com.uniovi.es.business.validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.persistence.PetitionDAO;

@Component
public class PetitionValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(PetitionValidator.class);
	
	@Autowired
	private PetitionDAO petitionDAO;

	public void validate(PetitionDTO dto) {
		
	}
	
	/**
	 * Comprueba que no exista ya una petición en estado PENDIENTE O ACEPTADA 
	 * para el investigador y experimento pasados como parámetro
	 * @param idInvestigator identificador del investigador
	 * @param idExperiment identificador del experimento
	 * @throws PetitionException en caso de que exista la petición en esos estados
	 */
	public void validatePetitionExistence(Long idInvestigator, Long idExperiment) throws PetitionException {
		if(petitionDAO.findPetitionByIdInvestigatorAndIdExperiment(idInvestigator, idExperiment) != null) {
			logger.error("[ERROR - 304] -- Ya existe una petitición registrada para el investigador y el experimento especificados");
			throw new PetitionException("304");
		}
	}
	
}
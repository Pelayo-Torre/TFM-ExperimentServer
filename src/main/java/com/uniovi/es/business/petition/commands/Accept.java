package com.uniovi.es.business.petition.commands;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.StatusPetition;

public class Accept implements PetitionCommand{
	
	private static final Logger logger = LoggerFactory.getLogger(Accept.class);

	@Override
	public void execute(Petition petition) throws PetitionException {
		if(petition.getStatus().equals(StatusPetition.PENDING)) {
			petition.accept();
			petition.setAnswerDate(new Date());
		}
		else {
			logger.error("[ERROR - 301] -- No se puede modificar el estado de una petición a ACEPTADA si no se encuentra en estado PENDIENTE");
			throw new PetitionException("301");
		}
	}

}

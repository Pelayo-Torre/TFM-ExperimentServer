package com.uniovi.es.business.petition.commands;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.Petition;

public class Reject implements PetitionCommand{

	private static final Logger logger = LoggerFactory.getLogger(Reject.class);
	
	@Override
	public void execute(Petition petition) throws PetitionException {
		if(petition.isPending()) {
			petition.reject();
			petition.setAnswerDate(new Date());
		}
		else {
			logger.error("[ERROR - 302] -- No se puede modificar el estado de una petición a RECHAZADA si no se encuentra en estado PENDIENTE");
			throw new PetitionException("302");
		}
	}

}

package com.uniovi.es.business.petition.commands;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.Petition;

public class Cancel implements PetitionCommand{

	private static final Logger logger = LoggerFactory.getLogger(Cancel.class);
	
	@Override
	public void execute(Petition petition) throws PetitionException {
		if(petition.isAccepted() || petition.isPending()) {
			petition.cancel();
			petition.setAnswerDate(new Date());
		}
		else {
			logger.error("[ERROR - 303] -- No se puede modificar el estado de una petición a CANCELADA si no se encuentra en estado ACEPTADA o PENDIENTE");
			throw new PetitionException("303");
		}
	}

}

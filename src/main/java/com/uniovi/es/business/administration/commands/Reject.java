package com.uniovi.es.business.administration.commands;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.model.Request;
import com.uniovi.es.model.types.StatusRequest;

public class Reject implements RequestCommand{
	
	private static final Logger logger = LoggerFactory.getLogger(Reject.class);

	@Override
	public void execute(Request request) throws AdministrationException {
		if(request.getStatus().equals(StatusRequest.PENDING)) {
			request.reject();
			request.setAnswerDate(new Date());
		}
		else {
			logger.error("[ERROR - 504] -- La solicitud debe estar en estado PENDING para ser aceptada o rechazada");
			throw new AdministrationException("504");
		}
	}

}

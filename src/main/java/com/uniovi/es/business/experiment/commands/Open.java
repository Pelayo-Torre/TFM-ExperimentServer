package com.uniovi.es.business.experiment.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;

public class Open implements ExperimentCommand{
	
	private static final Logger logger = LoggerFactory.getLogger(Open.class);

	@Override
	public void execute(Experiment experiment) throws ExperimentException {
		if(experiment.isCreated() || experiment.isClosed()) {
			experiment.open();
		}
		else {
			logger.error("[ERROR - 101] -- No se puede modificar el estado del experimento a ABIERTO si no está en estado CREADO o CERRADO");
			throw new ExperimentException("101");
		}
	}

}

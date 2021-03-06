package com.uniovi.es.business.experiment.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.types.StatusExperiment;

public class ReOpen implements ExperimentCommand{
	
	private static final Logger logger = LoggerFactory.getLogger(ReOpen.class);

	@Override
	public void execute(Experiment experiment) throws ExperimentException {
		if(experiment.getStatus().equals(StatusExperiment.CLOSED)) {
			experiment.open();
		}
		else {
			logger.error("[ERROR - 102] -- No se puede modificar el estado del experimento a ABIERTO si no está en estado CERRADO. (Reabrir experimento)");
			throw new ExperimentException("102");
		}
		
	}

}

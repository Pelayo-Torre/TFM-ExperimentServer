package com.uniovi.es.business.experiment.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.StatusExperiment;

public class Delete implements ExperimentCommand{
	
	private static final Logger logger = LoggerFactory.getLogger(Delete.class);

	@Override
	public void execute(Experiment experiment) throws ExperimentException {
		if(experiment.getStatus().equals(StatusExperiment.CREATED) || experiment.getStatus().equals(StatusExperiment.CLOSED)) {
			experiment.delete();
		}
		else {
			logger.error("[ERROR - 104] -- No se puede modificar el estado del experimento a ELIMINADO si no está en los estados CREADO o CERRADO");
			throw new ExperimentException("104");
		}
	}

}

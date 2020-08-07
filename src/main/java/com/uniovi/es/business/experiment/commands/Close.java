package com.uniovi.es.business.experiment.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.investigator.InvestigatorServiceImpl;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.StatusExperiment;

public class Close implements Command{
	
	private static final Logger logger = LoggerFactory.getLogger(InvestigatorServiceImpl.class);

	@Override
	public void execute(Experiment experiment) throws ExperimentException {
		if(experiment.getStatus().equals(StatusExperiment.OPEN)) {
			experiment.close();
		}
		else {
			logger.error("[ERROR - 103] -- No se puede modificar el estado del experimento a CERRADO si no está en estado ABIERTO");
			throw new ExperimentException("103");
		}
	}

}

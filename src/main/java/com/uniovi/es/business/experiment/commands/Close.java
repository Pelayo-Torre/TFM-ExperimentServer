package com.uniovi.es.business.experiment.commands;

import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.StatusExperiment;

public class Close implements Command{

	@Override
	public void execute(Experiment experiment) throws ExperimentException {
		if(experiment.getStatus().equals(StatusExperiment.OPEN)) {
			experiment.close();
		}
		else {
			throw new ExperimentException("103");
		}
	}

}

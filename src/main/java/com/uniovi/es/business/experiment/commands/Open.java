package com.uniovi.es.business.experiment.commands;

import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.StatusExperiment;

public class Open implements Command{

	@Override
	public void execute(Experiment experiment) throws ExperimentException {
		if(experiment.getStatus().equals(StatusExperiment.CREATED) || experiment.getStatus().equals(StatusExperiment.CLOSED)) {
			experiment.open();
		}
		else {
			throw new ExperimentException("101");
		}
	}

}

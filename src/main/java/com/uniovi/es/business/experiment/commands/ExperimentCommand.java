package com.uniovi.es.business.experiment.commands;

import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;

public interface ExperimentCommand {
	
	public void execute(Experiment experiment) throws ExperimentException;

}

package com.uniovi.es.business.experiment;

import com.uniovi.es.business.experiment.commands.Command;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;

public class ActionManager {
	
	private Experiment experiment;
	
	public ActionManager(Experiment experiment) {
		this.experiment = experiment;
	}
	
	public void execute(Command command) throws ExperimentException {
		command.execute(experiment);
	}
	
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

}

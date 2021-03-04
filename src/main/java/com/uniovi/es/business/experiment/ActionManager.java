package com.uniovi.es.business.experiment;

import com.uniovi.es.business.experiment.commands.ExperimentCommand;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;

public class ActionManager {
		
	public ActionManager() {}
	
	public void execute(ExperimentCommand command, Experiment experiment) throws ExperimentException {
		command.execute(experiment);
	}

}

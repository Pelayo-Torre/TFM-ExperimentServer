package com.uniovi.es.business.experiment;

import com.uniovi.es.business.experiment.commands.Open;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;

public class ExperimentServiceImpl implements ExperimentService{

	private ActionManager actionManager = new ActionManager(null);
	
	@Override
	public void open(String id) throws ExperimentException {
		Experiment experiment = null;
		actionManager.setExperiment(experiment);
		actionManager.execute(new Open());
	}

	@Override
	public void reOpen(String id) throws ExperimentException {
		Experiment experiment = null;
		actionManager.setExperiment(experiment);
		actionManager.execute(new Open());
	}

	@Override
	public void close(String id) {
		Experiment experiment = null;
		actionManager.setExperiment(experiment);
	}

	@Override
	public void delete(String id) {
		Experiment experiment = null;
		actionManager.setExperiment(experiment);
	}

}

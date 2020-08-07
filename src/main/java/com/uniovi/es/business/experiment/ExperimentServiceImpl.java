package com.uniovi.es.business.experiment;

import com.uniovi.es.business.experiment.commands.Close;
import com.uniovi.es.business.experiment.commands.Delete;
import com.uniovi.es.business.experiment.commands.Open;
import com.uniovi.es.business.experiment.commands.ReOpen;
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
		actionManager.execute(new ReOpen());
	}

	@Override
	public void close(String id) throws ExperimentException {
		Experiment experiment = null;
		actionManager.setExperiment(experiment);
		actionManager.execute(new Close());
	}

	@Override
	public void delete(String id) throws ExperimentException {
		Experiment experiment = null;
		actionManager.setExperiment(experiment);
		actionManager.execute(new Delete());
	}

}

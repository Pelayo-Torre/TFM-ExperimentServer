package com.uniovi.es.presentation.experiment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.investigator.InvestigatorServiceImpl;
import com.uniovi.es.exceptions.ExperimentException;

@RestController
@RequestMapping("/experiment")
public class ExperimentControllerImpl implements ExperimentController {
	
	private static final Logger logger = LoggerFactory.getLogger(InvestigatorServiceImpl.class);
	
	@Autowired
	private ExperimentService experimentService;

	@Override
	@RequestMapping(value = "/open/{id}", method = RequestMethod.PUT)
	public void openExperiment(String id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- open experiment");
		
		experimentService.open(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- open experiment");
	}

	@Override
	@RequestMapping(value = "/close/{id}", method = RequestMethod.PUT)
	public void closeExperiment(String id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- close experiment");
		
		experimentService.close(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- close experiment");
	}

	@Override
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.PUT)
	public void deleteExperiment(String id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- delete experiment");
		
		experimentService.delete(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- delete experiment");
	}
	
	@Override
	@RequestMapping(value = "/reopen/{id}", method = RequestMethod.PUT)
	public void reOpenExperiment(String id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- reOpen experiment");
		
		experimentService.reOpen(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- reOpen experiment");
	}

	@Override
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void registerExperiment(ExperimentDTO dto) {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- register experiment");

	}

	@Override
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public ExperimentDTO getExperiment(String id) {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- detail experiment");

		return null;
	}

	@Override
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public void editExperiment(ExperimentDTO dto) {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- update experiment");

	}

	
	
}

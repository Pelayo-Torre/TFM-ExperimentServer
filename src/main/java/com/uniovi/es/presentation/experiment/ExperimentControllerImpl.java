package com.uniovi.es.presentation.experiment;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.exceptions.ExperimentException;

@RestController
@RequestMapping("/experiment")
public class ExperimentControllerImpl implements ExperimentController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentControllerImpl.class);
	
	@Autowired
	private ExperimentService experimentService;

	@Override
	@RequestMapping(value = "/open", method = RequestMethod.PUT)
	public void openExperiment(@RequestBody Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- open experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);
		
		experimentService.open(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- open experiment");
	}

	@Override
	@RequestMapping(value = "/close", method = RequestMethod.PUT)
	public void closeExperiment(@RequestBody Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- close experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);

		experimentService.close(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- close experiment");
	}

	@Override
	@RequestMapping(value = "/delete", method = RequestMethod.PUT)
	public void deleteExperiment(@RequestBody Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- delete experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);

		experimentService.delete(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- delete experiment");
	}
	
	@Override
	@RequestMapping(value = "/reopen", method = RequestMethod.PUT)
	public void reOpenExperiment(@RequestBody Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- reOpen experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);

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
	public ExperimentDTO getExperiment(Long id) {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- detail experiment");

		return null;
	}

	@Override
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public void editExperiment(ExperimentDTO dto) throws ExperimentException{
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- update experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + dto);

		experimentService.update(dto);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- update experiment");
		
	}

	

	
	
}

package com.uniovi.es.presentation.experiment;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.utils.Identifier;

@RestController
@RequestMapping("/experiment")
@CrossOrigin(origins = "http://localhost:4200")
public class ExperimentControllerImpl implements ExperimentController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentControllerImpl.class);
	
	@Autowired
	private ExperimentService experimentService;

	@Override
	@RequestMapping(value = "/open", method = RequestMethod.PUT, consumes="application/json")
	public void openExperiment(@RequestBody Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- open experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);
		
		experimentService.open(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- open experiment");
	}

	@Override
	@RequestMapping(value = "/close", method = RequestMethod.PUT, consumes="application/json")
	public void closeExperiment(@RequestBody Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- close experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);

		experimentService.close(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- close experiment");
	}

	@Override
	@RequestMapping(value = "/delete", method = RequestMethod.PUT)
	public void deleteExperiment(@RequestBody Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- delete experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);

		experimentService.delete(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- delete experiment");
	}
	
	@Override
	@RequestMapping(value = "/reopen", method = RequestMethod.PUT, consumes="application/json")
	public void reOpenExperiment(@RequestBody Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- reOpen experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);

		experimentService.reOpen(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- reOpen experiment");
	}

	@Override
	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes="application/json")
	public void registerExperiment(@RequestBody ExperimentDTO dto) throws ExperimentException, InvestigatorException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- register experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + dto);

		experimentService.register(dto);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- register experiment");
	}

	@Override
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public ExperimentDTO getExperiment(@PathVariable Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- detail experiment");
		
		ExperimentDTO dto = experimentService.getDetail(id);

		logger.info("[FINAL] EXPERIMENT CONTROLLER -- detail experiment");
		return dto;
	}

	@Override
	@RequestMapping(value = "/update", method = RequestMethod.PUT, consumes="application/json")
	public void editExperiment(@RequestBody ExperimentDTO dto) throws ExperimentException{
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- update experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + dto);

		experimentService.update(dto);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- update experiment");
		
	}

	@Override
	@RequestMapping(value = "/investigators/{id}", method = RequestMethod.GET)
	public List<InvestigatorDTO> getInvestigatorsOfExperiment(@PathVariable Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- investigators of experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);
		
		List<InvestigatorDTO> list = experimentService.getInvestigatorsOfExperiment(id);
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- investigators of experiment");
		return list;
	}

	@Override
	public List<ExperimentDTO> getListExperiments() {
		logger.info("[INICIO] EXPERIMENT CONTROLLER -- all experiments");
		
		List<ExperimentDTO> list = experimentService.getExperiments();
		
		logger.info("[FINAL] EXPERIMENT CONTROLLER -- all experiments");
		return list;
	}

	

	
	
}

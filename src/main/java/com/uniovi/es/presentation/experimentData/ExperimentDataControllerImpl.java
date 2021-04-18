package com.uniovi.es.presentation.experimentData;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.SceneDTO;
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.business.dto.UserDTO;
import com.uniovi.es.business.experimentData.ExperimentDataService;
import com.uniovi.es.exceptions.ForbiddenException;

@RestController
@RequestMapping("/experimentdata")
@CrossOrigin(origins = "http://localhost:4200")
public class ExperimentDataControllerImpl implements ExperimentDataController {
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentDataControllerImpl.class);
	
	@Autowired
	private ExperimentDataService experimentDataService;

	@Override
	@RequestMapping(value = "/calculate", method = RequestMethod.POST)
	public Map<String, List<StrategyDataDTO>> getData(@RequestBody CalculateDataDTO data) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA CONTROLLER -- get data");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + data);
		
		Map<String, List<StrategyDataDTO>> map = experimentDataService.getData(data);
		
		logger.info("[FINAL] EXPERIMENT DATA CONTROLLER -- get data");
		return map;
	}

	@Override
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
	public List<UserDTO> getUsersByExperiment(@PathVariable Long id) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA CONTROLLER -- getUsersByExperiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);
		
		List<UserDTO> list = experimentDataService.getUsersByExperiment(id);
		
		logger.info("[FINAL] EXPERIMENT DATA CONTROLLER -- getUsersByExperiment");
		return list;
	}

	@Override
	@RequestMapping(value = "/strategys", method = RequestMethod.GET)
	public List<StrategyDataDTO> getStrategys() {
		logger.info("[INICIO] EXPERIMENT DATA CONTROLLER -- getStrategys");
		
		List<StrategyDataDTO> list = experimentDataService.getStrategys();
		
		logger.info("[FINAL] EXPERIMENT DATA CONTROLLER -- getStrategys");
		return list;
	}

	@Override
	@RequestMapping(value = "/scenes/{id}", method = RequestMethod.GET)
	public List<SceneDTO> getScenesByExperiment(@PathVariable Long id) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA CONTROLLER -- getScenesByExperiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);
		
		List<SceneDTO> scenes = experimentDataService.getScenesByExperiment(id);
		
		logger.info("[INICIO] EXPERIMENT DATA CONTROLLER -- getScenesByExperiment");
		return scenes;
	}

	@Override
	@RequestMapping(value = "/demographicdata/{idExperiment}", method = RequestMethod.GET)
	public List<DemographicDataDTO> getDemographicData(@PathVariable Long idExperiment) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA CONTROLLER -- getDemographicData");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + idExperiment);
		
		List<DemographicDataDTO> list = experimentDataService.getDemographicData(idExperiment);
		
		logger.info("[INICIO] EXPERIMENT DATA CONTROLLER -- getDemographicData");
		return list;
	}
	
}

package com.uniovi.es.business.experimentData;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.SceneDTO;
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.business.dto.UserDTO;
import com.uniovi.es.exceptions.ForbiddenException;

@Service
public interface ExperimentDataService {

	public Map<String, List<StrategyDataDTO>> getData(CalculateDataDTO data) throws ForbiddenException;
	
	public List<UserDTO> getUsersByExperiment(Long idExperiment) throws ForbiddenException;
	
	public List<SceneDTO> getScenesByExperiment(Long idExperiment) throws ForbiddenException;
	
	public List<StrategyDataDTO> getStrategys();
	
	public List<DemographicDataDTO> getDemographicData(Long idExperiment) throws ForbiddenException;
		
}

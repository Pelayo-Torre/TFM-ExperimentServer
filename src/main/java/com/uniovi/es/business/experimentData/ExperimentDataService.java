package com.uniovi.es.business.experimentData;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.exceptions.ForbiddenException;

@Service
public interface ExperimentDataService {

	public Map<String, List<StrategyDataDTO>> getData(CalculateDataDTO data) throws ForbiddenException;
	
}

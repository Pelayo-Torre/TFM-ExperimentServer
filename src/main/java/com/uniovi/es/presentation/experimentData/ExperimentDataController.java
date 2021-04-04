package com.uniovi.es.presentation.experimentData;

import java.util.List;
import java.util.Map;

import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.exceptions.ForbiddenException;

public interface ExperimentDataController {

	/**
	 * Recibe una lista con los strategys a ejecutar, así como la sesión y la escena a calcular los datos
	 * @param data lista de strategys, sceneID y sessionID
	 * @return Map con (ID Strategy, resultado Strategy)
	 */
	public Map<String, List<StrategyDataDTO>> getData(CalculateDataDTO data) throws ForbiddenException;
	
}

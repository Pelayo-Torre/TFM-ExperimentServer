package com.uniovi.es.presentation.experimentData;

import java.util.List;
import java.util.Map;

import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.SceneDTO;
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.business.dto.UserDTO;
import com.uniovi.es.exceptions.ForbiddenException;

public interface ExperimentDataController {

	/**
	 * Recibe una lista con los strategys a ejecutar, así como la sesión y la escena a calcular los datos
	 * @param data lista de strategys, sceneID y sessionID
	 * @return Map con (ID Strategy, resultado Strategy)
	 */
	public Map<String, List<StrategyDataDTO>> getData(CalculateDataDTO data) throws ForbiddenException;
	
	/**
	 * Devuelve la lista de usuarios que participaron en un experimento
	 * @param idExperiment identificador del experimento del que se desea ver los usuarios
	 * @return la lista de usuarios
	 * @throws ForbiddenException en caso de que el investigador no tenga permisos para ver los usuarios
	 */
	public List<UserDTO> getUsersByExperiment(Long idExperiment) throws ForbiddenException;
	
	/**
	 * Devuelve la lista de estrategias existentes para realizar los cálculos necesarios
	 * @return lista de strategys
	 */
	public List<StrategyDataDTO> getStrategys();
	
	/**
	 * Devuelve la lista de escenas registradas en un experimento
	 * @param idExperiment identificador del experimento
	 * @return lista de escenas
	 * @throws ForbiddenException en caso de que el investigador no tenga permisos para ver las escenas
	 */
	public List<SceneDTO> getScenesByExperiment(Long idExperiment) throws ForbiddenException;
	
	/**
	 * Devuelve la lista de datos demográficos a partir de un experimento
	 * @param idExperiment identificador del experimento
	 * @return la lista de datos demográficos
	 */
	public List<DemographicDataDTO> getDemographicData(Long idExperiment) throws ForbiddenException;
	
}

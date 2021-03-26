package com.uniovi.es.business.experimentData;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.experimentData.strategy.StrategyData;
import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;

@Service
public class ExperimentDataServiceImpl implements ExperimentDataService{
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentDataServiceImpl.class);

	@Override
	public Map<Integer, Double> getData(CalculateDataDTO data) {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getData");
		
		Map<Integer, Double> datos = new HashMap<Integer, Double>();
		
		if(data != null && data.sceneID != null && data.sessionID != null && data.strategys != null) {
			logger.info("\t \t Número total de strategys a ejecutar: " + data.strategys.size());
			for(Integer strategy : data.strategys) {
				logger.info("\t \t Ejecutando estrategia con identificador: " + strategy);
				StrategyData sd = StrategyDataManager.getInstance().getStrategyData(strategy);
				if(sd != null) {
					logger.info("\t \t Calculando datos estrategia con identificador: " + strategy);
					datos.put(strategy, sd.calculate(data.sceneID, data.sessionID));
				}
				else {
					logger.info("\t \t Estrategia con identificador: " + strategy + " no existente en el sistema");
					datos.put(strategy, null);
				}
			}
		}
		
		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- getData");
		return datos;
	}

	

}

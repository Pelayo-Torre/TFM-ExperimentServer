package com.uniovi.es.business.experimentData.strategy.strategies.components;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.DataStrategy;
import com.uniovi.es.utils.Constantes;
import com.uniovi.es.utils.ConstantesStrategys;

/**
 * Calcula la velocidad del ratón en función de la distancia ideal.
 * Patrón composite
 * @author pelay
 *
 */
public class MouseSpeedIdealDistance implements DataStrategy {
	
	public static final Logger logger = LoggerFactory.getLogger(MouseSpeedIdealDistance.class);

	private DataStrategy strategyTime;	//Strategia de tiempo total
	private DataStrategy strategyDistance; //Strategia de la distancia ideal

	public MouseSpeedIdealDistance(DataStrategy strategyTime, DataStrategy strategyDistance) {
		this.strategyTime = strategyTime;
		this.strategyDistance = strategyDistance;
	}
	
	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_MOUSE_SPEED_IDEAL_DISTANCE;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_speed_ideal_distance");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_speed_ideal_distance");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIAL] - MouseSpeedIdealDistance - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Double> result = new HashMap<String, Double>();
		
		//Se cálcula el tiempo de movimiento de ratón
		Map<String, Long> times = (Map<String, Long>) strategyTime.calculate(sceneID, sessionID);
		Map<String, Double> distances = (Map<String, Double>) strategyDistance.calculate(sceneID, sessionID);
		
		if(times != null && distances != null) {
			distances.forEach((key, value) -> {
				logger.info("\t \t Componente: " + key);
				if(times.containsKey(key)) {
					Double distance = value;
					if(distance == null) 
						distance = 0.0;
					logger.info("\t \t Distancia: " + distance);
					Long time = times.get(key);
					if(time == null || time == 0L) {
						logger.info("\t \t Tiempo: " + time);
						result.put(key, 0.0);
					}
					else {
						double timeD = time.doubleValue() / 1000; //Paso a segundos
						logger.info("\t \t Tiempo: " + timeD);
						result.put(key, (double)Math.round((distance / timeD) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
					}
				}
				else {
					logger.info("\t \t Tiempo no encontrado");
					result.put(key, 0.0);
				}
			});
		}
		
		logger.info("[FINAL] - MouseSpeedIdealDistance - calculate");
		return result;
	}

}

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
 * Calcula la diferencia de las distancias ideal y real calculadas en las estrategias correspondientes.
 * Se implementa el patrón composite.
 * @author pelayo
 *
 */
public class DifferenceIdealAndRealDistance implements DataStrategy {
	
	public static final Logger logger = LoggerFactory.getLogger(DifferenceIdealAndRealDistance.class);
	
	private DataStrategy strategy1;
	private DataStrategy strategy2;

	public DifferenceIdealAndRealDistance(DataStrategy strategy1, DataStrategy strategy2) {
		this.strategy1 = strategy1;
		this.strategy2 = strategy2;
	}
	
	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_DIFFERENCE_IDEAL_AND_REAL_DISTANCE;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("difference_ideal_and_real_distance");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("difference_ideal_and_real_distance");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIAL] - DifferenceIdealAndRealDistance - calculate");
		
		@SuppressWarnings("unchecked")
		Map<String, Double> real = (Map<String, Double>) this.strategy1.calculate(sceneID, sessionID);
		@SuppressWarnings("unchecked")
		Map<String, Double> ideal = (Map<String, Double>) this.strategy2.calculate(sceneID, sessionID);
		
		logger.info("\t \t Resultado strategy1: " + real);
		logger.info("\t \t Resultado strategy2: " + ideal);
		
		Map<String, Double> result = new HashMap<String, Double>();
		
		if(real != null && ideal != null) {
			real.forEach((key, value) -> {
				if(ideal.containsKey(key)) {
					Double value2 = ideal.get(key);
					result.put(key, (double)Math.round((value - value2) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
				}
				else {
					result.put(key, (double)Math.round(value * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
				}
			});
		}
		logger.info("[FINAL] - DifferenceIdealAndRealDistance - calculate");
		return result;
	}


}
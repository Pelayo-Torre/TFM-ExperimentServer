package com.uniovi.es.business.experimentData.strategy.strategys.components;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyData;
import com.uniovi.es.utils.Constantes;

public class DifferenceIdealAndRealDistance implements StrategyData {
	
	public static final Logger logger = LoggerFactory.getLogger(DifferenceIdealAndRealDistance.class);
	
	private StrategyData strategy1;
	private StrategyData strategy2;
	private Integer key;

	public DifferenceIdealAndRealDistance(Integer key, StrategyData strategy1, StrategyData strategy2) {
		this.strategy1 = strategy1;
		this.strategy2 = strategy2;
		this.key = key;
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

	@Override
	public Integer getKey() {
		return key;
	}

}

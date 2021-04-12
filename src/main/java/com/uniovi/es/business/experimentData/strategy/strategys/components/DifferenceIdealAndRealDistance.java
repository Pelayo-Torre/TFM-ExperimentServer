package com.uniovi.es.business.experimentData.strategy.strategys.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyData;

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
		
		Double result1 = (Double) this.strategy1.calculate(sceneID, sessionID);
		Double result2 = (Double) this.strategy2.calculate(sceneID, sessionID);
		
		logger.info("\t \t Resultado strategy1: " + result1);
		logger.info("\t \t Resultado strategy2: " + result2);
		
		Double distance = 0.0;
		
		if(result1 != null && result2 != null && result2 != 0.0) {
			distance = result1 - result2;
			if(distance < 0.0)
				return distance * (-1);
			return distance;
		}
		
		logger.info("[FINAL] - DifferenceIdealAndRealDistance - calculate");
		return distance;
	}

	@Override
	public Integer getKey() {
		return key;
	}

}

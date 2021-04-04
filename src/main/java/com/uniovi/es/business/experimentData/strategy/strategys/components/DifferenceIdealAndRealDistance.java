package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyData;

public class DifferenceIdealAndRealDistance implements StrategyData {
	
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
		Double result1 = (Double) this.strategy1.calculate(sceneID, sessionID);
		Double result2 = (Double) this.strategy2.calculate(sceneID, sessionID);
		
		Double distance = null;
		
		if(result1 != null && result2 != null && result2 != 0.0) {
			distance = result1 - result2;
			if(distance < 0.0)
				return distance * (-1);
			return distance;
		}
		
		return null;
	}

	@Override
	public Integer getKey() {
		return key;
	}

}

package com.uniovi.es.business.experimentData.strategy.strategys.components;

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
	public Double calculate(String sceneID, String sessionID) {
		Double result1 = this.strategy1.calculate(sceneID, sessionID);
		Double result2 = this.strategy2.calculate(sceneID, sessionID);
		
		if(result1 != null && result2 != null && result2 != 0.0)
			return result1 - result2;
		
		return null;
	}

	@Override
	public Integer getKey() {
		return key;
	}

}

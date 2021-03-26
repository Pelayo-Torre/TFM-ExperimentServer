package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.StrategyData;

public class MouseSpeedRealDistance implements StrategyData{

	private StrategyData strategy;
	private Integer key;

	public MouseSpeedRealDistance(Integer key, StrategyData strategy) {
		this.strategy = strategy;
		this.key = key;
	}

	@Override
	public Double calculate(String sceneID, String sessionID) {
		return null;
	}

	@Override
	public Integer getKey() {
		return key;
	}


}

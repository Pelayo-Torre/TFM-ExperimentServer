package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyData;

public class MouseSpeedRealDistance implements StrategyData{

	private StrategyData strategy;
	private Integer key;

	public MouseSpeedRealDistance(Integer key, StrategyData strategy) {
		this.strategy = strategy;
		this.key = key;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_speed_real_distance");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_speed_real_distance");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

	@Override
	public Integer getKey() {
		return key;
	}


}

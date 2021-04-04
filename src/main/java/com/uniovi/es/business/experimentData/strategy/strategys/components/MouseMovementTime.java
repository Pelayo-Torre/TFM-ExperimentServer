package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class MouseMovementTime extends StrategyDataAbstract{

	public MouseMovementTime(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_movement_time");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_movement_time");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class MouseAccuraccy extends StrategyDataAbstract{

	public MouseAccuraccy(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_accuracy");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_accuracy");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

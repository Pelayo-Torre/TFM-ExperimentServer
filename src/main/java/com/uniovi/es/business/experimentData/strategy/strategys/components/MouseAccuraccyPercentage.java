package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.utils.ConstantesStrategys;

public class MouseAccuraccyPercentage extends StrategyDataAbstract{

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_MOUSE_ACCURACY_PERCENTAGE;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_accuracy_percentage");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_accuracy_percentage");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

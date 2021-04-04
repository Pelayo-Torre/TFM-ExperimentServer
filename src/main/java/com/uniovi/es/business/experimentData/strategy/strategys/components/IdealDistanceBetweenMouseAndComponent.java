package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class IdealDistanceBetweenMouseAndComponent extends StrategyDataAbstract{

	public IdealDistanceBetweenMouseAndComponent(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("ideal_distance_between_mouse_and_component");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("ideal_distance_between_mouse_and_component");
	}


	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

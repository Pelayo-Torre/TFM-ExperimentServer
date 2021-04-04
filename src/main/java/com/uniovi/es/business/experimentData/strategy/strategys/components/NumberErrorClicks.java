package com.uniovi.es.business.experimentData.strategy.strategys.components;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class NumberErrorClicks extends StrategyDataAbstract{

	public NumberErrorClicks(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_error_clicks");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_error_clicks");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

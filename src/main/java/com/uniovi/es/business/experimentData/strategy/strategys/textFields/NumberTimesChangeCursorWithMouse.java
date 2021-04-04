package com.uniovi.es.business.experimentData.strategy.strategys.textFields;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class NumberTimesChangeCursorWithMouse extends StrategyDataAbstract{

	public NumberTimesChangeCursorWithMouse(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_times_change_cursor_with_mouse");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_times_change_cursor_with_mouse");
	}


	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

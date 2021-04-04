package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class NumberTimesChangedCombo extends StrategyDataAbstract{

	public NumberTimesChangedCombo(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_times_changed_combo");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_times_changed_combo");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

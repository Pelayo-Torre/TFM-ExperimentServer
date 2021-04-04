package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class OfferedOptionsCombo extends StrategyDataAbstract{

	public OfferedOptionsCombo(Integer key) {
		super(key);
	}

	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("offered_options_combobox");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("offered_options_combobox");
	}
	
	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

	
}

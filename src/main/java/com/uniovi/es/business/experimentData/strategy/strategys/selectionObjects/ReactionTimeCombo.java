package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class ReactionTimeCombo extends StrategyDataAbstract{

	public ReactionTimeCombo(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("reaction_time_combobox");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("reaction_time_combobox");
	}

	@Override
	public Double calculate(String sceneID, String sessionID) {
		return null;
	}

}

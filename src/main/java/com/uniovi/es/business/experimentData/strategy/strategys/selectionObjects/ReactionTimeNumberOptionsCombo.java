package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyData;

public class ReactionTimeNumberOptionsCombo implements StrategyData {
	
	private StrategyData strategy1;
	private StrategyData strategy2;
	private Integer key;

	public ReactionTimeNumberOptionsCombo(Integer key, StrategyData strategy1, StrategyData strategy2) {
		this.strategy1 = strategy1;
		this.strategy2 = strategy2;
		this.key = key;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("reaction_time_number_options_combo");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("reaction_time_number_options_combo");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		Double result1 = (Double) this.strategy1.calculate(sceneID, sessionID);
		Double result2 = (Double) this.strategy2.calculate(sceneID, sessionID);
		
		if(result1 != null && result2 != null && result2 != 0.0)
			return result1 / result2;
		
		return null;
	}

	@Override
	public Integer getKey() {
		return key;
	}

}

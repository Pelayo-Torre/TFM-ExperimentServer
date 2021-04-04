package com.uniovi.es.business.experimentData.strategy.strategys.textFields;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class NumberWordsDeleted extends StrategyDataAbstract{

	public NumberWordsDeleted(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_words_deleted");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_words_deleted");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}
}

package com.uniovi.es.business.experimentData.strategy.strategys.textFields;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;

public class NumberWordsPerSecond extends StrategyDataAbstract{

	public NumberWordsPerSecond(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_words_per_second");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_words_per_second");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		return null;
	}

}

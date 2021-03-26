package com.uniovi.es.business.experimentData.strategy;


public interface StrategyData {

	public Double calculate(String sceneID, String sessionID);
	
	public Integer getKey();
	
}

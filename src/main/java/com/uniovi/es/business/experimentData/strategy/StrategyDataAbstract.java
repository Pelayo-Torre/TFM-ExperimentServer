package com.uniovi.es.business.experimentData.strategy;

public abstract class StrategyDataAbstract implements StrategyData{
	
	private Integer key;
	
	public StrategyDataAbstract(Integer key) {
		this.key = key;
	}
	
	@Override
	public Integer getKey() {
		return key;
	}

}

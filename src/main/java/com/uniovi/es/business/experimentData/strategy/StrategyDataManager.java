package com.uniovi.es.business.experimentData.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Se trata de una clase que implementa el patrón Singleton. 
 * Es la clase encargada de gestionar las diferentes estrategias de cálculo de datos de un experimento
 * @author pelayo
 *
 */
public class StrategyDataManager {

	private static StrategyDataManager instance;
	
	private Map<Integer, StrategyData> strategies = new HashMap<Integer, StrategyData>();
	
	private StrategyDataManager() {}
	
	public static StrategyDataManager getInstance() {
		if(instance == null) {
			instance = new StrategyDataManager();
		}
		return instance;
	}
	
	public StrategyData getStrategyData(Integer key) {
		return this.strategies.get(key);
	}
	
	public boolean hasStrategyData(Integer key) {
		return this.strategies.containsKey(key);
	}
	
	public void addStrategyData(StrategyData strategy) {
		if(!hasStrategyData(strategy.getKey()))
			this.strategies.put(strategy.getKey(), strategy);
	}

	public Map<Integer, StrategyData> getStrategys() {
		return strategies;
	}
		
}

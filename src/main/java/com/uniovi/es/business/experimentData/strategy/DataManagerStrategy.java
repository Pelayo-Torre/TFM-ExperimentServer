package com.uniovi.es.business.experimentData.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Se trata de una clase que implementa el patrón Singleton. 
 * Es la clase encargada de gestionar las diferentes estrategias de cálculo de datos de un experimento
 * @author pelayo
 *
 */
public class DataManagerStrategy {

	private static DataManagerStrategy instance;
	
	private Map<Integer, DataStrategy> strategies = new HashMap<Integer, DataStrategy>();
	
	private DataManagerStrategy() {}
	
	public static DataManagerStrategy getInstance() {
		if(instance == null) {
			instance = new DataManagerStrategy();
		}
		return instance;
	}
	
	public DataStrategy getStrategyData(Integer key) {
		return this.strategies.get(key);
	}
	
	public boolean hasStrategyData(Integer key) {
		return this.strategies.containsKey(key);
	}
	
	public void addStrategyData(DataStrategy strategy) {
		if(!hasStrategyData(strategy.getKey()))
			this.strategies.put(strategy.getKey(), strategy);
	}

	public Map<Integer, DataStrategy> getStrategys() {
		return strategies;
	}
		
}

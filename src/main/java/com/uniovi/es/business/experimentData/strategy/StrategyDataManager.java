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

	private static StrategyDataManager instance = new StrategyDataManager();
	
	private Map<Integer, StrategyData> strategys = new HashMap<Integer, StrategyData>();
	
	private StrategyDataManager() {}
	
	public static StrategyDataManager getInstance() {
		return instance;
	}
	
	public StrategyData getStrategyData(Integer key) {
		return this.strategys.get(key);
	}
	
	public boolean hasStrategyData(Integer key) {
		return this.strategys.containsKey(key);
	}
	
	public void addStrategyData(StrategyData strategy) {
		if(hasStrategyData(strategy.getKey()))
			this.strategys.put(strategy.getKey(), strategy);
	}
	
}

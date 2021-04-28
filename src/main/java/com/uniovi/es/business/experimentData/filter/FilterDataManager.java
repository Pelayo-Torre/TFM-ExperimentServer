package com.uniovi.es.business.experimentData.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase implemementa el patrón de diseño Singleton. 
 * Es la clase encargada de gestionar los diferentes filtros de datos de un experimento
 * @author pelayo
 *
 */
public class FilterDataManager {

	private static FilterDataManager instance;
	
	private Map<Integer, FilterData> filters = new HashMap<Integer, FilterData>();
	
	private FilterDataManager() {}
	
	public static FilterDataManager getInstance() {
		if(instance == null) {
			instance = new FilterDataManager();
		}
		return instance;
	}
	
	public boolean hasFilterData(Integer key) {
		return this.filters.containsKey(key);
	}
	
	public void addStrategyData(FilterData filter) {
		if(!hasFilterData(filter.getKey()))
			this.filters.put(filter.getKey(), filter);
	}

	public Map<Integer, FilterData> getFilters() {
		return filters;
	}
	
}

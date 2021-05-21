package com.uniovi.es.business.experimentData.filter;

import java.util.HashMap;
import java.util.Map;


/**
 * Esta clase implemementa el patrón de diseño Singleton. 
 * Es la clase encargada de gestionar los diferentes filtros de datos de un experimento
 * @author pelayo
 *
 */
public class DataManagerFilter {

	private static DataManagerFilter instance;
	
	private Map<Integer, DataFilter> filters = new HashMap<Integer, DataFilter>();
	
	private DataManagerFilter() {}
	
	public static DataManagerFilter getInstance() {
		if(instance == null) {
			instance = new DataManagerFilter();
		}
		return instance;
	}
	
	public DataFilter getFilterData(Integer key) {
		return this.filters.get(key);
	}
	
	public boolean hasFilterData(Integer key) {
		return this.filters.containsKey(key);
	}
	
	public void addStrategyData(DataFilter filter) {
		if(!hasFilterData(filter.getKey()))
			this.filters.put(filter.getKey(), filter);
	}

	public Map<Integer, DataFilter> getFilters() {
		return filters;
	}
	
}

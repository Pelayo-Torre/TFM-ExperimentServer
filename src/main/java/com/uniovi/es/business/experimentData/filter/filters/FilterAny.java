package com.uniovi.es.business.experimentData.filter.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.filter.FilterData;
import com.uniovi.es.business.experimentData.filter.PropertiesFilterManager;

/**
 * Clase que implementa el patrón composite para aplicar al menos un filtro.
 * @author pelayo
 *
 */
public class FilterAny implements FilterData{
	
	public static final Logger logger = LoggerFactory.getLogger(FilterAny.class);
	
	private Integer key;
	private FilterData [] filters;
	
	public FilterAny(Integer key, FilterData...filterDatas) {
		this.key = key;
		this.filters = filterDatas;
	}

	@Override
	public Integer getKey() {
		return key;
	}
	
	@Override
	public String getName() {
		return PropertiesFilterManager.getInstance().getNameStrategysProperties().getProperty("filter_any");
	}

	@Override
	public boolean isValid(String sceneID, String sessionID) {
		logger.info("[INICIAL] - FilterAny - isValid");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		if(filters != null) {
			logger.info("\t \t Número de filtros a aplicar: " + filters.length);
			for(FilterData filter : filters) {
				if(filter != null) {
					if(filter.isValid(sceneID, sessionID)) {
						logger.info("\t \t El filtro : " + filter.getName() + " ha pasado la validación.");
						return true;
					}
				}
				else {
					logger.info("\t \t filtro nulo");
					return false;
				}
			}
		}
		else {
			logger.info("\t \t Lista de filtros de entrada nula");
			return false;
		}
		
		logger.info("[FINAL] - FilterAny - isValid");
		return false;
	}

}

package com.uniovi.es.business.experimentData.filter.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.filter.DataFilter;
import com.uniovi.es.business.experimentData.filter.PropertiesFilterManager;
import com.uniovi.es.utils.ConstantesFilters;

/**
 * Clase que implementa el patrón composite para aplicar al menos un filtro.
 * @author pelayo
 *
 */
public class AnyFilter implements DataFilter{
	
	public static final Logger logger = LoggerFactory.getLogger(AnyFilter.class);
	
	private DataFilter [] filters;
	
	public AnyFilter(DataFilter...filterDatas) {
		this.filters = filterDatas;
	}

	@Override
	public Integer getKey() {
		return ConstantesFilters.FILTER_ANY;
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
			for(DataFilter filter : filters) {
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

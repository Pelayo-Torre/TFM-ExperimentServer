package com.uniovi.es.business.experimentData.filter.filters;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.uniovi.es.business.experimentData.filter.FilterData;
import com.uniovi.es.business.experimentData.filter.PropertiesFilterManager;
import com.uniovi.es.model.DemographicData;
import com.uniovi.es.model.DemographicDataDate;
import com.uniovi.es.model.DemographicDataNumber;
import com.uniovi.es.model.DemographicDataString;
import com.uniovi.es.model.User;
import com.uniovi.es.persistence.UserDAO;
import com.uniovi.es.utils.ConstantesFilters;

@Component
public class FilterFullDemographicData implements FilterData{
	
	@Autowired
	private UserDAO userDAO;
	
	public static final Logger logger = LoggerFactory.getLogger(FilterFullDemographicData.class);

	@Override
	public boolean isValid(String sceneID, String sessionID) {
		logger.info("[INICIAL] - FilterFullDemographicData - isValid");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		//Se obtiene el experimento asociado al usuario
		User user = userDAO.getUserBySessionId(sessionID);
		Set<DemographicData> list = user.getExperiment().getDemographicData();
		logger.info("\t \t Número total de datos demográficos del experimento: " + list.size());
		
		int cont = 0;
		for(DemographicData dd : list) {
			for(DemographicDataDate date : dd.getDemographicDataDate()) {
				if(date.getUser().getSessionId().equals(sessionID)) {
					cont++;
					break;
				}
			}
			
			for(DemographicDataNumber number : dd.getDemographicDataNumber()) {
				if(number.getUser().getSessionId().equals(sessionID)) {
					cont++;
					break;
				}
			}
			
			for(DemographicDataString string : dd.getDemographicDataString()) {
				if(string.getUser().getSessionId().equals(sessionID)) {
					cont++;
					break;
				}
			}
		}
		
		logger.info("\t \t Número total de datos demográficos obtenidos: " + cont);
		
		boolean valid = false;
		if(cont == list.size())
			valid = true;
		
		logger.info("[FINAL] - FilterFullDemographicData - isValid");
		return valid;
	}

	@Override
	public String getName() {
		return PropertiesFilterManager.getInstance().getNameStrategysProperties().getProperty("filter_full_demographic_data");
	}

	@Override
	public Integer getKey() {
		return ConstantesFilters.FILTER_FULL_DEMOGRAPHIC_DATA;
	}
	
}

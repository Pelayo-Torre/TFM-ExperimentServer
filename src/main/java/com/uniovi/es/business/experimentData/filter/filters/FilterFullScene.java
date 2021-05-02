package com.uniovi.es.business.experimentData.filter.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.filter.FilterData;
import com.uniovi.es.business.experimentData.filter.PropertiesFilterManager;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;
import com.uniovi.es.utils.ConstantesFilters;

public class FilterFullScene implements FilterData{

	public static final Logger logger = LoggerFactory.getLogger(FilterFullScene.class);
	
	@Override
	public String getName() {
		return PropertiesFilterManager.getInstance().getNameStrategysProperties().getProperty("filter_full_scene");
	}
	
	@Override
	public Integer getKey() {
		return ConstantesFilters.FILTER_FULL_SCENE;
	}

	@Override
	public boolean isValid(String sceneID, String sessionID) {
		logger.info("[INICIAL] - FilterFullScene - isValid");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Event initial = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
				null, Constantes.EVENT_INIT_TRACKING, null);
		
		Event finish = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
				null, Constantes.EVENT_TRACKIND_END, null);
		
		if(initial == null || finish == null) {
			logger.info("\t \t Eventos de inicio o final de escena nulos ");
			initial = null;
			finish = null;
			return false;
		}
		
		initial = null;
		finish = null;
		logger.info("[FINAL] - FilterFullScene - isValid");
		return true;
	}
}

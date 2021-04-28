package com.uniovi.es.business.experimentData.filter.filters;

import com.uniovi.es.business.experimentData.filter.FilterDataAbstract;
import com.uniovi.es.business.experimentData.filter.PropertiesFilterManager;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;

public class FilterFullScene extends FilterDataAbstract{

	public FilterFullScene(Integer key) {
		super(key);
	}
	
	@Override
	public String getName() {
		return PropertiesFilterManager.getInstance().getNameStrategysProperties().getProperty("filter_full_scene");
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

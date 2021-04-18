package com.uniovi.es.business.experimentData.strategy.strategys;

import java.util.List;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;

public class NumberErrorClicks extends StrategyDataAbstract{

	public NumberErrorClicks(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_error_clicks");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_error_clicks");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIAL] - NumberErrorClicks - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Integer result = 0;
		List<Event> events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, "-1",
				null, null, null, Constantes.EVENT_ON_CLICK, Constantes.EVENT_ON_DOUBLE_CLICK);
		
		if(events != null)
			result = events.size();
		
		logger.info("\t \t Número de eventos de click erróneos obtenidos: " + result);
		
		logger.info("[FINAL] - NumberErrorClicks - calculate");
		return result;
	}

}

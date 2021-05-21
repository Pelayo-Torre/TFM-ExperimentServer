package com.uniovi.es.business.experimentData.strategy.strategys;

import java.util.List;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.DataStrategyAbstract;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;
import com.uniovi.es.utils.ConstantesStrategys;

/**
 * Estrategia que se encarga de calcular el número de clicks "erróneos", es decir, clicks que no se han
 * producido en ninguni de los componentes registrados
 * @author pelayo
 *
 */
public class NumberErrorClicks extends DataStrategyAbstract{

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_NUMBER_ERROR_CLICKS;
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
		
		//Se descartan los eventos
		events = null;
		
		logger.info("[FINAL] - NumberErrorClicks - calculate");
		return result;
	}

}

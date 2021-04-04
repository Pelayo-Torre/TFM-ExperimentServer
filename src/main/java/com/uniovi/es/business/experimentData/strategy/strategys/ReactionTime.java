package com.uniovi.es.business.experimentData.strategy.strategys;

import java.sql.Timestamp;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;

/**
 * Calcula el tiempo de reacción del usuario desde que carga la escena y se produce el 1º evento.
 * @author pelayo
 *
 */
public class ReactionTime extends StrategyDataAbstract{
	
	public ReactionTime(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("reaction_time");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("reaction_time");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIAL] - ReactionTime - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
				
		logger.info("kkkkkkkkkkkkkkkkkkkkkkkk " + getPropertyName());
		
		Event initial = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, null, null);
		Event mouse = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, null, Constantes.EVENT_ON_MOUSE_MOVE);
		
		Long result = null;
		
		if(initial != null && mouse != null) {
			result = new Timestamp(mouse.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		}
		
		logger.info("\t \t Resultado obtenido: " + result);
		
		logger.info("[FINAL] - ReactionTime - calculate");
		return result;
	}
	
}

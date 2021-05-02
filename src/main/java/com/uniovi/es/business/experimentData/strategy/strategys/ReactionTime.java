package com.uniovi.es.business.experimentData.strategy.strategys;

import java.sql.Timestamp;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.ConstantesStrategys;

/**
 * Calcula el tiempo de reacción del usuario desde que carga la escena y se produce el 1º evento.
 * @author pelayo
 *
 */
public class ReactionTime extends StrategyDataAbstract{
	
	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_REACTION_TIME;
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
						
		Event initial = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, null, null, null);
		Event initialUser = ExperimentDataFactory.getEventDAO().getInitialEventOfUser(sceneID, sessionID);
		
		Long result = 0L;
		
		if(initial != null && initialUser != null) {
			result = new Timestamp(initialUser.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		}
		
		logger.info("\t \t Resultado obtenido: " + result);
		
		//Se descartan los eventos
		initial = null;
		initialUser = null;
				
		logger.info("[FINAL] - ReactionTime - calculate");
		return result;
	}
	
}

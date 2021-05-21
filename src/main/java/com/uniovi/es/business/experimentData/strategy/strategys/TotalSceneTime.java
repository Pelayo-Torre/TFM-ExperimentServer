package com.uniovi.es.business.experimentData.strategy.strategys;

import java.sql.Timestamp;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.DataStrategyAbstract;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.ConstantesStrategys;

/**
 * Calcula el tiempo total que el usuario se encuentra en una escena
 * Devuelve la diferencia en milisegundos
 * @author pelayo
 *
 */
public class TotalSceneTime extends DataStrategyAbstract{
	
	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_TOTAL_SCENE_TIME;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("total_scene_time");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("total_scene_time");
	}


	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIAL] - TotalSceneTime - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		//Obtenemos el primer y último evento de escena
		Event initial = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, null, null, null);
		Event last = ExperimentDataFactory.getEventDAO().getFinalEvent(sceneID, sessionID, null, null, null);
		
		Long result = 0L;
		
		if(initial != null && last != null) {
			result = new Timestamp(last.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		}
		
		logger.info("\t \t Resultado obtenido: " + result);
		
		//Se descartan los eventos
		initial = null;
		last = null;
		
		logger.info("[FINAL] - TotalSceneTime - calculate");
		return result;
	}

}

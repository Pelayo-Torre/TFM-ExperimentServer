package com.uniovi.es.business.experimentData.strategy.strategys.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.ComponentData;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;

public class RealDistanceBetweenMouseAndComponent extends StrategyDataAbstract{

	public RealDistanceBetweenMouseAndComponent(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("real_distance_between_mouse_and_component");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("real_distance_between_mouse_and_component");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIAL] - RealDistanceBetweenMouseAndComponent - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		List<ComponentData> components = ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null);
		
		if(components != null) {
			logger.info("\t \t Número de componentes obtenidos: " + components.size());
			for(ComponentData component : components) {
				logger.info("\t \t Componente: " + component);
								
				Event initial = ExperimentDataFactory.getEventDAO().getInitialEvent(
						sceneID, sessionID, null, Constantes.EVENT_ON_MOUSE_MOVE);
				Event last = ExperimentDataFactory.getEventDAO().getInitialEvent(
						sceneID, sessionID, component.getComponentId(), Constantes.EVENT_ON_CLICK);
				
				if(initial != null && last != null) {
					logger.info("\t \t Eventos encontrados: INITIAL - " + initial + "  FINAL - " + last);
					List<Event> eventos = ExperimentDataFactory.getEventDAO().getEvents(
							sceneID, sessionID, null, initial.getTimeStamp(), last.getTimeStamp(), null);
					
					if(eventos != null) {
						logger.info("\t \t Número de eventos encontrados: " + eventos.size());
						result.put(component.getComponentId(), eventos.size());
					}
				}
				else {
					logger.info("\t \t Eventos initial y last no encontrados");
				}				
			}
		}
		
		logger.info("[FINAL] - RealDistanceBetweenMouseAndComponent - calculate");
		return result;
	}

}

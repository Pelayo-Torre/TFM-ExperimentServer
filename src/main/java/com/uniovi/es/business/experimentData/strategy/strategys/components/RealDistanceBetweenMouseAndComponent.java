package com.uniovi.es.business.experimentData.strategy.strategys.components;

import java.util.ArrayList;
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
		
		Map<String, Double> result = new HashMap<String, Double>();
		List<ComponentData> components = ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null);
		
		if(components.size() > 0) {
			logger.info("\t \t Número de componentes obtenidos: " + components.size());
			for(ComponentData component : components) {
				logger.info("\t \t Se obtiene la lista de eventos entre el 1º evento de movimiento y el de click sobre el Componente: " + component);
				
				Event initialMouse = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						null, Constantes.EVENT_ON_MOUSE_MOVE, null);
				Event initialClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						component.getComponentId(), Constantes.EVENT_ON_CLICK, null);
				Event initialDoubleClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						component.getComponentId(), Constantes.EVENT_ON_DOUBLE_CLICK, null);
				
				logger.info("\t \t Eventos obtenidos: INITIAL: " + initialMouse 
						+ "  INITIAL-CLICK-COMPONENT: " + initialClickComponent + "  INITIAL-DOUBLE-CLICK-COMPONENT: " + initialDoubleClickComponent);
				
				Double distance = 0.00;
				if(initialMouse != null) {
					if(initialClickComponent != null || initialDoubleClickComponent != null) {
						List<Event> events = new ArrayList<Event>();
						if(initialClickComponent != null) {
							logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de un click");
							events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
									null, initialMouse.getTimeStamp(), initialClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						}
						else {
							logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de doble click");
							events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
									null, initialMouse.getTimeStamp(), initialDoubleClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						}
						logger.info("\t \t Número de eventos obtenidos: " + events.size());
						//Se calculan las distancias
						if(events.size() > 0) {
							for(int i=0; i<events.size(); i++) {
								if(i+1 != events.size()) {
									double d = distance(events.get(i).getX(), events.get(i).getY(),
											events.get(i+1).getX(), events.get(i+1).getY());
									distance += d;
								}
							}
							logger.info("\t \t Distancia: " + distance);
						}
					}
				}
				result.put(component.getComponentId(), distance);
			}
		}
		logger.info("[FINAL] - RealDistanceBetweenMouseAndComponent - calculate");
		return result;
	}
}

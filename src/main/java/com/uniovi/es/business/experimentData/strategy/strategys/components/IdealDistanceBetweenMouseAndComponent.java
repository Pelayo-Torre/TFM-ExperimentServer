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

public class IdealDistanceBetweenMouseAndComponent extends StrategyDataAbstract{

	public IdealDistanceBetweenMouseAndComponent(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("ideal_distance_between_mouse_and_component");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("ideal_distance_between_mouse_and_component");
	}


	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - IdealDistanceBetweenMouseAndComponent - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Double> result = new HashMap<String, Double>();
		
		//Obtener todos los componentes para la escena y la sesión dadas
		List<ComponentData> components = new ArrayList<ComponentData>();
		
		components.addAll(
			ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null)
		);
			
		logger.info("\t \t Número de componentes: " + components.size());
		
		for(ComponentData component : components) {
			logger.info("\t \t Componente: " + component.getComponentId());
			logger.info("\t \t Obtenemos los primeros evento de ratón para saber la posición inicial del usuario");
			
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
					if(initialClickComponent != null) {
						distance = distance(initialMouse.getX(), initialMouse.getY(), 
								initialClickComponent.getX(), initialClickComponent.getY());
					}
					else {
						distance = distance(initialMouse.getX(), initialMouse.getY(), 
								initialDoubleClickComponent.getX(), initialDoubleClickComponent.getY());
					}
				}
			}
			result.put(component.getComponentId(), (double)Math.round(distance * 100d) / 100d);
			logger.info("\t \t Resultado distancia con componente " + component.getComponentId() + ": " + distance);			
		}
		
		logger.info("[FINAL] - IdealDistanceBetweenMouseAndComponent - calculate");
		return result;
	}

}

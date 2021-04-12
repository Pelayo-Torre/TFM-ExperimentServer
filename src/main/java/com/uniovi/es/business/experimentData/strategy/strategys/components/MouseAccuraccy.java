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

public class MouseAccuraccy extends StrategyDataAbstract{

	public MouseAccuraccy(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_accuracy");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_accuracy");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - MouseAccuraccy - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Double> result = new HashMap<String, Double>();
		List<ComponentData> components = ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null);
		
		if(components.size() > 0) {
			logger.info("\t \t Número de componentes obtenidos: " + components.size());
			for(ComponentData component : components) {
				logger.info("\t \t Se obtiene el evento del click sobre el Componente: " + component);
				
				Event initialClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						component.getComponentId(), Constantes.EVENT_ON_CLICK, null);
				Event initialDoubleClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						component.getComponentId(), Constantes.EVENT_ON_DOUBLE_CLICK, null);
				
				logger.info("\t \t Eventos obtenidos: INITIAL-CLICK-COMPONENT: " + initialClickComponent + 
						"  INITIAL-DOUBLE-CLICK-COMPONENT: " + initialDoubleClickComponent);
				
				Double accuraccy = 0.0;
				
				if(initialClickComponent != null) {
					accuraccy = distance(initialClickComponent.getX(), initialClickComponent.getY(), 
							component.getX(), component.getY());
				}
				else if(initialDoubleClickComponent != null) {
					accuraccy = distance(initialDoubleClickComponent.getX(), initialDoubleClickComponent.getY(), 
							component.getX(), component.getY());
				}
				
				result.put(component.getComponentId(), accuraccy);
				logger.info("\t \t Precisión obtenida: ");
			}
		}
		
		logger.info("[FINAL] - MouseAccuraccy - calculate");
		return result;
	}

}

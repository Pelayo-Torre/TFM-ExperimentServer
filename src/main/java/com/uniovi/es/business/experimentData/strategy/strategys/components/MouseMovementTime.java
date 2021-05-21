package com.uniovi.es.business.experimentData.strategy.strategys.components;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.DataStrategyAbstract;
import com.uniovi.es.model.ComponentData;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;
import com.uniovi.es.utils.ConstantesStrategys;

/**
 * Calcula el tiempo de movimiento del ratón desde el primer evento hasta el click sobre el componente
 * @author pelayo
 *
 */
public class MouseMovementTime extends DataStrategyAbstract{

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_MOUSE_MOVEMENT_TIME;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_movement_time");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_movement_time");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - MouseMovementTime - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Long> result = new HashMap<String, Long>();
		List<ComponentData> components = ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null);
		
		logger.info("\t \t Número de componentes obtenidos: " + components.size());
		for(ComponentData component : components) {
			logger.info("\t \t Se obtiene la lista de eventos entre el 1º evento de movimiento y el de click sobre el Componente: " + component.getComponentId());
			
			Event initialMouse = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
					null, Constantes.EVENT_ON_MOUSE_MOVE, null);
			Event initialClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
					component.getComponentId(), Constantes.EVENT_ON_CLICK, null);
			Event initialDoubleClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
					component.getComponentId(), Constantes.EVENT_ON_DOUBLE_CLICK, null);
			
			logger.info("\t \t Eventos obtenidos: INITIAL: " + initialMouse 
					+ "  INITIAL-CLICK-COMPONENT: " + initialClickComponent + "  INITIAL-DOUBLE-CLICK-COMPONENT: " + initialDoubleClickComponent);
			
			Long time = 0L;
			if(initialMouse != null) {
				if(initialClickComponent != null && initialDoubleClickComponent != null) {
					if(initialClickComponent.getTimeStamp() < initialDoubleClickComponent.getTimeStamp()) {
						logger.info("\t \t Se calcula el tiempo desde el inicial hasta el evento de un click");
						time = new Timestamp(initialClickComponent.getTimeStamp()).getTime() - 
								new Timestamp(initialMouse.getTimeStamp()).getTime();
					}
					else {
						logger.info("\t \t Se calcula el tiempo desde el inicial hasta el evento de doble click");
						time = new Timestamp(initialDoubleClickComponent.getTimeStamp()).getTime() - 
								new Timestamp(initialMouse.getTimeStamp()).getTime();
					}
				}
				else {
					if(initialClickComponent != null) {
						logger.info("\t \t Se calcula el tiempo desde el inicial hasta el evento de un click");
						time = new Timestamp(initialClickComponent.getTimeStamp()).getTime() - 
								new Timestamp(initialMouse.getTimeStamp()).getTime();
					}
					else if(initialDoubleClickComponent != null){
						logger.info("\t \t Se calcula el tiempo desde el inicial hasta el evento de doble click");
						time = new Timestamp(initialDoubleClickComponent.getTimeStamp()).getTime() - 
								new Timestamp(initialMouse.getTimeStamp()).getTime();
					}
				}
			}
			result.put(component.getComponentId(), time);
			initialMouse = null;
			initialClickComponent = null;
			initialDoubleClickComponent = null;
		}
		
		components = null;
		logger.info("[FINAL] - MouseMovementTime - calculate");
		return result;
	}

}

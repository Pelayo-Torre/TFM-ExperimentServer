package com.uniovi.es.business.experimentData.strategy.strategies.components;

import java.util.ArrayList;
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
 * Estrategia que calcula la distancia ideal entre la posición inicial del ratón y un componente.
 * @author pelayo
 *
 */
public class IdealDistanceBetweenMouseAndComponent extends DataStrategyAbstract{

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_IDEAL_DISTANCE_BETWEEN_MOUSE_AND_COMPONENT;
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
			
			Event initialMouse = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
					null, Constantes.EVENT_ON_MOUSE_MOVE, null);
			
			logger.info("\t \t Posición del componente: (" + component.getX() + "," + component.getY() + ")");
			
			Double distance = 0.00;
			if(initialMouse != null) {
				if(component.getX() != null && component.getY() != null) {
					logger.info("\t \t Se calcula la distance desde el inicial hasta la posición del componente");
					distance = distance(initialMouse.getX(), initialMouse.getY(), 
							component.getX(), component.getY());
				}
			}
			result.put(component.getComponentId(), (double)Math.round(distance * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
			logger.info("\t \t Resultado distancia con componente " + component.getComponentId() + ": " + distance);	
			initialMouse = null;
		}
		components = null;
		logger.info("[FINAL] - IdealDistanceBetweenMouseAndComponent - calculate");
		return result;
	}

}

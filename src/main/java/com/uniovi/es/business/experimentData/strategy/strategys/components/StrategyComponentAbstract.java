package com.uniovi.es.business.experimentData.strategy.strategys.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.strategy.StrategyData;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.ComponentData;
import com.uniovi.es.model.Event;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;

/**
 * Clase abstracta creada para evitar la repetición de código. 
 * Esta clase implementa el patrón de diseño Template Method para el método calcular
 * La mayoría de estrategias para el cálculo de datos sobre componentes utilizan el mismo cuerpo del mñetodo calculate:
 * 
 * @author pelay
 *
 */
public abstract class StrategyComponentAbstract implements StrategyData{

	public static final Logger logger = LoggerFactory.getLogger(StrategyDataAbstract.class);
	
	private Integer key;
	
	public StrategyComponentAbstract(Integer key) {
		this.key = key;
	}
	
	@Override
	public Integer getKey() {
		return key;
	}
	
	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - IdealDistanceBetweenMouseAndComponent - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Object> result = new HashMap<String, Object>();
		
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
		
			result.put(component.getComponentId(), null);
		}
		
		return null;
		
	}
	
	protected abstract void calculate(ComponentData component, Event initial, Event fin);
	
}

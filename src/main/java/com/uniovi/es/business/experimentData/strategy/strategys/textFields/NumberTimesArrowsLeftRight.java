package com.uniovi.es.business.experimentData.strategy.strategys.textFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.ComponentData;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;

public class NumberTimesArrowsLeftRight extends StrategyDataAbstract{

	public NumberTimesArrowsLeftRight(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_times_arrows_left_right");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_times_arrows_left_right");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - NumberTimesArrowsLeftRight - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.addAll(
			ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, Constantes.COMPONENT_TEXT_FIELD)
		);
		
		logger.info("\t \t Número de componentes: " + components.size());
		for(ComponentData component : components) {
			Integer times = 0;
			
			times += ExperimentDataFactory.getEventDAO().getEvents(
					sceneID, sessionID, component.getComponentId(), null, null, Constantes.EVENT_ARROW_LEFT, Constantes.EVENT_KEY_DOWN).size();
			
			times += ExperimentDataFactory.getEventDAO().getEvents(
					sceneID, sessionID, component.getComponentId(), null, null, Constantes.EVENT_ARROW_RIGHT, Constantes.EVENT_KEY_DOWN).size();
			
			logger.info("\t \t Número de teclas izquierda-derecha para el componente " + component.getComponentId() + ": " + times);
			result.put(component.getComponentId(), times);
		}
		
		
		logger.info("[FINAL] - NumberTimesArrowsLeftRight - calculate");
		return result;
	}

}

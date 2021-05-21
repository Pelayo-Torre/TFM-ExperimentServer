package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

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
 * Estrategia que calcula el número de veces que se cambió de opinión en los componentes de selección 
 * radio botones y check boxes.
 * @author pelayo
 *
 */
public class NumberTimesChangedSelectionObject extends DataStrategyAbstract{

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_NUMBER_TIMES_CHANGED_SELECTION_OBJECT;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_times_changed_combo");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_times_changed_combo");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - NumberTimesChangedSelectionObject - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		//Se obtienen todos los tipos de componentes de selección salvo los comboboxes
		List<ComponentData> components = new ArrayList<ComponentData>();
				
		components.addAll(
				ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null,
						Constantes.COMPONENT_CHECK_BOX, Constantes.COMPONENT_RADIO_BUTTON));
				
		logger.info("\t \t Lista de componentes obtenidos: " + components.size());
		
		for(ComponentData component: components) {
			logger.info("\t \t Componente: " + component.getComponentId());
			
			List<Event> events = ExperimentDataFactory.getEventDAO().getEventOfSelectionObject(sceneID, sessionID,
					component.getComponentId(), Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, null);
			
			logger.info("\t \t Lista de eventos de tipo onChange " + events.size());
			//Se obtiene el número de eventos -1 , ya que el primer onChange es el de la 1º selección
			result.put(component.getComponentId(), (events.size() - 1 >= 0)?events.size() - 1:0);
			
			events = null;			
		}
		components = null;		
		logger.info("[FINAL] - NumberTimesChangedSelectionObject - calculate");
		return result;
	}

}

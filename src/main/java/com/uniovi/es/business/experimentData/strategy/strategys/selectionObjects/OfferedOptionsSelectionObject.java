package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.ComponentData;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;

/**
 * Calcula el número de opciones ofrecidas por un elemento de selección. 
 * Actualmente, la aplicación cuenta con los siguientes elementos de selección:
 * 		ComboBox
 * 		RadioButtons
 * 		CheckBox
 * @author pelayo
 *
 */
public class OfferedOptionsSelectionObject extends StrategyDataAbstract{

	public OfferedOptionsSelectionObject(Integer key) {
		super(key);
	}

	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("offered_options_combobox");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("offered_options_combobox");
	}
	
	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - OfferedOptionsSelectionObject - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		//Se obtienen todos los los tipos de componentes de selección
		List<ComponentData> components = new ArrayList<ComponentData>();
				
		components.addAll(
				ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null,
						Constantes.COMPONENT_COMBOBOX, Constantes.COMPONENT_CHECK_BOX, Constantes.COMPONENT_RADIO_BUTTON));
		
		logger.info("\t \t Lista de componentes obtenidos: " + components.size());
		
		for(ComponentData component : components) {
			Integer numberOptions = 0;
			List<ComponentData> options = ExperimentDataFactory.getSceneComponentDAO().getComponents(
					sceneID, sessionID, component.getComponentId());
			logger.info("\t \t Numero de opciones: " + numberOptions);
			if(options != null) {
				numberOptions = options.size();
			}
			result.put(component.getComponentId(), numberOptions);
		}
		
		logger.info("[FINAL] - OfferedOptionsSelectionObject - calculate");
		return result;
	}

	
}

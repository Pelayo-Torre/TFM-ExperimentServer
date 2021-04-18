package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

import java.sql.Timestamp;
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

public class ReactionTimeSelectionObject extends StrategyDataAbstract{

	public ReactionTimeSelectionObject(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("reaction_time_combobox");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("reaction_time_combobox");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - OfferedOptionsSelectionObject - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Long> result = new HashMap<String, Long>();
		
		//Se obtienen todos los tipos de componentes de selección
		List<ComponentData> components = new ArrayList<ComponentData>();
				
		components.addAll(
				ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null,
						Constantes.COMPONENT_COMBOBOX, Constantes.COMPONENT_CHECK_BOX, Constantes.COMPONENT_RADIO_BUTTON));
		
		logger.info("\t \t Lista de componentes obtenidos: " + components.size());
		
		//Se obtiene primer evento de escena
		Event initial = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, null, null, null);
		
		logger.info("\t \t Primer evento de escena: " + initial);
		
		for(ComponentData component : components) {
			logger.info("\t \t Componente: " + component.getComponentId());
			Long time = 0L;
			if(component.getTypeId() == Constantes.COMPONENT_COMBOBOX) {
				time = calculateComboBox(sceneID, sessionID, component);
			}
			else {
				time = calculateOtherSelectionObject(sceneID, sessionID, component, initial);
			}
			
			logger.info("\t \t tiempo de reacción: " + time);
			result.put(component.getComponentId(), time);
		}
		
		return result;
	}

	/**
	 * Calcula el tiempo de reacción para un componente de tipo ComboBox
	 * @param sceneID Identificador de la escena
	 * @param sessionID Identificador de la sesión
	 * @param component componente
	 * @param time tiempo
	 * @return
	 */
	private Long calculateComboBox(String sceneID, String sessionID, ComponentData component) {
		//Si es un combo, el primer evento es del click sobre el combo para cargar las opciones
		//Puede que el click sea haga con el ratón y con el teclado
		Event eventMouse = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
				component.getComponentId(), Constantes.EVENT_ON_CLICK_SELECTION_OBJECT, null);
		
		Event eventKeyPress = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
				component.getComponentId(), Constantes.EVENT_KEY_PRESS, Constantes.EVENT_ENTER);
		
		Event eventOnChange = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
				component.getComponentId(), Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, null);
		
		logger.info("\t \t Eventos obtenidos: EVENTMOUSE: " + eventMouse + " - EVENTKEYPRESS: " + eventKeyPress + 
				" - EVENTONCHANGE: " + eventOnChange);
		
		if(eventOnChange != null) {
			if(eventKeyPress != null && eventMouse != null) {
				//Se coge el de menor tiempo
				if(eventKeyPress.getTimeStamp() > eventMouse.getTimeStamp()) {
					return new Timestamp(eventOnChange.getTimeStamp()).getTime() - new Timestamp(eventMouse.getTimeStamp()).getTime();
				}
				else {
					return new Timestamp(eventOnChange.getTimeStamp()).getTime() - new Timestamp(eventKeyPress.getTimeStamp()).getTime();
				}
			}
			else if(eventMouse != null && eventKeyPress == null) {
				return new Timestamp(eventOnChange.getTimeStamp()).getTime() - new Timestamp(eventMouse.getTimeStamp()).getTime();
			}
			else if(eventMouse == null && eventKeyPress != null) {
				return new Timestamp(eventOnChange.getTimeStamp()).getTime() - new Timestamp(eventKeyPress.getTimeStamp()).getTime();
			}
		}
		return 0L;
	}
	
	/**
	 * Realiza el cálculo del tiempo de reacción para aquellos elementos de selección que no son combos
	 * @param sceneID identificador de la escena
	 * @param sessionID identificador de la sesión
	 * @param component identificador del componente
	 * @param initial evento inicial de la escena
	 * @return el tiempo de reacción
	 */
	private Long calculateOtherSelectionObject(String sceneID, String sessionID, ComponentData component,
			Event initial) {
		//Si es un combo, el primer evento es del click sobre el combo para cargar las opciones
		//Puede que el click sea haga con el ratón y con el teclado
		Event eventMouse = ExperimentDataFactory.getEventDAO().getInitialEventOfSelectionObject(sceneID, sessionID, 
				component.getComponentId(), Constantes.EVENT_ON_CLICK_SELECTION_OBJECT, null);
		
		Event eventKeyPress = ExperimentDataFactory.getEventDAO().getInitialEventOfSelectionObject(sceneID, sessionID, 
				component.getComponentId(), Constantes.EVENT_KEY_PRESS, Constantes.EVENT_ENTER);
		
		logger.info("\t \t Eventos obtenidos: EVENTMOUSE: " + eventMouse + " - EVENTKEYPRESS: " + eventKeyPress);
		
		if(eventKeyPress != null && eventMouse != null) {
			//Se coge el de menor tiempo
			if(eventKeyPress.getTimeStamp() > eventMouse.getTimeStamp()) {
				return new Timestamp(eventMouse.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
			}
			else {
				return new Timestamp(eventKeyPress.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
			}
		}
		else if(eventMouse != null && eventKeyPress == null) {
			return new Timestamp(eventMouse.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		}
		else if(eventMouse == null && eventKeyPress != null) {
			return new Timestamp(eventKeyPress.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		}
		
		return 0L;
	}

}

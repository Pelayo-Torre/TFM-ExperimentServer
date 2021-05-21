package com.uniovi.es.business.experimentData.strategy.strategys.textFields;

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
 * Estrategia que se encarga de calcular el número de caracteres eliminados en un componente de tipo texto.
 * Para ellos construye la cadena de texto generada a partir de los eventos y se queda con los eventos
 * de teclado de tipo SUPRIMIR o ELIMINAR. 
 * @author pelayo
 *
 */
public class NumberCharactersDeleted extends DataStrategyAbstract{

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_NUMBER_CHARACTERS_DELETED;
	}

	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_characters_deleted");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_characters_deleted");
	}
	
	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - NumberCharactersDeleted - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		//Obtener todos los componentes para la escena y la sesión dadas
		List<ComponentData> components = new ArrayList<ComponentData>();
		
		components.addAll(
			ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null, Constantes.COMPONENT_TEXT_FIELD)
		);
			
		logger.info("\t \t Número de componentes: " + components.size());
		
		for(ComponentData component : components) {
			logger.info("\t \t Obtenemos eventos keydown sobre el componente: " + component.getComponentId());
			
			List<Event> events = ExperimentDataFactory.getEventDAO().getEvents(
					sceneID, sessionID, component.getComponentId(), null, null, null, Constantes.EVENT_KEY_DOWN);
			
			logger.info("\t \t Número de eventos: " + events.size());
			
			String cadena = "";
			Integer puntero = 0;
			Integer charactersRemoved = 0;
			
			//Se reconstruye caracter a caracter, 
			for(Event event : events) {
				if(event != null && event.getKeyValueEvent() != null && event.getKeyCodeEvent() != null) {
					//si el key value contiene más de un caracter quiere decir que no ha pulsado una tecla de escritura
					if(event.getKeyValueEvent().length() == 1) {
						cadena += event.getKeyValueEvent();
						puntero++;
					}
					else {
						//Si no es de un caracter, puede ser que haya pulsado tecla eliminar
						if(event.getKeyCodeEvent() == Constantes.EVENT_BACKSPACE && puntero != 0) {
							charactersRemoved++;
							cadena = charRemoveAt(cadena, puntero);
							puntero--;
						}
						//Si no es de un caracter, puede ser que haya pulsado tecla suprimir
						else if(event.getKeyCodeEvent() == Constantes.EVENT_DELETE && puntero != cadena.length()) {
							charactersRemoved++;
							cadena = charRemoveAt(cadena, puntero + 1);
						}
						//Si pulsa tecla de dirección derecha, se mueve el puntero
						else if(event.getKeyCodeEvent() == Constantes.EVENT_ARROW_RIGHT) {
							if(puntero != cadena.length()) {
								puntero++;
							}
						}
						//Si pulsa tecla de dirección izquierda, se mueve el puntero
						else if(event.getKeyCodeEvent() == Constantes.EVENT_ARROW_LEFT) {
							if(puntero != 0) {
								puntero--;
							}
						}
					}
				}
				logger.info("\t \t Datos - CADENA: " + cadena + " PUNTERO: " + puntero + " ELIMINADOS: " + charactersRemoved);
			}
			result.put(component.getComponentId(), charactersRemoved);
			events = null;
		}
		
		components = null;
		
		logger.info("[FINAL] - NumberCharactersDeleted - calculate");
		return result;
	}

}

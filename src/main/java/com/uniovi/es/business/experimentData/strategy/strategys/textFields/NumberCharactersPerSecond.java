package com.uniovi.es.business.experimentData.strategy.strategys.textFields;

import java.sql.Timestamp;
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
 * Se calcula el nº de caracteres por segundo tecleados en componentes de escritura.
 * Se tienen en cuenta todos los caracteres, aunque se hayan borrado
 * Se cogen los eventos de teclado entre el último evento de captura de foco y el último evento de pérdida de foco.
 * @author pelay
 *
 */
public class NumberCharactersPerSecond extends DataStrategyAbstract{

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_NUMBER_CHARACTERS_PER_SECOND;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_characters_per_second");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_characters_per_second");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - NumberCharactersDeleted - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Double> result = new HashMap<String, Double>();
		
		//Obtener todos los componentes para la escena y la sesión dadas
		List<ComponentData> components = new ArrayList<ComponentData>();
		
		components.addAll(
			ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null, Constantes.COMPONENT_TEXT_FIELD)
		);
				
		logger.info("\t \t Número de componentes: " + components.size());
		
		for(ComponentData component : components) {
			logger.info("\t \t Obtenemos los últimos eventos de captura de foco y pérdida de foco sobre el componente: " + component.getComponentId());
			
			Event initial = ExperimentDataFactory.getEventDAO().getFinalEvent(sceneID, sessionID, component.getComponentId(), Constantes.EVENT_FOCUS, null);
			Event last = ExperimentDataFactory.getEventDAO().getFinalEvent(sceneID, sessionID, component.getComponentId(), Constantes.EVENT_BLUR, null);
			
			if(initial != null && last != null) {
				logger.info("\t \t Eventos de captura y pérdida de foco encontrados. Captura: " + initial + " Pérdida: " + last);
				
				//Se obtiene la lista de eventos entre ambos eventos inicial y final
				List<Event> events = ExperimentDataFactory.getEventDAO().getEvents(
						sceneID, sessionID, component.getComponentId(), initial.getTimeStamp(), last.getTimeStamp(), null, 
							Constantes.EVENT_KEY_DOWN);
				
				//Si no hay eventos, tiempo 0
				if(events != null && events.size() > 0) {
					Double characters = 0.0;
					for(Event event : events) {
						if(event != null && event.getKeyValueEvent() != null && event.getKeyCodeEvent() != null) {
							//si el key value contiene más de un caracter quiere decir que no ha pulsado una tecla de escritura
							if(event.getKeyValueEvent().length() == 1) {
								characters++;
							}
						}
					}
					logger.info("\t \t Caracteres encontrados para el componente " + component.getComponentId() + ": " + characters);
					//El número de carcateres por segundo es total de caracteres escritos / tiempo entre evento inicial y final
					Long time = (new Timestamp(last.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime());
					Double charactersPerSecond = (characters / ( time.doubleValue() / 1000 ));
					
					result.put(component.getComponentId(), (double)Math.round(charactersPerSecond * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
					
					logger.info("\t \t Resultado componente " + component.getComponentId() + ": " + charactersPerSecond);
				}
				else {
					logger.info("\t \t No se han encontrado eventos entre el inicial y final");
					result.put(component.getComponentId(), 0.0);
				}
				
				initial = null;
				last = null;
				events = null;
			}
			else {
				logger.info("\t \t Eventos de captura y pérdida de foco no encontrados. Captura: " + initial + " Pérdida: " + last);
				result.put(component.getComponentId(), 0.0);
			}
		}
		components = null;
		logger.info("[FINAL] - NumberCharactersDeleted - calculate");
		return result;
	}

}

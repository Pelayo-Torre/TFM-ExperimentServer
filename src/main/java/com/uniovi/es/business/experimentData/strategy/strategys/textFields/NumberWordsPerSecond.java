package com.uniovi.es.business.experimentData.strategy.strategys.textFields;

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

public class NumberWordsPerSecond extends StrategyDataAbstract{
	
	private static final String REGEX_EXPRESSION = "[^a-zA-Z0-9]+";

	public NumberWordsPerSecond(Integer key) {
		super(key);
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("number_words_per_second");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("number_words_per_second");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - NumberWordsPerSecond - calculate");
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
				
				logger.info("\t \t Número de eventos: " + events.size());
				
				if(events.size() != 0) {
					String cadena = "";
					Integer puntero = 0;
					
					//Se reconstruye caracter a caracter, 
					for(Event event : events) {
						if(event != null && event.getKeyValueEvent() != null && event.getKeyCodeEvent() != null) {
							//si el key value contiene más de un caracter quiere decir que no ha pulsado una tecla de escritura
							if(event.getKeyValueEvent().length() == 1) {
								cadena = charInsertAt(event.getKeyValueEvent(), cadena, puntero);
								puntero ++;
							}
							else {
								//Si no es de un caracter, puede ser que haya pulsado tecla eliminar
								if(event.getKeyCodeEvent() == Constantes.EVENT_BACKSPACE && puntero != 0) {
									cadena = charRemoveAt(cadena, puntero);
									puntero--;
								}
								//Si no es de un caracter, puede ser que haya pulsado tecla suprimir
								else if(event.getKeyCodeEvent() == Constantes.EVENT_DELETE && puntero != cadena.length()) {
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
						logger.info("\t \t Datos - CADENA: " + cadena + " PUNTERO: " + puntero);
					}
					
					String [] words = cadena.split(REGEX_EXPRESSION);
					
					if(words == null)
						words = new String [0];
					
					//El número de palabras por segundo es total de palabras escritas / tiempo entre evento inicial y final
					Long time = (new Timestamp(last.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime());
					Double wordsPerSecond = (words.length / ( time.doubleValue() / 1000 ));
					
					result.put(component.getComponentId(), (double)Math.round(wordsPerSecond * 100d) / 100d);
					logger.info("\t \t Resultado componente " + component.getComponentId() + ": " + words);
				}
				else {
					logger.info("\t \t No se han encontrado eventos entre el inicial y final");
					result.put(component.getComponentId(), 0.00);
				}				
			}
			else {
				logger.info("\t \t Eventos de captura y pérdida de foco no encontrados. Captura: " + initial + " Pérdida: " + last);
				result.put(component.getComponentId(), 0.00);
			}
		}
		
		logger.info("[FINAL] - NumberWordsPerSecond - calculate");
		return result;
	}

}

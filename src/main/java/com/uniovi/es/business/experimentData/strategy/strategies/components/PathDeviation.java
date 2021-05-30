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

public class PathDeviation  extends DataStrategyAbstract{
	
	private final static Integer DESVIACION_POR_ENCIMA = 1;
	private final static Integer DESVIACION_POR_DEBAJO = -1;
	private final static Integer SIN_DESVIACION = 0;

	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_PATH_DEVIATION;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("path_deviation");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("path_deviation");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - PathDeviation - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		
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
			
			//Se obtienen los eventos que marcan la trayectoria real del usuario hasta clickar en el componente
			
			Event initialClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
					component.getComponentId(), Constantes.EVENT_ON_CLICK, null);
			Event initialDoubleClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
					component.getComponentId(), Constantes.EVENT_ON_DOUBLE_CLICK, null);
			
			logger.info("\t \t Eventos obtenidos: INITIAL: " + initialMouse 
					+ "  INITIAL-CLICK-COMPONENT: " + initialClickComponent + "  INITIAL-DOUBLE-CLICK-COMPONENT: " + initialDoubleClickComponent);
			
			double desviation = 0.0;
			int x1 = 0;
			int x2 = 0;
			int y1 = 0;
			int y2 = 0;
			if(initialMouse != null) {
				x1 = initialMouse.getX();
				y1 = initialMouse.getY();
				List<Event> events = new ArrayList<Event>();
				if(initialClickComponent != null && initialDoubleClickComponent != null) {
					if(initialClickComponent.getTimeStamp() < initialDoubleClickComponent.getTimeStamp()) {
						logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de un click");
						events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
								null, initialMouse.getTimeStamp(), initialClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						x2 = initialClickComponent.getX();
						y2 = initialClickComponent.getY();
					}
					else {
						logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de doble click");
						events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
								null, initialMouse.getTimeStamp(), initialDoubleClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						x2 = initialDoubleClickComponent.getX();
						y2 = initialDoubleClickComponent.getY();
					}
				}
				else {
					if(initialClickComponent != null) {
						logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de un click");
						events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
								null, initialMouse.getTimeStamp(), initialClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						x2 = initialClickComponent.getX();
						y2 = initialClickComponent.getY();
					}
					else if(initialDoubleClickComponent != null) {
						logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de doble click");
						events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
								null, initialMouse.getTimeStamp(), initialDoubleClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						x2 = initialDoubleClickComponent.getX();
						y2 = initialDoubleClickComponent.getY();
					}
				}
				desviation = calculateDesviation(events, x1, y1, x2, y2);
				events = null;
			}
			
			if(desviation < 0) {
				result.put(component.getComponentId(), DESVIACION_POR_DEBAJO);
			}
			else if(desviation > 0) {
				result.put(component.getComponentId(), DESVIACION_POR_ENCIMA);
			}
			else {
				result.put(component.getComponentId(), SIN_DESVIACION);
			}
			
			logger.info("\t \t Resultado desviación " + component.getComponentId() + ": " + desviation);	
			initialMouse = null;
			initialClickComponent = null;
			initialDoubleClickComponent = null;
		}
		components = null;
		logger.info("[FINAL] - PathDeviation - calculate");
		return result;
	}
	
	/**
	 * Calcula la desviación sobre la trayectoria ideal
	 * @param events lista de eventos de movimiento de ratón
	 * @param x1 coordenada X del evento inicial
	 * @param y1 coordenada Y del evento inicial
	 * @param x2 coordenada X del evento final
	 * @param y2 coordenada Y del evento final
	 * @return la desviación
	 */
	private double calculateDesviation(List<Event> events, int x1, int y1, int x2, int y2) {
		double desviation = 0.0;
		for(Event event : events) {
			if(event != null && event.getX() != null && event.getY() != null) {
				//Para cada coordenada (X,Y) de evento
				//Se calcula la ecuación de la recta entre el punto inicial y el final (y = mx + n)
				//Se sustituye la 'n' por el punto del evento
				//Despejando y sustituyendo variables, el cálculo queda de la siguiente manera
				double yIdeal = ( calculateSlope(x1, y1, x2, y2) * event.getX() ) + ( y2 - calculateSlope(x1, y1, x2, y2) * x2 );
				desviation += (event.getY() - yIdeal);
			}
		}
		return desviation;
	} 
	
}

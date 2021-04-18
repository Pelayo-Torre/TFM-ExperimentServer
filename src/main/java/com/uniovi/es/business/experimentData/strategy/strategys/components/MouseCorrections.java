package com.uniovi.es.business.experimentData.strategy.strategys.components;

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

public class MouseCorrections extends StrategyDataAbstract{

	public MouseCorrections(Integer key) {
		super(key);
	}

	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_corrections");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_corrections");
	}
	
	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - MouseCorrections - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		List<ComponentData> components = ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null);
		
		if(components.size() > 0) {
			logger.info("\t \t Número de componentes obtenidos: " + components.size());
			for(ComponentData component : components) {
				logger.info("\t \t Se obtiene la lista de eventos entre el 1º evento de movimiento y el de click sobre el Componente: " + component.getComponentId());
				
				Event initialMouse = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						null, Constantes.EVENT_ON_MOUSE_MOVE, null);
				Event initialClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						component.getComponentId(), Constantes.EVENT_ON_CLICK, null);
				Event initialDoubleClickComponent = ExperimentDataFactory.getEventDAO().getInitialEvent(sceneID, sessionID, 
						component.getComponentId(), Constantes.EVENT_ON_DOUBLE_CLICK, null);
				
				logger.info("\t \t Eventos obtenidos: INITIAL: " + initialMouse 
						+ "  INITIAL-CLICK-COMPONENT: " + initialClickComponent + "  INITIAL-DOUBLE-CLICK-COMPONENT: " + initialDoubleClickComponent);
				
				Integer corrections = 0;
				if(initialMouse != null) {
					List<Event> events = new ArrayList<Event>();
					if(initialClickComponent != null && initialDoubleClickComponent != null) {
						if(initialClickComponent.getTimeStamp() < initialDoubleClickComponent.getTimeStamp()) {
							logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de un click");
							events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
									null, initialMouse.getTimeStamp(), initialClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						
						}
						else {
							logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de doble click");
							events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
									null, initialMouse.getTimeStamp(), initialDoubleClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						}
					}
					else {
						if(initialClickComponent != null) {
							logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de un click");
							events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
									null, initialMouse.getTimeStamp(), initialClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						
						}
						else if(initialDoubleClickComponent != null){
							logger.info("\t \t Se obtiene la lista de eventos desde el inicial hasta el evento de doble click");
							events = ExperimentDataFactory.getEventDAO().getEvents(sceneID, sessionID, 
									null, initialMouse.getTimeStamp(), initialDoubleClickComponent.getTimeStamp(), null, Constantes.EVENT_ON_MOUSE_MOVE);
						}
					}
					logger.info("\t \t Número de eventos obtenidos: " + events.size());
					corrections = calculate(events);
					logger.info("\t \t Correcciones para el componente " + component.getComponentId() + ": " + corrections);
				}
				
				result.put(component.getComponentId(), corrections);
			}
		}		
		
		logger.info("[FINAL] - MouseCorrections - calculate");
		return result;
	}
	
	private Double calculateSlope(Event a, Event b) {
		return (double) ( (b.getY() - a.getY()) / (b.getX() - a.getX()) );
	}
	
	/**
	 * Calcula el número de correcciones a partir de una lista de eventos recibida
	 * @param events lsta de eventos
	 * @return el número de correcciones producidas
	 */
	private Integer calculate(List<Event> events) {
		Integer result = 0;
		for(int i=0; i<events.size(); i++) {
			if( i+1 < events.size() && i+2 < events.size() ) {
				Event a = events.get(i);
				Event b = events.get(i+1);
				Event c = events.get(i+2);
				
				Double slopeAB = 0.0;
				Double slopeBC = 0.0;
				
				//Se comrprueba si las X de los puntos son iguales.
				//Si son iguales es dividir entre 0, por lo que lo tomaremos como 0 de pendiente.
				//Si no son iguales, se calcula la pendiente
				if(a.getX() != b.getX()) {
					slopeAB = calculateSlope(a, b);
				}
				if(b.getX() != c.getX()) {
					slopeBC = calculateSlope(b, c);
				}
				
				//Una vez calculadas las pendientes, se comprueba si son distintas (en caso de serlo, hay corrección)
				if(slopeAB.doubleValue() != slopeBC.doubleValue()) {
					result ++;
				}
				else {
					if(hayCambio(c.getY(), b.getY(), a.getY(), c.getX(), b.getX(), a.getX())) {
						result++;
					}						
				}
			}
		}
		return result;
	}
	
	/**
	 * Si las pendientes no son distintas, 
	 * si el signo de los dividendos de m1 y m2 son iguales Y el signo de los divisores de m1 y m2 son iguales, no hay corrección
	 * En caso contrario sí
	 * @param y3
	 * @param y2
	 * @param y1
	 * @param x3
	 * @param x2
	 * @param x1
	 * @return
	 */
	private boolean hayCambio(Integer y3, Integer y2, Integer y1, Integer x3, Integer x2, Integer x1) {
		Integer dividendo1 = y2-y1;
		Integer dividendo2 = y3-y2;
		Integer divisor1 = x2-x1;
		Integer divisor2 = x3-x2;
		
		if(dividendo1 <= 0 && dividendo2 <= 0 && divisor1 < 0 && divisor2 < 0)
			return false;
		else if(dividendo1 >= 0 && dividendo2 >= 0 && divisor1 > 0 && divisor2 > 0)
			return false;
		else if(dividendo1 <= 0 && dividendo2 <= 0 && divisor1 > 0 && divisor2 > 0)
			return false;
		else if(dividendo1 >= 0 && dividendo2 >= 0 && divisor1 < 0 && divisor2 < 0)
			return false;
		
		return true;
	}
}

package com.uniovi.es.business.experimentData.strategy.strategys.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyDataAbstract;
import com.uniovi.es.model.ComponentData;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;
import com.uniovi.es.utils.ConstantesStrategys;

public class MouseAccuraccyPercentage extends StrategyDataAbstract{
	
	private MouseAccuraccy ma;
	
	public MouseAccuraccyPercentage(MouseAccuraccy ma){
		this.ma = ma;
	}
	
	@Override
	public Integer getKey() {
		return ConstantesStrategys.STRATEGY_MOUSE_ACCURACY_PERCENTAGE;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("mouse_accuracy_percentage");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("mouse_accuracy_percentage");
	}

	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - MouseAccuraccyPercentage - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Double> result = new HashMap<String, Double>();
		
		//Se realiza el cálculo de la precisión de cada componente
		@SuppressWarnings("unchecked")
		Map<String, Double> accuracy = (Map<String, Double>) ma.calculate(sceneID, sessionID);
		
		//Obtener todos los componentes para la escena y la sesión dadas
		List<ComponentData> components = new ArrayList<ComponentData>();
		
		components.addAll(
			ExperimentDataFactory.getSceneComponentDAO().getComponents(sceneID, sessionID, null)
		);
			
		logger.info("\t \t Número de componentes: " + components.size());
		
		for(ComponentData component : components) {
			logger.info("\t \t Componente: " + component.getComponentId());
			
			//Se calcula la distancia entre la esquina y el centro del componente
			double distance1 = (double)Math.round((distance(component.getX(), component.getY(), component.getxF(), component.getyF())) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
			//Se calcula la distancia entre el punto de pulsación y el centro del componente
			Double a = accuracy.get(component.getComponentId());
			double distance2 = 0.0;
			if(a != null) {
				distance2 = accuracy.get(component.getComponentId());
			}
			
			logger.info("\t \t Distancia mayor: " + distance1 + ", Distancia menor: " + distance2);
			double percentage = distance2 * 100 / distance1;
			logger.info("\t \t Porcentaje de precisión: " + percentage);
			
			result.put(component.getComponentId(), (double)Math.round(percentage * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
		}
		
		logger.info("[FINAL] - MouseAccuraccyPercentage - calculate");
		return result;
	}

}

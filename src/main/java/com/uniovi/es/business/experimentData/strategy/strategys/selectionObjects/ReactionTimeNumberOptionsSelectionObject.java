package com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uniovi.es.business.experimentData.strategy.PropetiesStrategyManager;
import com.uniovi.es.business.experimentData.strategy.StrategyData;

/**
 * Calcula el tiempo de reacción sobre el número de opciones ofertadas 
 * @author pelayo
 *
 */
public class ReactionTimeNumberOptionsSelectionObject implements StrategyData {
	
	public static final Logger logger = LoggerFactory.getLogger(ReactionTimeNumberOptionsSelectionObject.class);
	
	private StrategyData strategyReactionTime;
	private StrategyData strategyOfferedOptions;
	private Integer key;

	public ReactionTimeNumberOptionsSelectionObject(Integer key, StrategyData strategyReactionTime, StrategyData strategyOfferedOptions) {
		this.strategyReactionTime = strategyReactionTime;
		this.strategyOfferedOptions = strategyOfferedOptions;
		this.key = key;
	}
	
	@Override
	public String getPropertyName() {
		return PropetiesStrategyManager.getInstance().getNameStrategysProperties().getProperty("reaction_time_number_options_combo");
	}
	
	@Override
	public String getPropertyAbbreviation() {
		return PropetiesStrategyManager.getInstance().getAbbreviationStrategysProperties().getProperty("reaction_time_number_options_combo");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object calculate(String sceneID, String sessionID) {
		logger.info("[INICIO] - ReactionTimeNumberOptionsSelectionObject - calculate");
		logger.info("\t \t Parámetros de entrada: SceneID - " + sceneID + " SessionID - " + sessionID);
		
		Map<String, Long> times = (Map<String, Long>) this.strategyReactionTime.calculate(sceneID, sessionID);
		Map<String, Integer> options = (Map<String, Integer>) this.strategyOfferedOptions.calculate(sceneID, sessionID);
		
		Map<String, Double> result = new HashMap<String, Double>();
		
		if(times != null && options != null) {
			for (Map.Entry<String, Long> entry : times.entrySet()) {
				Integer numberOptions = options.get(entry.getKey());
				if(numberOptions == null)
					numberOptions = 0;
				logger.info("\t \t Opciones: " + numberOptions);
				double time = entry.getValue().doubleValue();
				logger.info("\t \t Tiempo: " + time);
				//Tiempo en milisegundos
				Double timeOptions = 0.0;
				if(numberOptions != 0.0)
					timeOptions = time / numberOptions.doubleValue();
				logger.info("\t \t Tiempo/Opciones: " + timeOptions);
				result.put(entry.getKey(), (double)Math.round(timeOptions * 100d) / 100d);
			}
		}		
		logger.info("[FINAL] - ReactionTimeNumberOptionsSelectionObject - calculate");
		return result;
	}

	@Override
	public Integer getKey() {
		return key;
	}

}

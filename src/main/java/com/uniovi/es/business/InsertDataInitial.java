package com.uniovi.es.business;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;
import com.uniovi.es.business.experimentData.strategy.strategys.ReactionTime;
import com.uniovi.es.business.experimentData.strategy.strategys.TotalSceneTime;
import com.uniovi.es.utils.Constantes;


@Service
/**
 * Esta clase ejecutará el método init() cada vez que se lance la aplicación
 * @author pelay
 *
 */
public class InsertDataInitial {
	
	@PostConstruct
	public void init() {	
		
		//Se cargan las diferentes strategias definidas. 
		//Si se desea añadir una nueva estrategia, basta con añadirla aquí y crear la clase java extendiendo de StrategyDataAbstract
		
		StrategyDataManager.getInstance().addStrategyData(new TotalSceneTime(Constantes.STRATEGY_TOTAL_SCENE_TIME));
		StrategyDataManager.getInstance().addStrategyData(new ReactionTime(Constantes.STRATEGY_REACTION_TIME));

		
	}
	
}

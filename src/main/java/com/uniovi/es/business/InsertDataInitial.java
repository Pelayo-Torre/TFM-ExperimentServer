package com.uniovi.es.business;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;
import com.uniovi.es.business.experimentData.strategy.strategys.ReactionTime;
import com.uniovi.es.business.experimentData.strategy.strategys.TotalSceneTime;
import com.uniovi.es.business.experimentData.strategy.strategys.components.DifferenceIdealAndRealDistance;
import com.uniovi.es.business.experimentData.strategy.strategys.components.IdealDistanceBetweenMouseAndComponent;
import com.uniovi.es.business.experimentData.strategy.strategys.components.MouseAccuraccy;
import com.uniovi.es.business.experimentData.strategy.strategys.components.MouseAccuraccyPercentage;
import com.uniovi.es.business.experimentData.strategy.strategys.components.MouseCorrections;
import com.uniovi.es.business.experimentData.strategy.strategys.components.MouseMovementTime;
import com.uniovi.es.business.experimentData.strategy.strategys.components.MouseSpeedIdealDistance;
import com.uniovi.es.business.experimentData.strategy.strategys.components.MouseSpeedRealDistance;
import com.uniovi.es.business.experimentData.strategy.strategys.components.NumberErrorClicks;
import com.uniovi.es.business.experimentData.strategy.strategys.components.RealDistanceBetweenMouseAndComponent;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.NumberTimesChangedCombo;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.OfferedOptionsCombo;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.ReactionTimeCombo;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.ReactionTimeNumberOptionsCombo;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberCharactersDeleted;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberCharactersPerSecond;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberTimesArrowsLeftRight;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberTimesChangeCursorWithMouse;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberWordsDeleted;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberWordsPerSecond;
import com.uniovi.es.utils.Constantes;


@Service
/**
 * Esta clase ejecutará el método init() cada vez que se lance la aplicación
 * @author pelayo
 *
 */
public class InsertDataInitial {
	
	@PostConstruct
	public void init() {	
		
		//Se cargan las diferentes strategias definidas. 
		//Si se desea añadir una nueva estrategia, basta con crearla y añadirla aquí y crear la clase java extendiendo de StrategyDataAbstract
		
		StrategyDataManager.getInstance().addStrategyData(new TotalSceneTime(Constantes.STRATEGY_TOTAL_SCENE_TIME));
		StrategyDataManager.getInstance().addStrategyData(new ReactionTime(Constantes.STRATEGY_REACTION_TIME));

		//SELECTION OBJECTS
		ReactionTimeCombo rtc = new ReactionTimeCombo(Constantes.STRATEGY_REACTION_TIME_COMBO);
		OfferedOptionsCombo ooc = new OfferedOptionsCombo(Constantes.STRATEGY_OFFERED_OPTIONS_COMBO);
		
		StrategyDataManager.getInstance().addStrategyData(rtc);
		StrategyDataManager.getInstance().addStrategyData(ooc);
		StrategyDataManager.getInstance().addStrategyData(new ReactionTimeNumberOptionsCombo(Constantes.STRATEGY_REACTION_TIME_NUMBER_OPTIONS_COMBO, rtc, ooc));
		StrategyDataManager.getInstance().addStrategyData(new NumberTimesChangedCombo(Constantes.STRATEGY_NUMBER_TIMES_CHANGED_COMBO));
		
		//TEXT FIELDS
		StrategyDataManager.getInstance().addStrategyData(new NumberCharactersPerSecond(Constantes.STRATEGY_NUMBER_CHARACTERS_PER_SECOND));
		StrategyDataManager.getInstance().addStrategyData(new NumberWordsPerSecond(Constantes.STRATEGY_NUMBER_WORDS_PER_SECOND));
		StrategyDataManager.getInstance().addStrategyData(new NumberCharactersDeleted(Constantes.STRATEGY_NUMBER_CHARACTERS_DELETED));
		StrategyDataManager.getInstance().addStrategyData(new NumberWordsDeleted(Constantes.STRATEGY_NUMBER_WORDS_DELETED));
		StrategyDataManager.getInstance().addStrategyData(new NumberTimesArrowsLeftRight(Constantes.STRATEGY_NUMBER_TIMES_ARROWS_LEFT_RIGHT));
		StrategyDataManager.getInstance().addStrategyData(new NumberTimesChangeCursorWithMouse(Constantes.STRATEGY_NUMBER_TIMES_CHANGE_CURSOR_WITH_MOUSE));

		//COMPONENTES
		IdealDistanceBetweenMouseAndComponent idbmc = new IdealDistanceBetweenMouseAndComponent(Constantes.STRATEGY_IDEAL_DISTANCE_BETWEEN_MOUSE_AND_COMPONENT);
		RealDistanceBetweenMouseAndComponent rdbmc = new RealDistanceBetweenMouseAndComponent(Constantes.STRATEGY_REAL_DISTANCE_BETWEEN_MOUSE_AND_COMPONENT);
		
		StrategyDataManager.getInstance().addStrategyData(idbmc);
		StrategyDataManager.getInstance().addStrategyData(rdbmc);
		StrategyDataManager.getInstance().addStrategyData(new DifferenceIdealAndRealDistance(Constantes.STRATEGY_DIFFERENCE_IDEAL_AND_REAL_DISTANCE, idbmc, rdbmc));
		StrategyDataManager.getInstance().addStrategyData(new MouseMovementTime(Constantes.STRATEGY_MOUSE_MOVEMENT_TIME));
		StrategyDataManager.getInstance().addStrategyData(new MouseSpeedIdealDistance(Constantes.STRATEGY_MOUSE_SPEED_IDEAL_DISTANCE, idbmc));
		StrategyDataManager.getInstance().addStrategyData(new MouseSpeedRealDistance(Constantes.STRATEGY_MOUSE_SPEED_REAL_DISTANCE, rdbmc));
		StrategyDataManager.getInstance().addStrategyData(new MouseCorrections(Constantes.STRATEGY_MOUSE_CORRECTIONS));
		StrategyDataManager.getInstance().addStrategyData(new MouseAccuraccy(Constantes.STRATEGY_MOUSE_ACCURACY));
		StrategyDataManager.getInstance().addStrategyData(new MouseAccuraccyPercentage(Constantes.STRATEGY_MOUSE_ACCURACY_PERCENTAGE));
		StrategyDataManager.getInstance().addStrategyData(new NumberErrorClicks(Constantes.STRATEGY_NUMBER_ERROR_CLICKS));
	
		
	}
	
}

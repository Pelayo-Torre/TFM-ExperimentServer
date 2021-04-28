package com.uniovi.es.business;
import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.uniovi.es.business.experimentData.filter.FilterDataManager;
import com.uniovi.es.business.experimentData.filter.filters.FilterFullScene;
import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;
import com.uniovi.es.business.experimentData.strategy.strategys.NumberErrorClicks;
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
import com.uniovi.es.business.experimentData.strategy.strategys.components.RealDistanceBetweenMouseAndComponent;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.NumberTimesChangedSelectionObject;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.OfferedOptionsSelectionObject;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.ReactionTimeSelectionObject;
import com.uniovi.es.business.experimentData.strategy.strategys.selectionObjects.ReactionTimeNumberOptionsSelectionObject;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberCharactersDeleted;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberCharactersPerSecond;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberTimesArrowsLeftRight;
import com.uniovi.es.business.experimentData.strategy.strategys.textFields.NumberWordsPerSecond;
import com.uniovi.es.utils.ConstantesFilters;
import com.uniovi.es.utils.ConstantesStrategys;


@Service
/**
 * Esta clase ejecutará el método init() cada vez que se lance la aplicación
 * @author pelayo
 *
 */
public class InsertDataInitial {
	
	@PostConstruct
	public void init() {	
		
		loadStrategys();
		loadFilters();
	}
	
	private void loadStrategys() {
		//Se cargan las diferentes strategias definidas. 
		//Si se desea añadir una nueva estrategia, basta con crearla y añadirla aquí y crear la clase java extendiendo de StrategyDataAbstract
		
		StrategyDataManager.getInstance().addStrategyData(new TotalSceneTime(ConstantesStrategys.STRATEGY_TOTAL_SCENE_TIME));
		StrategyDataManager.getInstance().addStrategyData(new ReactionTime(ConstantesStrategys.STRATEGY_REACTION_TIME));

		//SELECTION OBJECTS
		ReactionTimeSelectionObject rtc = new ReactionTimeSelectionObject(ConstantesStrategys.STRATEGY_REACTION_TIME_SELECTION_OBJECT);
		OfferedOptionsSelectionObject ooc = new OfferedOptionsSelectionObject(ConstantesStrategys.STRATEGY_OFFERED_OPTIONS_SELECTION_OBJECT);
		
		StrategyDataManager.getInstance().addStrategyData(rtc);
		StrategyDataManager.getInstance().addStrategyData(ooc);
		StrategyDataManager.getInstance().addStrategyData(new ReactionTimeNumberOptionsSelectionObject(ConstantesStrategys.STRATEGY_REACTION_TIME_NUMBER_OPTIONS_SELECTION_OBJECT, rtc, ooc));
		StrategyDataManager.getInstance().addStrategyData(new NumberTimesChangedSelectionObject(ConstantesStrategys.STRATEGY_NUMBER_TIMES_CHANGED_SELECTION_OBJECT));
		
		//TEXT FIELDS
		StrategyDataManager.getInstance().addStrategyData(new NumberCharactersPerSecond(ConstantesStrategys.STRATEGY_NUMBER_CHARACTERS_PER_SECOND));
		StrategyDataManager.getInstance().addStrategyData(new NumberWordsPerSecond(ConstantesStrategys.STRATEGY_NUMBER_WORDS_PER_SECOND));
		StrategyDataManager.getInstance().addStrategyData(new NumberCharactersDeleted(ConstantesStrategys.STRATEGY_NUMBER_CHARACTERS_DELETED));
		StrategyDataManager.getInstance().addStrategyData(new NumberTimesArrowsLeftRight(ConstantesStrategys.STRATEGY_NUMBER_TIMES_ARROWS_LEFT_RIGHT));

		//COMPONENTES
		IdealDistanceBetweenMouseAndComponent idbmc = new IdealDistanceBetweenMouseAndComponent(ConstantesStrategys.STRATEGY_IDEAL_DISTANCE_BETWEEN_MOUSE_AND_COMPONENT);
		RealDistanceBetweenMouseAndComponent rdbmc = new RealDistanceBetweenMouseAndComponent(ConstantesStrategys.STRATEGY_REAL_DISTANCE_BETWEEN_MOUSE_AND_COMPONENT);
		
		StrategyDataManager.getInstance().addStrategyData(idbmc);
		StrategyDataManager.getInstance().addStrategyData(rdbmc);
		StrategyDataManager.getInstance().addStrategyData(new DifferenceIdealAndRealDistance(ConstantesStrategys.STRATEGY_DIFFERENCE_IDEAL_AND_REAL_DISTANCE, rdbmc, idbmc));
		
		MouseMovementTime mmt = new MouseMovementTime(ConstantesStrategys.STRATEGY_MOUSE_MOVEMENT_TIME);
		StrategyDataManager.getInstance().addStrategyData(mmt);
		StrategyDataManager.getInstance().addStrategyData(new MouseSpeedIdealDistance(ConstantesStrategys.STRATEGY_MOUSE_SPEED_IDEAL_DISTANCE, mmt, idbmc));
		StrategyDataManager.getInstance().addStrategyData(new MouseSpeedRealDistance(ConstantesStrategys.STRATEGY_MOUSE_SPEED_REAL_DISTANCE, mmt, rdbmc));
		StrategyDataManager.getInstance().addStrategyData(new MouseCorrections(ConstantesStrategys.STRATEGY_MOUSE_CORRECTIONS));
		StrategyDataManager.getInstance().addStrategyData(new MouseAccuraccy(ConstantesStrategys.STRATEGY_MOUSE_ACCURACY));
		StrategyDataManager.getInstance().addStrategyData(new MouseAccuraccyPercentage(ConstantesStrategys.STRATEGY_MOUSE_ACCURACY_PERCENTAGE));
		StrategyDataManager.getInstance().addStrategyData(new NumberErrorClicks(ConstantesStrategys.STRATEGY_NUMBER_ERROR_CLICKS));
	}
	
	private void loadFilters() {
		
		//Se cargan los diferentes filtros definidos. 
		//Si se desea añadir un nuevo filtro, basta con crearlo y añadirlo aquí y crear la clase java extendiendo de FilterDataAbstract
		
		FilterDataManager.getInstance().addStrategyData(new FilterFullScene(ConstantesFilters.FILTER_FULL_SCENE));
		
	}
	
}

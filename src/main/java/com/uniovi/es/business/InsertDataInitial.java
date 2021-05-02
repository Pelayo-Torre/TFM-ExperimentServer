package com.uniovi.es.business;
import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.uniovi.es.business.experimentData.filter.FilterDataManager;
import com.uniovi.es.business.experimentData.filter.filters.FilterFullDemographicData;
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



/**
 * Esta clase ejecutará el método init() cada vez que se lance la aplicación
 * @author pelayo
 *
 */
@Configuration
@ComponentScan(basePackages = "com.uniovi.es.experimentData")
public class InsertDataInitial {
	
	@PostConstruct
	public void init() {	
		
		loadStrategys();
		loadFilters();
	}
	
	private void loadStrategys() {
		//Se cargan las diferentes strategias definidas. 
		//Si se desea añadir una nueva estrategia, basta con crearla y añadirla aquí y crear la clase java extendiendo de StrategyDataAbstract
		
		StrategyDataManager.getInstance().addStrategyData(new TotalSceneTime());
		StrategyDataManager.getInstance().addStrategyData(new ReactionTime());

		//SELECTION OBJECTS
		ReactionTimeSelectionObject rtc = new ReactionTimeSelectionObject();
		OfferedOptionsSelectionObject ooc = new OfferedOptionsSelectionObject();
		
		StrategyDataManager.getInstance().addStrategyData(rtc);
		StrategyDataManager.getInstance().addStrategyData(ooc);
		StrategyDataManager.getInstance().addStrategyData(new ReactionTimeNumberOptionsSelectionObject(rtc, ooc));
		StrategyDataManager.getInstance().addStrategyData(new NumberTimesChangedSelectionObject());
		
		//TEXT FIELDS
		StrategyDataManager.getInstance().addStrategyData(new NumberCharactersPerSecond());
		StrategyDataManager.getInstance().addStrategyData(new NumberWordsPerSecond());
		StrategyDataManager.getInstance().addStrategyData(new NumberCharactersDeleted());
		StrategyDataManager.getInstance().addStrategyData(new NumberTimesArrowsLeftRight());

		//COMPONENTES
		IdealDistanceBetweenMouseAndComponent idbmc = new IdealDistanceBetweenMouseAndComponent();
		RealDistanceBetweenMouseAndComponent rdbmc = new RealDistanceBetweenMouseAndComponent();
		
		StrategyDataManager.getInstance().addStrategyData(idbmc);
		StrategyDataManager.getInstance().addStrategyData(rdbmc);
		StrategyDataManager.getInstance().addStrategyData(new DifferenceIdealAndRealDistance(rdbmc, idbmc));
		
		MouseMovementTime mmt = new MouseMovementTime();
		StrategyDataManager.getInstance().addStrategyData(mmt);
		StrategyDataManager.getInstance().addStrategyData(new MouseSpeedIdealDistance(mmt, idbmc));
		StrategyDataManager.getInstance().addStrategyData(new MouseSpeedRealDistance(mmt, rdbmc));
		StrategyDataManager.getInstance().addStrategyData(new MouseCorrections());
		StrategyDataManager.getInstance().addStrategyData(new MouseAccuraccy());
		StrategyDataManager.getInstance().addStrategyData(new MouseAccuraccyPercentage());
		StrategyDataManager.getInstance().addStrategyData(new NumberErrorClicks());
	}
	
	private void loadFilters() {
		
		//Se cargan los diferentes filtros definidos. 
		//Si se desea añadir un nuevo filtro, basta con crearlo y añadirlo aquí y crear la clase java extendiendo de FilterDataAbstract
		
		FilterDataManager.getInstance().addStrategyData(new FilterFullScene());
		FilterDataManager.getInstance().addStrategyData(getFilterFullDemographicData());
		
	}
	
	@Bean
	public FilterFullDemographicData getFilterFullDemographicData() {
		return new FilterFullDemographicData();
	}
	
}

package service;

import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.uniovi.es.ExperimentServerApplication;
import com.uniovi.es.business.authentication.AuthenticationService;
import com.uniovi.es.business.dto.AuthDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.AttempsException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.ComponentData;
import com.uniovi.es.model.Event;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.User;
import com.uniovi.es.persistence.ExperimentDAO;
import com.uniovi.es.persistence.UserDAO;
import com.uniovi.es.persistence.experimentData.ConnectionProvider;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;
import com.uniovi.es.utils.Constantes;
import com.uniovi.es.utils.ConstantesStrategys;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExperimentServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestMethodOrder(Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ExperimentDataTest {
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private ExperimentDAO experimentDAO;
	
	@Autowired
	private AuthenticationService authenticateUser;
	
	@Autowired
	private ExperimentService experimentService;
	
	@Autowired
	private InvestigatorService investigatorService;
	
	private static boolean primeraVez = true;
	
	@PostConstruct
	public void init() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException, IOException {
		if(primeraVez) {
			//Establecemos el nuevo fichero de propiedades para la conexión a la base de datos de prueba
			ConnectionProvider.getInstance().changeConnection("application-test.properties");
			
			//COMENZAMOS CREANDO UN INVESTIGADOR Y UN EXPERIMENTO
			InvestigatorDTO dto = new InvestigatorDTO();
			dto.name = "Manel";
			dto.surname = "Corte";
			dto.mail = "manelcorte@gmail.com";
			dto.password = "123456789";
			
			investigatorService.registerInvestigator(dto);
			
			//INICIAMOS SESIÓN
			AuthDTO authDTO = new AuthDTO();
			authDTO.mail = "manelcorte@gmail.com";
			authDTO.password = "123456789";
			authenticateUser.authenticateUser(authDTO);
			
			//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
			ExperimentDTO experientDTO = new ExperimentDTO();
			experientDTO.title = "Experimento en Langreo";
			experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
			experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("manelcorte@gmail.com").id;
			
			experimentService.register(experientDTO);
			primeraVez = false;
		}
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de tiempo total de escena
	 */
	public void totalSceneTime() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario1", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena1";
		
		//Registramos 5 eventos
		Event event1 = new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user);
		Event event2 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618660872000L, 0, 0, "-1", -1, user);
		Event event3 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618660932000L, 0, 0, "-1", -1, user);
		Event event4 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618660992000L, 0, 0, "-1", -1, user);
		Event event5 = new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661052000L, 0, 0, "-1", -1, user);
		ExperimentDataFactory.getEventDAO().insertEvent(event1);
		ExperimentDataFactory.getEventDAO().insertEvent(event2);
		ExperimentDataFactory.getEventDAO().insertEvent(event3);
		ExperimentDataFactory.getEventDAO().insertEvent(event4);
		ExperimentDataFactory.getEventDAO().insertEvent(event5);
		
		long time = (Long) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_TOTAL_SCENE_TIME)
				.calculate(scene, user.getSessionId());
		long resta = new Timestamp(event5.getTimeStamp()).getTime() - new Timestamp(event1.getTimeStamp()).getTime();
		assertEquals(resta, time);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de tiempo de reacción de un usuario
	 */
	public void reactionTime() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario2", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena2";
		
		//Registramos eventos
		Event event1 = new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user);
		Event event2 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618660872000L, 0, 0, "-1", -1, user);
		Event event3 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618660932000L, 0, 0, "-1", -1, user);
		Event event4 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618660992000L, 0, 0, "-1", -1, user);
		Event event5 = new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661052000L, 0, 0, "-1", -1, user);
		ExperimentDataFactory.getEventDAO().insertEvent(event1);
		ExperimentDataFactory.getEventDAO().insertEvent(event2);
		ExperimentDataFactory.getEventDAO().insertEvent(event3);
		ExperimentDataFactory.getEventDAO().insertEvent(event4);
		ExperimentDataFactory.getEventDAO().insertEvent(event5);
		
		long time = (Long) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_REACTION_TIME)
				.calculate(scene, user.getSessionId());
		long resta = new Timestamp(event2.getTimeStamp()).getTime() - new Timestamp(event1.getTimeStamp()).getTime();
		assertEquals(resta, time);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de clicks erróneos fuera de componentes
	 */
	public void numberErrorClicks() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario3", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena3";
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CLICK, "-1", 1618660872000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618660932000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_DOUBLE_CLICK, "-1", 1618660992000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_DOUBLE_CLICK, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_DOUBLE_CLICK, "inputTelcado", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CLICK, "inputTeclado", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
		
		int clicks = (Integer) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_NUMBER_ERROR_CLICKS)
				.calculate(scene, user.getSessionId());
		assertEquals(clicks, 3);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de caracteres eliminados en un componente
	 */
	public void numberCharactersDeleted() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario4", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena4";
		
		//Registramos un componente de tipo texto
		ComponentData com1 = new ComponentData("inputTeclado", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null);
		ExperimentDataFactory.getSceneComponentDAO().insertComponent(com1);
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618660872000L, 0, 0, "h", 72, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618660932000L, 0, 0, "o", 79, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618660992000L, 0, 0, "l", 76, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661052000L, 0, 0, "a", 65, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661053000L, 0, 0, " ", 32, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661054000L, 0, 0, "p", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661055000L, 0, 0, "e", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661056000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_PRESS, "inputTeclado", 1618661057000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661058000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661059000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661060000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661061000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661062000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661063000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661064000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661065000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661066000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661067000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661068000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661069000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
		
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_NUMBER_CHARACTERS_DELETED)
				.calculate(scene, user.getSessionId());
		int inputTeclado = result.get("inputTeclado");
		assertEquals(inputTeclado, 4);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de caracteres por segundo en un componente
	 */
	public void numberCharactersPerSecond() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario5", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena5";
		
		//Registramos un componente de tipo texto
		ComponentData com1 = new ComponentData("inputTeclado", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null);
		ExperimentDataFactory.getSceneComponentDAO().insertComponent(com1);
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618660872000L, 0, 0, "h", 72, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618660932000L, 0, 0, "o", 79, user));
		Event focus = new Event(scene, Constantes.EVENT_FOCUS, "inputTeclado", 1618660972000L, 0, 0, "-1", -1, user);
		events.add(focus);
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618660992000L, 0, 0, "l", 76, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661052000L, 0, 0, "a", 65, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661053000L, 0, 0, " ", 32, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661054000L, 0, 0, "p", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661055000L, 0, 0, "e", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661056000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_PRESS, "inputTeclado", 1618661057000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661058000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661059000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661060000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661061000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661062000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661063000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661064000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661065000L, 0, 0, "Backspace", 8, user));
		Event blur = new Event(scene, Constantes.EVENT_BLUR, "inputTeclado", 1618661866000L, 0, 0, "-1", -1, user);
		events.add(blur);
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661867000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661868000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661869000L, 0, 0, "Delete", 46, user));
		Event focus2 = new Event(scene, Constantes.EVENT_FOCUS, "inputTeclado", 1618661871000L, 0, 0, "-1", -1, user);
		events.add(focus2);
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661871000L, 0, 0, "l", 76, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661872000L, 0, 0, "a", 65, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661873000L, 0, 0, " ", 32, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661874000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661875000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661876000L, 0, 0, "ArrowLeft", 37, user));
		Event blur2 = new Event(scene, Constantes.EVENT_BLUR, "inputTeclado", 1618661877000L, 0, 0, "-1", -1, user);
		events.add(blur2);
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661878000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
		
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_NUMBER_CHARACTERS_PER_SECOND)
				.calculate(scene, user.getSessionId());
		
		double inputTeclado = result.get("inputTeclado");
		
		Double characters = 3.0;
		Long time = new Timestamp(blur2.getTimeStamp()).getTime() - new Timestamp(focus2.getTimeStamp()).getTime();
		double charactersPerSecond = (double)Math.round( (characters / ( time / 1000 ))  * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		assertTrue(charactersPerSecond == inputTeclado);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de veces que se pulsó derecha e izquierda en un componente
	 */
	public void numberTimesArrowsLeftAndRight() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario6", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena6";
		
		//Registramos un componente de tipo texto
		ComponentData com1 = new ComponentData("inputTeclado", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null);
		ExperimentDataFactory.getSceneComponentDAO().insertComponent(com1);
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618660872000L, 0, 0, "h", 72, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618660932000L, 0, 0, "o", 79, user));
		Event focus = new Event(scene, Constantes.EVENT_FOCUS, "inputTeclado", 1618660972000L, 0, 0, "-1", -1, user);
		events.add(focus);
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618660992000L, 0, 0, "l", 76, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661052000L, 0, 0, "a", 65, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661053000L, 0, 0, " ", 32, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661054000L, 0, 0, "p", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661055000L, 0, 0, "e", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661056000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_PRESS, "inputTeclado", 1618661057000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661058000L, 0, 0, "ArrowRight", 39, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661059000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661060000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661061000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661062000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661063000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661064000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661065000L, 0, 0, "Backspace", 8, user));
		Event blur = new Event(scene, Constantes.EVENT_BLUR, "inputTeclado", 1618661866000L, 0, 0, "-1", -1, user);
		events.add(blur);
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661867000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661868000L, 0, 0, "ArrowRight", 39, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661878000L, 0, 0, "-1", -1, user));

		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
		
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_NUMBER_TIMES_ARROWS_LEFT_RIGHT)
				.calculate(scene, user.getSessionId());
		
		int inputTeclado = result.get("inputTeclado");
		
		int times = 5;
		assertTrue(times == inputTeclado);
	}

	@Test
	/**
	 * Prueba la estrategia del cálculo de número de palabras por segundo
	 */
	public void numberWordsPerSecond() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario7", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena7";
		
		//Registramos un componente de tipo texto
		ComponentData com1 = new ComponentData("inputTeclado", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null);
		ExperimentDataFactory.getSceneComponentDAO().insertComponent(com1);
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618660872000L, 0, 0, "h", 72, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618660932000L, 0, 0, "o", 79, user));
		Event focus = new Event(scene, Constantes.EVENT_FOCUS, "inputTeclado", 1618660972000L, 0, 0, "-1", -1, user);
		events.add(focus);
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618660992000L, 0, 0, "l", 76, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661052000L, 0, 0, "a", 65, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661053000L, 0, 0, " ", 32, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661054000L, 0, 0, "p", 80, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661055000L, 0, 0, "e", 69, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661056000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_PRESS, "inputTeclado", 1618661057000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661058000L, 0, 0, "*", 106, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661059000L, 0, 0, "/", 111, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661060000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661061000L, 0, 0, "p", 80, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661062000L, 0, 0, "e", 69, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661063000L, 0, 0, "&", 54, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661064000L, 0, 0, "l", 76, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661065000L, 0, 0, "a", 65, user));
		Event blur = new Event(scene, Constantes.EVENT_BLUR, "inputTeclado", 1618661866000L, 0, 0, "-1", -1, user);
		events.add(blur);
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661867000L, 0, 0, "ArrowLeft", 37, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "-1", 1618661868000L, 0, 0, "ArrowRight", 39, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661878000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
		
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_NUMBER_WORDS_PER_SECOND)
				.calculate(scene, user.getSessionId());
		
		double inputTeclado = result.get("inputTeclado");
		
		Double words = 4.0;
		Long time = new Timestamp(blur.getTimeStamp()).getTime() - new Timestamp(focus.getTimeStamp()).getTime();
		double wordsPerSecond = (double)Math.round( (words / ( time / 1000 ))  * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		assertTrue(wordsPerSecond == inputTeclado);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de cambio de opinión en un grupo de checkboxes
	 */
	public void numberTimesChangedSelectionObject_Checkbox() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario8", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena8";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_CHECK_BOX, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null));
		
		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "saab", 1618660992000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661052000L, 0, 0, "a", 65, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661053000L, 0, 0, " ", 32, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661054000L, 0, 0, "p", 80, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "volvo", 1618661055000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661056000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_PRESS, "inputTeclado", 1618661057000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661058000L, 0, 0, "*", 106, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661059000L, 0, 0, "/", 111, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661060000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "fiat", 1618661061000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661062000L, 0, 0, "e", 69, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "audi", 1618661063000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
		
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_NUMBER_TIMES_CHANGED_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
		int changes = 3;
		assertTrue(cars == changes);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de cambio de opinión en un grupo de radio botones
	 */
	public void numberTimesChangedSelectionObject_RadioButton() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario9", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena9";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null));
		
		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "saab", 1618660992000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661052000L, 0, 0, "a", 65, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661053000L, 0, 0, " ", 32, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661054000L, 0, 0, "p", 80, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "volvo", 1618661055000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661056000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_PRESS, "inputTeclado", 1618661057000L, 0, 0, "Backspace", 8, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661058000L, 0, 0, "*", 106, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661059000L, 0, 0, "/", 111, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661060000L, 0, 0, "Delete", 46, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "fiat", 1618661061000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_KEY_DOWN, "inputTeclado", 1618661062000L, 0, 0, "e", 69, user));
		events.add(new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "audi", 1618661063000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
		
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_NUMBER_TIMES_CHANGED_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
		int changes = 3;
		assertTrue(cars == changes);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de número de opciones ofrecidas en un combobox
	 */
	public void offeredOptionsSelectionObject_Combobox() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario10", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena10";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_COMBOBOX, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_COMBOBOX, null));
		
		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
	
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_OFFERED_OPTIONS_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
		int options = 4;
		assertTrue(cars == options);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de número de opciones ofrecidas en un checkbox
	 */
	public void offeredOptionsSelectionObject_Checkbox() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario11", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena11";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_CHECK_BOX, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_CHECK_BOX, null));
		
		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
	
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_OFFERED_OPTIONS_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
		int options = 4;
		assertTrue(cars == options);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de número de número de opciones ofrecidas en un radio button
	 */
	public void offeredOptionsSelectionObject_RadioButton() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario11", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena11";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		
		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
	
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_OFFERED_OPTIONS_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
		int options = 4;
		assertTrue(cars == options);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo del tiempo de reacción desde que se cargan las opciones en un combobox
	 * hasta que se pulsa en una de ellas.
	 */
	public void reactionTimeSelectionObject_Combobox() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario12", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena12";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_COMBOBOX, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_COMBOBOX, null));
		
		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 0, 0, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK_SELECTION_OBJECT, "cars", 1618661056000L, 0, 0, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 0, 0, "-1", -1, user));
		Event onChange = new Event(scene, Constantes.EVENT_ON_CHANGE_SELECTION_OBJECT, "cars", 1618661061000L, 0, 0, "-1", -1, user);
		events.add(onChange);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Long> result = (Map<String, Long>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_REACTION_TIME_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		long cars = result.get("cars");
		long time = new Timestamp(onChange.getTimeStamp()).getTime() - new Timestamp(onClick.getTimeStamp()).getTime();
		assertTrue(cars == time);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo del tiempo de reacción desde que se carga la pantalla
	 * hasta que se pulsa en una de las opciones de un grupo de checkboxes.
	 */
	public void reactionTimeSelectionObject_CheckBox() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario13", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena13";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_CHECK_BOX, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		Event initial = new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 0, 0, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK_SELECTION_OBJECT, "audi", 1618661061000L, 0, 0, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Long> result = (Map<String, Long>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_REACTION_TIME_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		long cars = result.get("cars");
		long time = new Timestamp(onClick.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		assertTrue(cars == time);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo del tiempo de reacción desde que se carga la pantalla
	 * hasta que se pulsa en una de las opciones de un grupo de radio botones.
	 */
	public void reactionTimeSelectionObject_RadioButton() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario14", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena14";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		Event initial = new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 0, 0, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK_SELECTION_OBJECT, "audi", 1618661061000L, 0, 0, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Long> result = (Map<String, Long>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_REACTION_TIME_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		long cars = result.get("cars");
		long time = new Timestamp(onClick.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		assertTrue(cars == time);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo del tiempo de reacción / número de opciones en un componente de selección.
	 */
	public void reactionTimeNumberOptionsSelectionObject() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario15", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena15";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("inputTeclado", scene, user, new Date().getTime(), 25, 35, 5, 10, Constantes.COMPONENT_TEXT_FIELD, null));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		Event initial = new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 0, 0, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK_SELECTION_OBJECT, "audi", 1618661061000L, 0, 0, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_REACTION_TIME_NUMBER_OPTIONS_SELECTION_OBJECT)
				.calculate(scene, user.getSessionId());
		
		double cars = result.get("cars");
		long time = new Timestamp(onClick.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		double options = 4.0;
		double timeOptions =  (double)Math.round( (time / options)  * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		assertTrue(cars == timeOptions);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo del tiempo del movimiento del ratón desde el primer evento hasta que se pulsa sobre el componente
	 */
	public void mouseMovementTime() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario16", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena16";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 20, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 20, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 20, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 20, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 20, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars"));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 0, 0, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 0, 0, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661061000L, 20, 30, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Long> result = (Map<String, Long>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_MOUSE_MOVEMENT_TIME)
				.calculate(scene, user.getSessionId());
		
		long cars = result.get("cars");
		long audi = result.get("audi");
		long time = new Timestamp(onClick.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime();
		assertTrue(cars == time);
		assertTrue(audi == 0L);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la distancia real entre el primer evento del ratón y el componente
	 */
	public void realDistanceBetweenMouseAndComponent() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario17", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena17";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 40, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 40, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 40, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 40, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars"));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		Event event1 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user);
		events.add(event1);
		Event event2 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 20, 20, "-1", -1, user);
		events.add(event2);
		Event event3 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 25, "-1", -1, user);
		events.add(event3);
		Event event4 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 25, "-1", -1, user);
		events.add(event4);
		Event event5 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 32, 25, "-1", -1, user);
		events.add(event5);
		Event event6 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 35, 25, "-1", -1, user);
		events.add(event6);
		Event event7 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 40, 30, "-1", -1, user);
		events.add(event7);
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661061000L, 40, 30, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_REAL_DISTANCE_BETWEEN_MOUSE_AND_COMPONENT)
				.calculate(scene, user.getSessionId());
		
		double cars = result.get("cars");
		double audi = result.get("audi");
		
		double d = 0.0;
		d += distance(initial.getX(), initial.getY(), event1.getX(), event1.getY());
		d += distance(event1.getX(), event1.getY(), event2.getX(), event2.getY());
		d += distance(event2.getX(), event2.getY(), event3.getX(), event3.getY());
		d += distance(event3.getX(), event3.getY(), event4.getX(), event4.getY());
		d += distance(event4.getX(), event4.getY(), event5.getX(), event5.getY());
		d += distance(event5.getX(), event5.getY(), event6.getX(), event6.getY());
		d += distance(event6.getX(), event6.getY(), event7.getX(), event7.getY());
		d += distance(event7.getX(), event7.getY(), onClick.getX(), onClick.getY());
		
		assertTrue(cars == (double)Math.round(d * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
		assertTrue(audi == 0L);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la distancia ideal entre el primer evento del ratón y el componente
	 */
	public void idealDistanceBetweenMouseAndComponent() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario18", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena18";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		ComponentData carsComp = new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null);
		components.add(carsComp);
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 40, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 40, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 40, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		ComponentData audiComp = new ComponentData("audi", scene, user, new Date().getTime(), 40, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars");
		components.add(audiComp);

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 20, 20, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 32, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 35, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 40, 30, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661061000L, 40, 30, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_IDEAL_DISTANCE_BETWEEN_MOUSE_AND_COMPONENT)
				.calculate(scene, user.getSessionId());
		
		double cars = result.get("cars");
		double audi = result.get("audi");
		
		double d = 0.0;
		double dAudi = 0.0;
		d += distance(initial.getX(), initial.getY(), carsComp.getX(), carsComp.getY());
		dAudi += distance(initial.getX(), initial.getY(), audiComp.getX(), audiComp.getY());
		
		assertTrue(cars == (double)Math.round(d * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
		assertTrue(audi == (double)Math.round(dAudi * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la diferencia entre las distancias reales e ideales sobre un componente
	 */
	public void differenceIdealAndRealDistance() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario18", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena18";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 40, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 40, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 40, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 40, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars"));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		Event event1 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user);
		events.add(event1);
		Event event2 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 20, 20, "-1", -1, user);
		events.add(event2);
		Event event3 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 25, "-1", -1, user);
		events.add(event3);
		Event event4 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 25, "-1", -1, user);
		events.add(event4);
		Event event5 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 32, 25, "-1", -1, user);
		events.add(event5);
		Event event6 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 35, 25, "-1", -1, user);
		events.add(event6);
		Event event7 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 40, 30, "-1", -1, user);
		events.add(event7);
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661061000L, 40, 30, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
				
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_DIFFERENCE_IDEAL_AND_REAL_DISTANCE)
				.calculate(scene, user.getSessionId());
		
		double cars = result.get("cars");
		
		double ideal = 0.0;
		ideal += distance(initial.getX(), initial.getY(), onClick.getX(), onClick.getY());
		
		double real = 0.0;
		real += distance(initial.getX(), initial.getY(), event1.getX(), event1.getY());
		real += distance(event1.getX(), event1.getY(), event2.getX(), event2.getY());
		real += distance(event2.getX(), event2.getY(), event3.getX(), event3.getY());
		real += distance(event3.getX(), event3.getY(), event4.getX(), event4.getY());
		real += distance(event4.getX(), event4.getY(), event5.getX(), event5.getY());
		real += distance(event5.getX(), event5.getY(), event6.getX(), event6.getY());
		real += distance(event6.getX(), event6.getY(), event7.getX(), event7.getY());
		real += distance(event7.getX(), event7.getY(), onClick.getX(), onClick.getY());
		
		double difference = (double)Math.round( (real - ideal) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		assertTrue(cars == difference);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la velocidad del ratón sobre la distancia real
	 */
	public void mouseSpeedRealDistance() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario19", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena19";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 40, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 40, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 40, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 40, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars"));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		Event event1 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user);
		events.add(event1);
		Event event2 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 20, 20, "-1", -1, user);
		events.add(event2);
		Event event3 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 25, "-1", -1, user);
		events.add(event3);
		Event event4 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 25, "-1", -1, user);
		events.add(event4);
		Event event5 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 32, 25, "-1", -1, user);
		events.add(event5);
		Event event6 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 35, 25, "-1", -1, user);
		events.add(event6);
		Event event7 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 40, 30, "-1", -1, user);
		events.add(event7);
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661061000L, 40, 30, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
				
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_MOUSE_SPEED_REAL_DISTANCE)
				.calculate(scene, user.getSessionId());
		
		double cars = result.get("cars");
		double audi = result.get("audi");
		
		double real = 0.0;
		real += distance(initial.getX(), initial.getY(), event1.getX(), event1.getY());
		real += distance(event1.getX(), event1.getY(), event2.getX(), event2.getY());
		real += distance(event2.getX(), event2.getY(), event3.getX(), event3.getY());
		real += distance(event3.getX(), event3.getY(), event4.getX(), event4.getY());
		real += distance(event4.getX(), event4.getY(), event5.getX(), event5.getY());
		real += distance(event5.getX(), event5.getY(), event6.getX(), event6.getY());
		real += distance(event6.getX(), event6.getY(), event7.getX(), event7.getY());
		real += distance(event7.getX(), event7.getY(), onClick.getX(), onClick.getY());
		
		Long time = (new Timestamp(onClick.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime()) / 1000;
		
		double speed = (double)Math.round( (real / time.doubleValue()) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		assertTrue(cars == speed);
		assertTrue(audi == 0L);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la velocidad del ratón sobre la distancia ideal
	 */
	public void mouseSpeedIdealDistance() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario20", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena20";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		components.add(new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null));
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 40, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 40, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 40, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 40, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars"));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		Event event1 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user);
		events.add(event1);
		Event event2 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 20, 20, "-1", -1, user);
		events.add(event2);
		Event event3 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 25, "-1", -1, user);
		events.add(event3);
		Event event4 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 25, "-1", -1, user);
		events.add(event4);
		Event event5 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 32, 25, "-1", -1, user);
		events.add(event5);
		Event event6 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 35, 25, "-1", -1, user);
		events.add(event6);
		Event event7 = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 40, 30, "-1", -1, user);
		events.add(event7);
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661061000L, 40, 30, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
				
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_MOUSE_SPEED_IDEAL_DISTANCE)
				.calculate(scene, user.getSessionId());
		
		double cars = result.get("cars");
		double audi = result.get("audi");
		
		double ideal = 0.0;
		ideal += distance(initial.getX(), initial.getY(), onClick.getX(), onClick.getY());
		
		Long time = (new Timestamp(onClick.getTimeStamp()).getTime() - new Timestamp(initial.getTimeStamp()).getTime()) / 1000;
		
		double speed = (double)Math.round( (ideal / time.doubleValue()) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		assertTrue(cars == speed);
		assertTrue(audi == 0L);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la precisión sobre el centro de un componente
	 */
	public void mouseAccuraccy() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario21", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena21";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		ComponentData rb = new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null);
		components.add(rb);
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 40, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 40, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 40, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 40, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars"));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 20, 20, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 32, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 35, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 40, 30, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661061000L, 42, 32, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_MOUSE_ACCURACY)
				.calculate(scene, user.getSessionId());
		
		double cars = result.get("cars");
		double audi = result.get("audi");
		
		double d = distance(rb.getX(), rb.getY(), onClick.getX(), onClick.getY());
		
		assertTrue(cars == (double)Math.round(d * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS);
		assertTrue(audi == 0L);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo del número de correcciones de ratón hasta pulsar en el componente
	 */
	public void mouseCorrections() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario22", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena22";
		
		//Registramos un componente de tipo texto
		List<ComponentData> components = new ArrayList<ComponentData>();
		ComponentData rb = new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 5, 10, Constantes.COMPONENT_RADIO_BUTTON, null);
		components.add(rb);
		components.add(new ComponentData("volvo", scene, user, new Date().getTime(), 40, 31, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("saab", scene, user, new Date().getTime(), 40, 32, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("fiat", scene, user, new Date().getTime(), 40, 33, 5, 10, Constantes.COMPONENT_OPTION, "cars"));
		components.add(new ComponentData("audi", scene, user, new Date().getTime(), 40, 34, 5, 10, Constantes.COMPONENT_OPTION, "cars"));

		components.forEach((component) -> ExperimentDataFactory.getSceneComponentDAO().insertComponent(component));
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 18, 15, "-1", -1, user)); //1º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 20, 10, "-1", -1, user)); //2º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 10, "-1", -1, user)); //3º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 35, 10, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 30, 10, "-1", -1, user)); //4º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 30, 20, "-1", -1, user)); //5º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661061000L, 30, 30, "-1", -1, user)); //6º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 30, 20, "-1", -1, user)); //7º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661063000L, 20, 20, "-1", -1, user)); //8º corrección
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661064000L, 10, 20, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661065000L, 20, 20, "-1", -1, user)); //9º corrección
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661066000L, 42, 32, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661067000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_MOUSE_CORRECTIONS)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
		int audi = result.get("audi");
				
		assertTrue(cars == 9);
		assertTrue(audi == 0);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo del porcetaje de precisión
	 */
	public void mouseAccuraccyPercentage() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario23", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena23";
		
		//Registramos un componente
		ComponentData rb = new ComponentData("cars", scene, user, new Date().getTime(), 40, 30, 45, 35, Constantes.COMPONENT_TEXT_FIELD, null);
		ExperimentDataFactory.getSceneComponentDAO().insertComponent(rb);
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 10, 10, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 15, 15, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 18, 15, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 20, 10, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 30, 10, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 35, 10, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 30, 10, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 30, 20, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661061000L, 30, 30, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 30, 20, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661063000L, 20, 20, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661064000L, 10, 20, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661065000L, 20, 20, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661066000L, 42, 32, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661067000L, 0, 0, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Double> result = (Map<String, Double>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_MOUSE_ACCURACY_PERCENTAGE)
				.calculate(scene, user.getSessionId());
		
		double distance1 = (double)Math.round( (distance(rb.getX(), rb.getY(), rb.getxF(), rb.getyF())) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		double distance2 = (double)Math.round( (distance(rb.getX(), rb.getY(), onClick.getX(), onClick.getY())) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		
		double percentage = (double)Math.round( (distance2 * 100 / distance1) * Constantes.NUMBER_DECIMALS) / Constantes.NUMBER_DECIMALS;
		double cars = result.get("cars");
				
		assertTrue(cars == percentage);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la desviación sobre la trayectoria ideal, cuando va por debajo
	 */
	public void pathDeviation_below() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario24", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena24";
		
		//Registramos un componente
		ComponentData rb = new ComponentData("cars", scene, user, new Date().getTime(), 100, 70, 105, 75, Constantes.COMPONENT_TEXT_FIELD, null);
		ExperimentDataFactory.getSceneComponentDAO().insertComponent(rb);
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 20, 20, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 22, 30, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 25, 32, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 35, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 35, 40, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 40, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 50, 18, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 60, 18, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661061000L, 70, 10, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 80, 40, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661063000L, 90, 50, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661064000L, 95, 55, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661065000L, 100, 70, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661066000L, 102, 74, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661067000L, 105, 80, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_PATH_DEVIATION)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
				
		assertTrue(cars == -1);
	}
	
	@Test
	/**
	 * Prueba la estrategia del cálculo de la desviación sobre la trayectoria ideal, cuando va por encima
	 */
	public void pathDeviation_above() {
		//Se comienza registrando un usuario
		
		List<ExperimentDTO> dtos = experimentService.getExperiments();
		Optional<Experiment> optional = experimentDAO.findById(dtos.get(0).id);
		Experiment experiment = optional.get();
		User user = new User("usuario25", experiment, 999, 5694, new Date().getTime(),"596", "es", "0:0:0:0:0:0:1");
		
		userDAO.save(user);		
		String scene = "escena25";
		
		//Registramos un componente
		ComponentData rb = new ComponentData("cars", scene, user, new Date().getTime(), 100, 20, 105, 75, Constantes.COMPONENT_TEXT_FIELD, null);
		ExperimentDataFactory.getSceneComponentDAO().insertComponent(rb);
		
		//Registramos eventos
		List<Event> events = new ArrayList<Event>();
		events.add(new Event(scene, Constantes.EVENT_INIT_TRACKING, "-1", 1618660812000L, 0, 0, "-1", -1, user));
		Event initial = new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661052000L, 20, 20, "-1", -1, user);
		events.add(initial);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661053000L, 22, 30, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661054000L, 25, 32, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661056000L, 25, 35, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661057000L, 35, 40, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661058000L, 40, 25, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661059000L, 50, 18, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661060000L, 60, 18, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661061000L, 70, 10, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661062000L, 80, 40, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661063000L, 90, 30, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661064000L, 95, 35, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661065000L, 100, 20, "-1", -1, user));
		Event onClick = new Event(scene, Constantes.EVENT_ON_CLICK, "cars", 1618661066000L, 100, 20, "-1", -1, user);
		events.add(onClick);
		events.add(new Event(scene, Constantes.EVENT_ON_MOUSE_MOVE, "-1", 1618661067000L, 105, 16, "-1", -1, user));
		events.add(new Event(scene, Constantes.EVENT_TRACKIND_END, "-1", 1618661885000L, 0, 0, "-1", -1, user));
		
		events.forEach((event) -> ExperimentDataFactory.getEventDAO().insertEvent(event));
	
		@SuppressWarnings("unchecked")
		Map<String, Integer> result = (Map<String, Integer>) StrategyDataManager.getInstance().getStrategyData(ConstantesStrategys.STRATEGY_PATH_DEVIATION)
				.calculate(scene, user.getSessionId());
		
		int cars = result.get("cars");
				
		assertTrue(cars == 1);
	}
	
	private Double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
}

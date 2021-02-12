package service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.AttempsException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.types.Device;
import com.uniovi.es.model.types.Gender;
import com.uniovi.es.model.types.Laterality;
import com.uniovi.es.model.types.StatusExperiment;
import com.uniovi.es.persistence.DeviceDAO;
import com.uniovi.es.utils.Identifier;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExperimentServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestMethodOrder(Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ExperimentTest {

	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private ExperimentService experimentService;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;
	
	@Autowired
	private DeviceDAO deviceDAO;
	
	@Autowired
	private AuthenticationService authenticateUser;
	
	private static boolean primeraVez = true;
	
	@PostConstruct
	public void init() {
		if(primeraVez) {
			Device d = new Device("MOUSE");
			Device d1 = new Device("TOUCHPAD");
			
			deviceDAO.save(d);
			deviceDAO.save(d1);
			
			primeraVez = false;
		}
	}
	
	@Test
	/**
	 * Se prueba el registro de un experimento de manera correcta
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 */
	public void test10RegisterExperiment() throws InvestigatorException, ExperimentException, AttempsException {
			
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.username = "carlos123";
		dto.mail = "carlos@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "carlos123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);

		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
		
		experimentService.register(experientDTO);
		
		//COMPROBAMOS QUE SE REGISTRÓ CORRECTAMENTE
		ExperimentDTO exp = experimentService.getDetail(1L);
		
		assertNotNull(exp);
		assertEquals("Experimento en Langreo", exp.title);
		assertEquals("Prueba en ordenadores con niños de 12 a 16 años", exp.description);
		assertEquals(hoy, exp.birthDate);
		assertEquals(Gender.MALE.name(), exp.gender);
		assertEquals(Laterality.LEFT_HANDED.name(), exp.laterality);
		assertEquals(2L, exp.idDevice);
	}
	
	@Test
	/**
	 * Se prueba el registro de un experimento de manera incorrecta (título sin especificar)
	 * @throws ExperimentException el título es un campo obligatorio
	 */
	public void test11RegisterExperimentERROR105() throws ExperimentException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Garcia";
		dto.username = "juanin123";
		dto.mail = "juanin@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "juanin123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = null;
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("juanin@gmail.com").id;
		
		experientDTO.birthDate = new Date();
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 1L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("105", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el registro de un experimento de manera incorrecta (descripción sin especificar)
	 * @throws ExperimentException la descripción es un campo obligatorio
	 */
	public void test12RegisterExperimentERROR106() throws ExperimentException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pedro";
		dto.surname = "Garcia";
		dto.username = "pedro123";
		dto.mail = "pedro@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "pedro123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("pedro@gmail.com").id;
		
		experientDTO.birthDate = new Date();
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 1L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("106", e.getMessage());
		}
	}

	@Test
	/**
	 * Se prueba el registro de un experimento de manera incorrecta (el investigador no existe)
	 * @throws ExperimentException el investigador no existe
	 */
	public void test13RegisterExperimentERROR200() throws ExperimentException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Dolfi";
		dto.surname = "Garcia";
		dto.username = "dolfi123";
		dto.mail = "dolfi@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "dolfi123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = ID_NOT_EXIST;
		
		experientDTO.birthDate = new Date();
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 1L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("115", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de los datos de un experimento de manera correcta
	 * @throws ExperimentException
	 */
	public void test14UpdateExperiment() throws ExperimentException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Paco";
		dto.surname = "Garcia";
		dto.username = "paco123";
		dto.mail = "paco@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "paco123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Sama de Lanreo";
		experientDTO.description = "Prueba en ordenadores con niños de 22 a 44 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("paco@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
				
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
				
		//EDITAMOS UN EXPERIMENTO 
		experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en SAMA";
		experientDTO.description = "Prueba en ordenadores con adultos de 25 a 35 años";
		experientDTO.id = experiments.get(experiments.size()-1).id;
		
		experimentService.update(experientDTO);
		
		//COMPROBAMOS QUE SE HA EDITADO CORRECTAMENTE
		experientDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		
		assertNotNull(experientDTO);
		assertEquals("Experimento en SAMA", experientDTO.title);
		assertEquals("Prueba en ordenadores con adultos de 25 a 35 años", experientDTO.description);
		assertEquals(Gender.MALE.name(), experientDTO.gender);
		assertEquals(Laterality.LEFT_HANDED.name(), experientDTO.laterality);
		assertEquals(2L, experientDTO.idDevice);
	}
	
	@Test
	/**
	 * Se prueba la edición de un experimento de manera incorrecta (título sin especificar)
	 * @throws ExperimentException el título es un campo obligatorio
	 */
	public void test15UpdateExperimentERROR105() throws ExperimentException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juana";
		dto.surname = "Garcia";
		dto.username = "juana123";
		dto.mail = "juana@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "juana123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Sama de Lanreo";
		experientDTO.description = "Prueba en ordenadores con niños de 22 a 44 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("juana@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
				
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		Long id = experiments.get(experiments.size()-1).id;
		
		//EDITAMOS UN EXPERIMENTO
		experientDTO = new ExperimentDTO();
		experientDTO.title = null;
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.id = id;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("105", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de un experimento de manera incorrecta (descripción sin especificar)
	 * @throws ExperimentException la descripción es un campo obligatorio
	 */
	public void test16UpdateExperimentERROR106() throws ExperimentException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "julieta";
		dto.surname = "Garcia";
		dto.username = "julieta123";
		dto.mail = "julieta@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "julieta123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Sama de Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 22 a 44 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("julieta@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
				
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		Long id = experiments.get(experiments.size()-1).id;
		
		//EDITAMOS UN EXPERIMENTO
		experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "";
		experientDTO.id = id;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("106", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de un experimento de manera incorrecta (Experimento no existe)
	 * @throws ExperimentException experimento no existe
	 */
	public void test17UpdateExperimentERROR100() throws ExperimentException, InvestigatorException{

		
		//EDITAMOS UN EXPERIMENTO
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.id = ID_NOT_EXIST;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el detalle de un experimento de manera incorrecta (Experimento no existe)
	 * @throws ExperimentException experimento no existe
	 */
	public void test19DetailExperimentERROR100() throws ExperimentException{
			
		try {
			experimentService.getDetail(ID_NOT_EXIST);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("100", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a obtener la lista de investigadores asociados a un experimento
	 * @throws ExperimentException
	 */
	public void test20InvestigatorsOfExperiments() throws ExperimentException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Rodolfo";
		dto.surname = "Garcia";
		dto.username = "rodolfo123";
		dto.mail = "rodolfo@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "rodolfo123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de Rodolfo";
		experientDTO.description = "Prueba en ordenadores para Rodolfo";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("rodolfo@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
				
		experimentService.register(experientDTO);
		
		//recuperamos el experimento
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		List<InvestigatorDTO> investigators = experimentService.getInvestigatorsOfExperiment(experiments.get(experiments.size()-1).id);
		
		assertNotNull(investigators);
		assertEquals(1, investigators.size());
		assertEquals("Rodolfo", investigators.get(0).name);
		assertEquals("Garcia", investigators.get(0).surname);
		assertEquals("rodolfo123", investigators.get(0).username);
		assertEquals("rodolfo@gmail.com", investigators.get(0).mail);
	}
	
	@Test
	/**
	 * Se prueba a obtener la lista de investigadores asociados a un experimento que no existe
	 * @throws ExperimentException
	 */
	public void test21InvestigatorsOfExperimentsERROR100() throws ExperimentException{
		
		List<InvestigatorDTO> investigators = experimentService.getInvestigatorsOfExperiment(ID_NOT_EXIST);
		
		assertNotNull(investigators);
		assertEquals(0, investigators.size());		
	}
	
	@Test
	/**
	 * Se prueba a cambiar el estado de un experimento de CRETED a OPEN 
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 */
	public void test23OpenExperiment() throws InvestigatorException, ExperimentException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Javier";
		dto.surname = "Garcia";
		dto.username = "javi123";
		dto.mail = "javi@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "javi123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("javi@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		experimentDTO = experiments.get(experiments.size()-1);
		assertEquals(StatusExperiment.CREATED.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		Identifier identifier = new Identifier( experiments.get(experiments.size()-1).id);
		experimentService.open(identifier);
		
		experiments = experimentService.getExperiments();
		assertEquals(StatusExperiment.OPEN.name(), experiments.get(experiments.size()-1).status);
	}
	
	@Test
	/**
	 * Se prueba a cambiar el estado de un experimento a OPEN cuando no está CLOSED o CREATED
	 * @throws ExperimentException no se puede cambiar el estado del experimento de OPEN a OPEN
	 */
	public void test24OpenExperimentERROR101() throws ExperimentException, InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Sara";
		dto.surname = "Garcia";
		dto.username = "sara123";
		dto.mail = "sara@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "sara123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos OPEN";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("sara@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		Identifier identifier = new Identifier(experiments.get(experiments.size()-1).id);
		
		try {
			experimentService.open(identifier);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("101", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a eliminar un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException, el experimento está en estado OPEN
	 */
	public void test25DeleteExperimentERROR104() throws ExperimentException, InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Oriol";
		dto.surname = "Garcia";
		dto.username = "oriol123";
		dto.mail = "oriol@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "oriol123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos OPEN";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("oriol@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A DELETED
		Identifier identifier = new Identifier(experiments.get(experiments.size()-1).id);
		
		try {
			experimentService.delete(identifier);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("104", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a reabrir un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException no se puede reAbrir un experimento que está en estado ABIERTO
	 */
	public void test26ReOpenExperimentERROR102() throws ExperimentException, InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Arenillas";
		dto.surname = "Garcia";
		dto.username = "arenillas123";
		dto.mail = "arenillas@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "arenillas123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos OPEN";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("arenillas@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		Identifier identifier = new Identifier(experiments.get(experiments.size()-1).id);
		
		try {
			experimentService.reOpen(identifier);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("102", e.getMessage());
		}
		
	}
	
	@Test
	/**
	 * Se prueba a cerrar un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException
	 */
	public void test27CloseExperiment() throws ExperimentException, InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "David";
		dto.surname = "Garcia";
		dto.username = "david123";
		dto.mail = "david@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "david123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos";
		experimentDTO.description = "Prueba a pasar el experimento a otro estado";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("david@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A CLOSED
		Identifier identifier = new Identifier(experiments.get(experiments.size()-1).id);
		experimentService.close(identifier);
		
		experiments = experimentService.getExperiments();
		experimentDTO = experiments.get(experiments.size()-1);
		assertEquals(StatusExperiment.CLOSED.name(), experimentDTO.status);
	}
	
	@Test
	/**
	 * Se prueba a reAbrir un experimento que se encuentra en estado CLOSED
	 * @throws ExperimentException
	 */
	public void test28ReOpenExperiment() throws ExperimentException, InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Sofia";
		dto.surname = "Garcia";
		dto.username = "sofia123";
		dto.mail = "sofia@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "sofia123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos";
		experimentDTO.description = "Prueba a pasar el experimento a otro estado";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("sofia@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A CLOSED
		Identifier identifier = new Identifier(experiments.get(experiments.size()-1).id);
		experimentService.close(identifier);
		
		experiments = experimentService.getExperiments();
		experimentDTO = experiments.get(experiments.size()-1);
		assertEquals(StatusExperiment.CLOSED.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		identifier = new Identifier(experiments.get(experiments.size()-1).id);
		experimentService.reOpen(identifier);
		
		experiments = experimentService.getExperiments();
		experimentDTO = experiments.get(experiments.size()-1);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
	}
	
	@Test
	/**
	 * Se prueba a eliminar un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException
	 */
	public void test29DELETEExperiment() throws ExperimentException, InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Montes";
		dto.surname = "Garcia";
		dto.username = "montes123";
		dto.mail = "montes@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "montes123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos";
		experimentDTO.description = "Prueba a pasar el experimento a otro estado";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("montes@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A CLOSED
		Identifier identifier = new Identifier(experiments.get(experiments.size()-1).id);
		experimentService.close(identifier);
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.CLOSED.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A DELETED
		experimentService.delete(identifier);
		
		experiments = experimentService.getExperiments();
		experimentDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		assertEquals(StatusExperiment.DELETED.name(), experimentDTO.status);
	}
	
	@Test
	/**
	 * Se registra un experimento sin el género como campo obligatorio sin rellenar
	 * @throws InvestigatorException 
	 */
	public void test30RegisterExperimentERROR110() throws InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Messi";
		dto.surname = "Garcia";
		dto.username = "messi123";
		dto.mail = "messi@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "messi123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("messi@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = null;
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("110", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento sin la fecha de nacimento como campo obligatorio sin rellenar
	 * @throws InvestigatorException 
	 */
	public void test31RegisterExperimentERROR111() throws InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carles";
		dto.surname = "Garcia";
		dto.username = "carles123";
		dto.mail = "carles@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "carles123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carles@gmail.com").id;
		
		experientDTO.birthDate = null;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("111", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento con la lateralidad informada incorrectamente
	 * @throws InvestigatorException 
	 */
	public void test32RegisterExperimentERROR112() throws InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Luisito";
		dto.surname = "Garcia";
		dto.username = "suarez123";
		dto.mail = "suarez@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "suarez123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
				
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("suarez@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = "IZQUIERDA";
		experientDTO.idDevice = 2L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("112", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento sin un dispositivo sin especificar como campo obligatorio
	 * @throws InvestigatorException 
	 */
	public void test33RegisterExperimentERROR113() throws InvestigatorException, AttempsException {
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Gonzalo";
		dto.surname = "Garcia";
		dto.username = "gonzalo123";
		dto.mail = "gonzalo@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "gonzalo123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
				
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("gonzalo@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = null;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("113", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento con un dispositivo registrado en el sistema
	 * @throws InvestigatorException 
	 */
	public void test34RegisterExperimentERROR114() throws InvestigatorException, AttempsException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "De Arriba";
		dto.surname = "Garcia";
		dto.username = "dearriba123";
		dto.mail = "dearriba@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "dearriba123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("dearriba@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = ID_NOT_EXIST;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("114", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la seguridad de actualizar los datos de un experimento del que no se es manager
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test35UpdateExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Aitor";
		dto.surname = "Garcia";
		dto.username = "aitor123";
		dto.mail = "aitor@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "aitor123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("aitor@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = 1L;
		
		experimentService.register(experientDTO);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Elena";
		dto.surname = "Garcia";
		dto.username = "elena123";
		dto.mail = "elena@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.username = "elena123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//EDITAMOS UN EXPERIMENTO 
		experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en SAMA";
		experientDTO.description = "Prueba en ordenadores con adultos de 25 a 35 años";
		experientDTO.id = experiments.get(experiments.size()-1).id;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la seguridad de modificar el estado de un experimento del que no se es manager
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test36OpenExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Secun";
		dto.surname = "Garcia";
		dto.username = "secun123";
		dto.mail = "secun@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "secun123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("secun@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = 1L;
		
		experimentService.register(experientDTO);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Castri";
		dto.surname = "Garcia";
		dto.username = "castri123";
		dto.mail = "castri@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.username = "castri123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		try {
			experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la seguridad de modificar el estado de un experimento del que no se es manager
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test37DeleteExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Rut";
		dto.surname = "Garcia";
		dto.username = "ruti123";
		dto.mail = "ruti@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "ruti123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("ruti@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = 1L;
		
		experimentService.register(experientDTO);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Leyre";
		dto.surname = "Garcia";
		dto.username = "leyre123";
		dto.mail = "leyre@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.username = "leyre123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		try {
			experimentService.delete(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la seguridad de modificar el estado de un experimento del que no se es manager
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test38CloseExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Aida";
		dto.surname = "Garcia";
		dto.username = "aida123";
		dto.mail = "aida@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "aida123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("aida@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = 1L;
		
		experimentService.register(experientDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Lucas";
		dto.surname = "Garcia";
		dto.username = "lucas123";
		dto.mail = "lucas@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.username = "lucas123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		try {
			experimentService.close(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la seguridad de modificar el estado de un experimento del que no se es manager
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test39ReOpenExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Maria";
		dto.surname = "Garcia";
		dto.username = "maria123";
		dto.mail = "maria@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "maria123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("maria@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = 1L;
		
		experimentService.register(experientDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		experimentService.close(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "casals";
		dto.surname = "Garcia";
		dto.username = "casals123";
		dto.mail = "casals@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.username = "casals123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		try {
			experimentService.reOpen(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
}

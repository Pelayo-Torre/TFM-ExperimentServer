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
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.investigator.InvestigatorService;
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
class ExperimentTest {

	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private ExperimentService experimentService;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;
	
	@Autowired
	private DeviceDAO deviceDAO;
	
	@PostConstruct
	public void init() {
		Device d = new Device("MOUSE");
		Device d1 = new Device("TOUCHPAD");
		
		deviceDAO.save(d);
		deviceDAO.save(d1);
	}
	
	@Test
	/**
	 * Se prueba el registro de un experimento de manera correcta
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 */
	public void test10RegisterExperiment() throws InvestigatorException, ExperimentException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.username = "carlos23";
		dto.mail = "carlos@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
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
	public void test11RegisterExperimentERROR105() throws ExperimentException, InvestigatorException{
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = null;
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		experientDTO.birthDate = new Date();
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 1L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("El título es obligatorio");
		} catch (ExperimentException e) {
			Assert.assertEquals("105", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el registro de un experimento de manera incorrecta (descripción sin especificar)
	 * @throws ExperimentException la descripción es un campo obligatorio
	 */
	public void test12RegisterExperimentERROR106() throws ExperimentException, InvestigatorException{
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		experientDTO.birthDate = new Date();
		experientDTO.gender = Gender.MALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 1L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("La descripción es obligatoria");
		} catch (ExperimentException e) {
			Assert.assertEquals("106", e.getMessage());
		}
	}

	@Test
	/**
	 * Se prueba el registro de un experimento de manera incorrecta (el investigador no existe)
	 * @throws ExperimentException el investigador no existe
	 */
	public void test13RegisterExperimentERROR200() throws ExperimentException, InvestigatorException{
		
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
			Assert.fail("El investigador no existe");
		} catch (InvestigatorException e) {
			Assert.assertEquals("200", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de los datos de un experimento de manera correcta
	 * @throws ExperimentException
	 */
	public void test14UpdateExperiment() throws ExperimentException{
		
		//EDITAMOS UN EXPERIMENTO 
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en SAMA";
		experientDTO.description = "Prueba en ordenadores con adultos de 25 a 35 años";
		experientDTO.id = 1L;
		
		experimentService.update(experientDTO);
		
		//COMPROBAMOS QUE SE HA EDITADO CORRECTAMENTE
		experientDTO = experimentService.getDetail(1L);
		
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
	public void test15UpdateExperimentERROR105() throws ExperimentException{
		
		//EDITAMOS UN EXPERIMENTO
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = null;
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.id = 1L;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("El título es obligatorio");
		} catch (ExperimentException e) {
			Assert.assertEquals("105", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de un experimento de manera incorrecta (descripción sin especificar)
	 * @throws ExperimentException la descripción es un campo obligatorio
	 */
	public void test16UpdateExperimentERROR106() throws ExperimentException{
		
		//EDITAMOS UN EXPERIMENTO
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "";
		experientDTO.id = 1L;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("La descripción es obligatoria");
		} catch (ExperimentException e) {
			Assert.assertEquals("106", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de un experimento de manera incorrecta (Experimento no existe)
	 * @throws ExperimentException experimento no existe
	 */
	public void test17UpdateExperimentERROR100() throws ExperimentException{
		
		//EDITAMOS UN EXPERIMENTO
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.id = ID_NOT_EXIST;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("El experimento no existe");
		} catch (ExperimentException e) {
			Assert.assertEquals("100", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el detalle de un experimento de manera correcta
	 * @throws ExperimentException
	 */
	public void test18DetailExperiment() throws ExperimentException{
			
		ExperimentDTO dto = experimentService.getDetail(1L);
		
		assertNotNull(dto);
		assertEquals("Experimento en SAMA", dto.title);
		assertEquals("Prueba en ordenadores con adultos de 25 a 35 años", dto.description);
		
	}
	
	@Test
	/**
	 * Se prueba el detalle de un experimento de manera incorrecta (Experimento no existe)
	 * @throws ExperimentException experimento no existe
	 */
	public void test19DetailExperimentERROR100() throws ExperimentException{
			
		try {
			experimentService.getDetail(ID_NOT_EXIST);
			Assert.fail("El experimento no existe");
		} catch (ExperimentException e) {
			Assert.assertEquals("100", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a obtener la lista de investigadores asociados a un experimento
	 * @throws ExperimentException
	 */
	public void test20InvestigatorsOfExperiments() throws ExperimentException{
		
		List<InvestigatorDTO> investigators = experimentService.getInvestigatorsOfExperiment(1L);
		
		assertNotNull(investigators);
		assertEquals(1, investigators.size());
		assertEquals("Carlos", investigators.get(0).name);
		assertEquals("Garcia", investigators.get(0).surname);
		assertEquals("carlos23", investigators.get(0).username);
		assertEquals("carlos@gmail.com", investigators.get(0).mail);
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
	 * Se prueba a obtener la lista de experimentos de la aplicación
	 */
	public void test22AllExperiments() {
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		assertNotNull(experiments);
		assertEquals(1, experiments.size());		
	}
	
	@Test
	/**
	 * Se prueba a cambiar el estado de un experimento de CRETED a OPEN 
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 */
	public void test23OpenExperiment() throws InvestigatorException, ExperimentException {
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		experimentDTO.birthDate = new Date();
		experimentDTO.gender = Gender.MALE.name();
		experimentDTO.laterality = Laterality.LEFT_HANDED.name();
		experimentDTO.idDevice = 1L;
		
		experimentService.register(experimentDTO);
		
		experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.CREATED.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		Identifier identifier = new Identifier(2L);
		experimentService.open(identifier);
		
		experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
	}
	
	@Test
	/**
	 * Se prueba a cambiar el estado de un experimento a OPEN cuando no está CLOSED o CREATED
	 * @throws ExperimentException no se puede cambiar el estado del experimento de OPEN a OPEN
	 */
	public void test24OpenExperimentERROR101() throws ExperimentException {
		
		ExperimentDTO experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		Identifier identifier = new Identifier(2L);
		
		try {
			experimentService.open(identifier);
			Assert.fail("El experimento no se encuentra en estado CLOSED o CREATED");
		} catch (ExperimentException e) {
			Assert.assertEquals("101", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a eliminar un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException, el experimento está en estado OPEN
	 */
	public void test25DeleteExperimentERROR104() throws ExperimentException {
		ExperimentDTO experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A DELETED
		Identifier identifier = new Identifier(2L);
		
		try {
			experimentService.delete(identifier);
			Assert.fail("El experimento no se encuentra en estado CLOSED o CREATED");
		} catch (ExperimentException e) {
			Assert.assertEquals("104", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a reabrir un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException no se puede reAbrir un experimento que está en estado ABIERTO
	 */
	public void test26ReOpenExperimentERROR102() throws ExperimentException {
		ExperimentDTO experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		Identifier identifier = new Identifier(2L);
		
		try {
			experimentService.reOpen(identifier);
			Assert.fail("El experimento no se encuentra en estado CLOSED");
		} catch (ExperimentException e) {
			Assert.assertEquals("102", e.getMessage());
		}
		
	}
	
	@Test
	/**
	 * Se prueba a cerrar un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException
	 */
	public void test27CloseExperiment() throws ExperimentException {
		
		ExperimentDTO experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A CLOSED
		Identifier identifier = new Identifier(2L);
		experimentService.close(identifier);
		
		experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.CLOSED.name(), experimentDTO.status);
	}
	
	@Test
	/**
	 * Se prueba a reAbrir un experimento que se encuentra en estado CLOSED
	 * @throws ExperimentException
	 */
	public void test28ReOpenExperiment() throws ExperimentException {
		
		ExperimentDTO experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.CLOSED.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A OPEN
		Identifier identifier = new Identifier(2L);
		experimentService.reOpen(identifier);
		
		experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
	}
	
	@Test
	/**
	 * Se prueba a eliminar un experimento que se encuentra en estado OPEN
	 * @throws ExperimentException
	 */
	public void test29DELETEExperiment() throws ExperimentException {
		
		ExperimentDTO experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.OPEN.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A CLOSED
		Identifier identifier = new Identifier(2L);
		experimentService.close(identifier);
		
		experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.CLOSED.name(), experimentDTO.status);
		
		//LE CAMBIAMOS EL ESTADO A DELETED
		experimentService.delete(identifier);
		
		experimentDTO = experimentService.getDetail(2L);
		assertEquals(StatusExperiment.DELETED.name(), experimentDTO.status);
	}
	
	@Test
	/**
	 * Se registra un experimento sin el género como campo obligatorio sin rellenar
	 * @throws InvestigatorException 
	 */
	public void test30RegisterExperimentERROR110() throws InvestigatorException {
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = null;
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("El género de los usuarios de un experimento es un campo obligatorio");
		} catch (ExperimentException e) {
			Assert.assertEquals("110", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento sin la fecha de nacimento como campo obligatorio sin rellenar
	 * @throws InvestigatorException 
	 */
	public void test31RegisterExperimentERROR111() throws InvestigatorException {
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		experientDTO.birthDate = null;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();
		experientDTO.idDevice = 2L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("La edad de los usuarios de un experimento es un campo obligatorio");
		} catch (ExperimentException e) {
			Assert.assertEquals("111", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento con la lateralidad informada incorrectamente
	 * @throws InvestigatorException 
	 */
	public void test32RegisterExperimentERROR112() throws InvestigatorException {
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = "IZQUIERDA";
		experientDTO.idDevice = 2L;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("La lateralidad de los usuarios de un experimento es un campo obligatorio");
		} catch (ExperimentException e) {
			Assert.assertEquals("112", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento sin un dispositivo sin especificar como campo obligatorio
	 * @throws InvestigatorException 
	 */
	public void test33RegisterExperimentERROR113() throws InvestigatorException {
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = null;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("El dispositivo sobre el que se realizará un experimento es un campo obligatorio");
		} catch (ExperimentException e) {
			Assert.assertEquals("113", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento con un dispositivo registrado en el sistema
	 * @throws InvestigatorException 
	 */
	public void test34RegisterExperimentERROR114() throws InvestigatorException {
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
		
		Date hoy = new Date();
		experientDTO.birthDate = hoy;
		experientDTO.gender = Gender.FEMALE.name();
		experientDTO.laterality = Laterality.LEFT_HANDED.name();;
		experientDTO.idDevice = ID_NOT_EXIST;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("El dispositivo especificado no se encuentra registrado en el sistema");
		} catch (ExperimentException e) {
			Assert.assertEquals("114", e.getMessage());
		}
	}
}

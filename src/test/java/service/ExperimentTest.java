package service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;


import org.junit.Assert;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
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
import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.AttempsException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.types.DemographicDataType;
import com.uniovi.es.model.types.StatusExperiment;
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
	private AuthenticationService authenticateUser;
	
	@Test
	/**
	 * Se prueba el registro de un experimento de manera correcta
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 */
	public void test10RegisterExperiment() throws InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
			
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.mail = "carlos@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "carlos@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);

		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carlos@gmail.com").id;
				
		DemographicDataDTO dd = new DemographicDataDTO();
		dd.name = "Profesion";
		dd.type = DemographicDataType.STRING.name();
		
		DemographicDataDTO dd1 = new DemographicDataDTO();
		dd1.name = "Fecha de nacimiento";
		dd1.type = DemographicDataType.DATE.name();
		
		DemographicDataDTO dd2 = new DemographicDataDTO();
		dd2.name = "Lateralidad";
		dd2.type = DemographicDataType.NUMBER.name();
		
		experientDTO.demographicData = new ArrayList<DemographicDataDTO>();
		experientDTO.demographicData.add(dd);
		experientDTO.demographicData.add(dd1);
		experientDTO.demographicData.add(dd2);
		
		experimentService.register(experientDTO);
		
		//COMPROBAMOS QUE SE REGISTRÓ CORRECTAMENTE
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		ExperimentDTO exp = experimentService.getDetail(experiments.get(experiments.size() - 1).id);
		
		assertNotNull(exp);
		assertEquals("Experimento en Langreo", exp.title);
		assertEquals("Prueba en ordenadores con niños de 12 a 16 años", exp.description);
		assertEquals(3, exp.demographicData.size());
		
		for(DemographicDataDTO d : exp.demographicData) {
			if(d.type.equals(DemographicDataType.STRING.name())) {
				assertEquals("Profesion", d.name);
				assertEquals(DemographicDataType.STRING.name(), d.type);
			}
			else if(d.type.equals(DemographicDataType.DATE.name())) {
				assertEquals("Fecha de nacimiento", d.name);
				assertEquals(DemographicDataType.DATE.name(), d.type);
			}
			else if(d.type.equals(DemographicDataType.NUMBER.name())) {
				assertEquals("Lateralidad", d.name);
				assertEquals(DemographicDataType.NUMBER.name(), d.type);
			}
		}
	}
	
	@Test
	/**
	 * Se prueba el registro de un experimento de manera incorrecta (título sin especificar)
	 * @throws ExperimentException el título es un campo obligatorio
	 */
	public void test11RegisterExperimentERROR105() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException{
		 
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Garcia";
		dto.mail = "juanin@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "juanin@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = null;
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("juanin@gmail.com").id;
		
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
	public void test12RegisterExperimentERROR106() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pedro";
		dto.surname = "Garcia";
		dto.mail = "pedro@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "pedro@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("pedro@gmail.com").id;
		
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
		dto.mail = "dolfi@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "dolfi@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = ID_NOT_EXIST;
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("115", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de los datos de un experimento de manera correcta
	 * @throws ExperimentException
	 */
	public void test14UpdateExperiment() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Paco";
		dto.surname = "Garcia";
		dto.mail = "paco@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "paco@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Sama de Lanreo";
		experientDTO.description = "Prueba en ordenadores con niños de 22 a 44 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("paco@gmail.com").id;
		
		DemographicDataDTO dd = new DemographicDataDTO();
		dd.name = "Profesion";
		dd.type = DemographicDataType.STRING.name();
		
		DemographicDataDTO dd1 = new DemographicDataDTO();
		dd1.name = "Fecha de nacimiento";
		dd1.type = DemographicDataType.DATE.name();
		
		DemographicDataDTO dd2 = new DemographicDataDTO();
		dd2.name = "Lateralidad";
		dd2.type = DemographicDataType.NUMBER.name();
		
		experientDTO.demographicData = new ArrayList<DemographicDataDTO>();
		experientDTO.demographicData.add(dd);
		experientDTO.demographicData.add(dd1);
		experientDTO.demographicData.add(dd2);
				
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
				
		//EDITAMOS UN EXPERIMENTO 
		experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en SAMA";
		experientDTO.description = "Prueba en ordenadores con adultos de 25 a 35 años";
		experientDTO.id = experiments.get(experiments.size()-1).id;
		
		DemographicDataDTO dd3 = new DemographicDataDTO();
		dd3.name = "Profesion";
		dd3.type = DemographicDataType.STRING.name();
		
		experientDTO.demographicData = new ArrayList<DemographicDataDTO>();
		experientDTO.demographicData.add(dd3);
		
		experimentService.update(experientDTO);
		
		//COMPROBAMOS QUE SE HA EDITADO CORRECTAMENTE
		experientDTO = experimentService.getDetail(experiments.get(experiments.size()-1).id);
		
		assertNotNull(experientDTO);
		assertEquals("Experimento en SAMA", experientDTO.title);
		assertEquals("Prueba en ordenadores con adultos de 25 a 35 años", experientDTO.description);
		assertEquals("Profesion", experientDTO.demographicData.get(0).name);
		assertEquals(DemographicDataType.STRING.name(), experientDTO.demographicData.get(0).type);
	}
	
	@Test
	/**
	 * Se prueba la edición de un experimento de manera incorrecta (título sin especificar)
	 * @throws ExperimentException el título es un campo obligatorio
	 */
	public void test15UpdateExperimentERROR105() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juana";
		dto.surname = "Garcia";
		dto.mail = "juana@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "juana@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Sama de Lanreo";
		experientDTO.description = "Prueba en ordenadores con niños de 22 a 44 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("juana@gmail.com").id;
		
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
	public void test16UpdateExperimentERROR106() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "julieta";
		dto.surname = "Garcia";
		dto.mail = "julieta@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "julieta@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Sama de Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 22 a 44 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("julieta@gmail.com").id;
		
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
		} catch (ForbiddenException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de un experimento de manera incorrecta (El estado del experimento es CREADO)
	 * @throws ExperimentException el estado del experimento debe ser CREATED
	 */
	public void test18UpdateExperimentERROR114() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Remiro";
		dto.surname = "Garcia";
		dto.mail = "remiro@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "remiro@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Sama de Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 22 a 44 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("remiro@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		Long id = experiments.get(experiments.size()-1).id;
		
		//CAMBIAMOS EL ESTADO del experimento		
		experimentService.open(new Identifier(id));
		
		//EDITAMOS UN EXPERIMENTO
		experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Experimento en Langreo para edades comprendidas entre los 50 y 60 años";
		experientDTO.id = id;
		
		try {
			experimentService.update(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("114", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el detalle de un experimento de manera incorrecta (Experimento no existe)
	 * @throws ExperimentException experimento no existe
	 */
	public void test19DetailExperimentERROR100() throws ExperimentException, ForbiddenException{
			
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
	public void test20InvestigatorsOfExperiments() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Rodolfo";
		dto.surname = "Garcia";
		dto.mail = "rodolfo@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "rodolfo@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de Rodolfo";
		experientDTO.description = "Prueba en ordenadores para Rodolfo";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("rodolfo@gmail.com").id;
				
		experimentService.register(experientDTO);
		
		//recuperamos el experimento
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		List<InvestigatorDTO> investigators = experimentService.getInvestigatorsOfExperiment(experiments.get(experiments.size()-1).id);
		
		assertNotNull(investigators);
		assertEquals(1, investigators.size());
		assertEquals("Rodolfo", investigators.get(0).name);
		assertEquals("Garcia", investigators.get(0).surname);
		assertEquals("rodolfo@gmail.com", investigators.get(0).mail);
	}
	
	@Test
	/**
	 * Se prueba a obtener la lista de investigadores asociados a un experimento que no existe
	 * @throws ExperimentException
	 */
	public void test21InvestigatorsOfExperimentsERROR100() throws ExperimentException, ForbiddenException, InvestigatorException, AttempsException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Junior";
		dto.surname = "Garcia";
		dto.mail = "junior@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "junior@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		try {
			experimentService.getInvestigatorsOfExperiment(ID_NOT_EXIST);
			Assert.fail("Debe lanzarse excepción.");
		}
		catch(ForbiddenException e) {
			assertEquals("117", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a cambiar el estado de un experimento de CRETED a OPEN 
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 */
	public void test23OpenExperiment() throws InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Javier";
		dto.surname = "Garcia";
		dto.mail = "javi@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "javi@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("javi@gmail.com").id;
		
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
	public void test24OpenExperimentERROR101() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Sara";
		dto.surname = "Garcia";
		dto.mail = "sara@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "sara@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos OPEN";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("sara@gmail.com").id;
		
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
	public void test25DeleteExperimentERROR104() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Oriol";
		dto.surname = "Garcia";
		dto.mail = "oriol@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "oriol@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos OPEN";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("oriol@gmail.com").id;
		
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
	public void test26ReOpenExperimentERROR102() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Arenillas";
		dto.surname = "Garcia";
		dto.mail = "arenillas@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "arenillas@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos OPEN";
		experimentDTO.description = "Prueba a pasar el experimento a estado OPEN";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("arenillas@gmail.com").id;
		
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
	public void test27CloseExperiment() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "David";
		dto.surname = "Garcia";
		dto.mail = "david@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "david@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos";
		experimentDTO.description = "Prueba a pasar el experimento a otro estado";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("david@gmail.com").id;
		
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
	public void test28ReOpenExperiment() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Sofia";
		dto.surname = "Garcia";
		dto.mail = "sofia@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "sofia@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos";
		experimentDTO.description = "Prueba a pasar el experimento a otro estado";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("sofia@gmail.com").id;
		
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
	public void test29DELETEExperiment() throws ExperimentException, InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Montes";
		dto.surname = "Garcia";
		dto.mail = "montes@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "montes@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS UN NUEVO EXPERIMENTO
		ExperimentDTO experimentDTO = new ExperimentDTO();
		experimentDTO.title = "Experimento de prueba de estados erróneos";
		experimentDTO.description = "Prueba a pasar el experimento a otro estado";
		experimentDTO.idInvestigator = investigatorService.getInvestigatorByMail("montes@gmail.com").id;
		
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
	 * Se registra un experimento con un campo demográfico como null
	 * @throws InvestigatorException 
	 */
	public void test30RegisterExperimentERROR110() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Messi";
		dto.surname = "Garcia";
		dto.mail = "messi@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "messi@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("messi@gmail.com").id;
		
		DemographicDataDTO dd = null;
		
		experientDTO.demographicData = new ArrayList<DemographicDataDTO>();
		experientDTO.demographicData.add(dd);
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("110", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento con un dato demográfico sin valor
	 * @throws InvestigatorException 
	 */
	public void test31RegisterExperimentERROR111() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carles";
		dto.surname = "Garcia";
		dto.mail = "carles@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "carles@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("carles@gmail.com").id;
		
		DemographicDataDTO dd = new DemographicDataDTO();
		dd.type = DemographicDataType.STRING.name();
		
		experientDTO.demographicData = new ArrayList<DemographicDataDTO>();
		experientDTO.demographicData.add(dd);
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("111", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento con un dato demográfico sin tipo
	 * @throws InvestigatorException 
	 */
	public void test32RegisterExperimentERROR112() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Luisito";
		dto.surname = "Garcia";
		dto.mail = "suarez@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "suarez@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
				
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("suarez@gmail.com").id;
		
		DemographicDataDTO dd = new DemographicDataDTO();
		dd.name = "Profesion";
		
		experientDTO.demographicData = new ArrayList<DemographicDataDTO>();
		experientDTO.demographicData.add(dd);
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("112", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se registra un experimento con un dato demográfico de tipo incorrecto
	 * @throws InvestigatorException 
	 */
	public void test33RegisterExperimentERROR113() throws InvestigatorException, AttempsException, ForbiddenException {
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Gonzalo";
		dto.surname = "Garcia";
		dto.mail = "gonzalo@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "gonzalo@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
				
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("gonzalo@gmail.com").id;
		
		DemographicDataDTO dd = new DemographicDataDTO();
		dd.name = "Profesion";
		dd.type = "DOUBLE";
	
		experientDTO.demographicData = new ArrayList<DemographicDataDTO>();
		experientDTO.demographicData.add(dd);
		
		try {
			experimentService.register(experientDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("113", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la seguridad de actualizar los datos de un experimento del que no se es manager
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test35UpdateExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Aitor";
		dto.surname = "Garcia";
		dto.mail = "aitor@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "aitor@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("aitor@gmail.com").id;
		
		experimentService.register(experientDTO);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Elena";
		dto.surname = "Garcia";
		dto.mail = "elena@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "elena@gmail.com";
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
		} catch (ForbiddenException e) {
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
	public void test36OpenExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Secun";
		dto.surname = "Garcia";
		dto.mail = "secun@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "secun@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("secun@gmail.com").id;
		
		experimentService.register(experientDTO);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Castri";
		dto.surname = "Garcia";
		dto.mail = "castri@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "castri@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		try {
			experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
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
	public void test37DeleteExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Rut";
		dto.surname = "Garcia";
		dto.mail = "ruti@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "ruti@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("ruti@gmail.com").id;
		
		experimentService.register(experientDTO);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Leyre";
		dto.surname = "Garcia";
		dto.mail = "leyre@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "leyre@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		try {
			experimentService.delete(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
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
	public void test38CloseExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Aida";
		dto.surname = "Garcia";
		dto.mail = "aida@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "aida@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("aida@gmail.com").id;
		
		experimentService.register(experientDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		experimentService.open(new Identifier(experiments.get(experiments.size()-1).id));
		
		experiments = experimentService.getExperiments();
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Lucas";
		dto.surname = "Garcia";
		dto.mail = "lucas@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "lucas@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		try {
			experimentService.close(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
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
	public void test39ReOpenExperimentByInvestigatorNotManager() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Maria";
		dto.surname = "Garcia";
		dto.mail = "maria@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "maria@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("maria@gmail.com").id;
		
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
		dto.mail = "casals@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "casals@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		try {
			experimentService.reOpen(new Identifier(experiments.get(experiments.size()-1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("116", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la seguridad de visualizar el estado de un experimento del que no se es manager
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test40DetailExperimentByInvestigatorNotAssociated() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Jorge";
		dto.surname = "Garcia";
		dto.mail = "jorge@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "jorge@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("jorge@gmail.com").id;
		
		experimentService.register(experientDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS UN INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Perez";
		dto.surname = "Garcia";
		dto.mail = "perez@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "perez@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//Obtenemos el detalle del experimento anteriormente creado
		
		try {
			experimentService.getDetail(experiments.get(experiments.size() - 1).id);
			Assert.fail("Debe lanzarse excepción.");
		} catch(ForbiddenException e) {
			assertEquals("117", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a obtener la lista de experimentos de un investigador
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test41ListExperimentsByInvestigator() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Martin";
		dto.surname = "Garcia";
		dto.mail = "martin12@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "martin12@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de personas entre 30 y 40 años";
		experientDTO.description = "Prueba en ordenadores con personas de 30 a 40 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("martin12@gmail.com").id;
		
		experimentService.register(experientDTO);
		
		//Actualizamos los datos
		List<ExperimentDTO> experiments = investigatorService.getExperimentsAcceptedByIdInvestigator(experientDTO.idInvestigator);
		
		assertNotNull(experiments);
		assertEquals(1, experiments.size());
		assertEquals("Experimento de personas entre 30 y 40 años", experiments.get(0).title);
		assertEquals("Prueba en ordenadores con personas de 30 a 40 años", experiments.get(0).description);
	}
	
	@Test
	/**
	 * Se prueba a obtener la lista de experimentos de un investigador
	 * @throws InvestigatorException
	 * @throws AttempsException
	 * @throws ExperimentException
	 */
	public void test42ListExperimentsByInvestigatorWithOutExperiments() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Presa";
		dto.surname = "Garcia";
		dto.mail = "presa@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "presa@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		List<ExperimentDTO> experiments = investigatorService.getExperimentsAcceptedByIdInvestigator(
				investigatorService.getInvestigatorByMail("presa@gmail.com").id);
		
		assertNotNull(experiments);
		assertEquals(0, experiments.size());
	}
}

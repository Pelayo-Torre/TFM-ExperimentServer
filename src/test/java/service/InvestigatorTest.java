package service;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.business.petition.PetitionService;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.StatusPetition;
import com.uniovi.es.utils.Identifier;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExperimentServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestMethodOrder(Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class InvestigatorTest {
	
	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private ExperimentService experimentService;
	
	@Autowired 
	private PetitionService petitionService;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;

	@Test
	/**
	 * Registro de un investigador sin errores
	 * @throws InvestigatorException
	 */
	public void test10CreateInvestigator() throws InvestigatorException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pelayo";
		dto.surname = "Garcia Torre";
		dto.username = "pgarciat";
		dto.email = "pelayo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//COMPROBAMOS QUE SE HA ALMACENADO CORRECTAMENTE
		InvestigatorDTO investigator = investigatorService.getInvestigatorByMail("PELAYO@gmail.com");
		
		assertNotNull(investigator);
		assertEquals("Pelayo", investigator.name);
		assertEquals("Garcia Torre", investigator.surname);
		assertEquals("pgarciat", investigator.username);
		assertEquals("pelayo@gmail.com", investigator.email);
	}
	
	@Test
	/**
	 * Registro de un investigador con error 201 (El nombre es obligatorio)
	 * @throws InvestigatorException, nombre obligatorio
	 */
	public void test11CreateInvestigatorERROR201() throws InvestigatorException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = null;
		dto.username = "pgarcitorre";
		dto.surname = "Garcia Torre";
		dto.email = "pelayo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de nombre obligatorio.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("201", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Registro de un investigador con error 202 (Los apellidos son obligatorios)
	 * @throws InvestigatorException, apellidos obligatorios
	 */
	public void test12CreateInvestigatorERROR202() throws InvestigatorException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pelayo";
		dto.surname = "";
		dto.username = "pgarcitorre";
		dto.email = "pelayo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de apellidos obligatorios.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("202", e.getMessage());
		}
	}

	@Test
	/**
	 * Registro de un investigador con error 203 (Email obligatorio)
	 * @throws InvestigatorException, email obligatorio
	 */
	public void test13CreateInvestigatorERROR203() throws InvestigatorException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pelayo";
		dto.surname = "Garcia Torre";
		dto.username = "pgarcitorre";
		dto.email = null;
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de email obligatorio.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("203", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Registro de un investigador con error 204 (Email ya registrado)
	 * @throws InvestigatorException, email ya registrado
	 */
	public void test14CreateInvestigatorERROR204() throws InvestigatorException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pelayo";
		dto.surname = "Gonzalez";
		dto.username = "pgarcitorre45";
		dto.email = "pel@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR CON MISMO EMAIL
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Pelayo";
		dto2.surname = "Gonzalez";
		dto2.username = "pgartorre45";
		dto2.email = "PEL@GMAIL.COM";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto2);
			Assert.fail(
					"Debe lanzarse excepción de email ya registrado.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("204", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Registro de un investigador con error 205 (Username obligatorio)
	 * @throws InvestigatorException, username obligatorio
	 */
	public void test15CreateInvestigatorERROR205() throws InvestigatorException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pelayo";
		dto.surname = "Garcia Torre";
		dto.username = null;
		dto.email = "pelayo@hotmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de username obligatorio.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("205", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Registro de un investigador con error 206 (username ya registrado)
	 * @throws InvestigatorException, username ya registrado
	 */
	public void test16CreateInvestigatorERROR206() throws InvestigatorException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pelayo";
		dto.surname = "Gonzalez";
		dto.username = "pgarciTorre";
		dto.email = "pelTore@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR CON MISMO EMAIL
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Pelayo";
		dto2.surname = "Álvarez";
		dto2.username = "pgarciTORRe";
		dto2.email = "pelgar@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto2);
			Assert.fail(
					"Debe lanzarse excepción de username ya registrado.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("206", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el detalle de un investigador que se encuentra registrado
	 * @throws InvestigatorException
	 */
	public void test17getDetail() throws InvestigatorException{
		
		InvestigatorDTO investigator = investigatorService.getInvestigatorByMail("pelayo@gmail.com");
		
		//SE OBTIENE UN INVESTIGADOR CUYO IDENTIFICADOR SE ENCUENTRA REGISTRADO
		investigator = investigatorService.getDetail(investigator.id);
		
		assertNotNull(investigator);
		assertEquals("Pelayo", investigator.name);
		assertEquals("Garcia Torre", investigator.surname);
		assertEquals("pgarciat", investigator.username);
		assertEquals("pelayo@gmail.com", investigator.email);
	}
	
	@Test
	/**
	 * Se prueba el detalle de un investigador que no se encuentra registrado
	 * @throws InvestigatorException ya que el investigador no se encuentra registrado
	 */
	public void test18getDetailERROR200() throws InvestigatorException{
		
		//SE OBTIENE UN INVESTIGADOR CUYO IDENTIFICADOR NO SE ENCUENTRA REGISTRADO
		try {
			investigatorService.getDetail(ID_NOT_EXIST);
			Assert.fail("Debe lanzarse excepción debido a que el investigador no existe");
		} catch (InvestigatorException e) {
			Assert.assertEquals("200", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de los datos de un investigador de manera correcta
	 * @throws InvestigatorException
	 */
	public void test19updateInvestigator() throws InvestigatorException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Torre";
		dto.username = "juanele";
		dto.email = "juan@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//LO EDITAMOS
		dto = new InvestigatorDTO();
		dto.name = "Juan Antonio";
		dto.surname = "Llaneza";
		dto.username = "juanele27";
		dto.email = "juanantonio@gmail.com";
		dto.id = 3L;
		
		investigatorService.updateInvestigator(dto);
				
		//COMPROBAMOS QUE SE HA GUARDADO CORRECTAMENTE
		dto = investigatorService.getInvestigatorByMail("juanantonio@gmail.com");
		
		assertNotNull(dto);
		assertEquals("Juan Antonio", dto.name);
		assertEquals("Llaneza", dto.surname);
		assertEquals("juanele27", dto.username);
		assertEquals("juanantonio@gmail.com", dto.email);
	}
	
	@Test
	/**
	 * Edición de un investigador con error 201 (El nombre es obligatorio)
	 * @throws InvestigatorException, nombre obligatorio
	 */
	public void test20UpdateInvestigatorERROR201() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = null;
		dto.surname = "Llaneza";
		dto.username = "juanele";
		dto.email = "juanantonio@gmail.com";
		dto.id = 3L;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de nombre obligatorio.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("201", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 202 (Los apellidos son obligatorios)
	 * @throws InvestigatorException, apellidos obligatorios
	 */
	public void test21UpdateInvestigatorERROR202() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "";
		dto.username = "juanele";
		dto.email = "juanantonio@gmail.com";
		dto.id = 3L;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de apellidos obligatorios.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("202", e.getMessage());
		}
	}

	@Test
	/**
	 * Edición de un investigador con error 203 (Email obligatorio)
	 * @throws InvestigatorException, email obligatorio
	 */
	public void test22UpdateInvestigatorERROR203() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
		dto.username = "juanele";
		dto.email = "";
		dto.id = 3L;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de email obligatorio.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("203", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 204 (Email ya registrado)
	 * @throws InvestigatorException, email ya registrado
	 */
	public void test23UpdateInvestigatorERROR204() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
		dto.username = "juanele";
		dto.email = "pelAYo@gmail.com";
		dto.id = 3L;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de email ya registrado.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("204", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 205 (username obligatorio)
	 * @throws InvestigatorException, username obligatorio
	 */
	public void test24UpdateInvestigatorERROR205() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
		dto.username = "";
		dto.email = "juanllan@gmail.com";
		dto.id = 3L;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de username obligatorio.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("205", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 206 (username ya registrado)
	 * @throws InvestigatorException, username ya registrado
	 */
	public void test25UpdateInvestigatorERROR206() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
		dto.username = "JUANELE27";
		dto.email = "juaneleo@gmail.com";
		dto.id = 1L;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de username ya registrado.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("206", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 200 (No existe en base de datos)
	 * @throws InvestigatorException, investigador no registrado
	 */
	public void test26UpdateInvestigatorERROR200() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
		dto.username = "juanele";
		dto.email = "pelayo@gmail.com";
		dto.id = ID_NOT_EXIST;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de investigador no registrado.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("200", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a registrar un experimento y enviar una petición de un investigador sobre el experimento
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws PetitionException
	 */
	public void test27getPetitionsPending() throws InvestigatorException, ExperimentException, PetitionException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.username = "carlos007";
		dto.email = "carlos@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		dto = investigatorService.getInvestigatorByMail("carlos@gmail.com");
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de prueba";
		experientDTO.description = "Esto es para hacer una prueba";
		experientDTO.idInvestigator = dto.id;
		
		experimentService.register(experientDTO);
		
		//CREAMOS UNA PETICIÓN PARA ESE EXPERIMENTO CON OTRO INVESTIGADOR
		dto = investigatorService.getInvestigatorByMail("pelayo@gmail.com");
		
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = 1L;
		petitionDTO.idInvestigator = dto.id;
		petitionDTO.manager = true;
		
		petitionService.register(petitionDTO);
		
		//COMPROBAMOS QUE EL INVESTIGADOR 3 TIENE 1 PETICIÓN PENDIENTE DE ACEPTAR
		List<PetitionDTO> list = investigatorService.getPetitionsPendingByIdInvestigator(dto.id);
		
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(StatusPetition.PENDING.name(), list.get(0).statusPetition);
	}
	
	@Test
	/**
	 * Se prueba el número de experimentos asociados a un investigador
	 * @throws PetitionException
	 * @throws InvestigatorException
	 */
	public void test28getPetitionsAccepted() throws PetitionException, InvestigatorException {
		
		//SE ACEPTA LA PETICIÓN ANTERIOR
		Identifier identifier = new Identifier();
		identifier.setId(2L);
		petitionService.accept(identifier);
		
		InvestigatorDTO dto = investigatorService.getInvestigatorByMail("pelayo@gmail.com");
		
		//SE COMPRUEBA QUE EL INVESTIGADOR ANTERIOR TIENE 1 PETICIÓN ACEPTADA
		List<ExperimentDTO> experiments = investigatorService.getExperimentsAcceptedByIdInvestigator(dto.id);
		
		assertNotNull(experiments);
		assertEquals(1, experiments.size());
		assertEquals("Experimento de prueba", experiments.get(0).title);
		assertEquals("Esto es para hacer una prueba", experiments.get(0).description);
		assertTrue(1L == experiments.get(0).id);
	}
	
	@Test
	/**
	 * Se prueba a registrar un investigador sin contraseña
	 * @throws InvestigatorException la contraseña es un campo obligatoria
	 */
	public void test29CreateInvestigatorError207() throws InvestigatorException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.username = "carlos067807";
		dto.email = "car567los@gmail.com";
		dto.password = null;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de contraseña obligatoria.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("207", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a registrar un investigador con longitud de contraseña errónea
	 * @throws InvestigatorException la longitud de la contraseña es errónea
	 */
	public void test30CreateInvestigatorError208() throws InvestigatorException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.username = "carlos067807";
		dto.email = "car567los@gmail.com";
		dto.password = "123";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de contraseña no cumple longitud mínima.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("208", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a registrar un investigador con correo erróneo
	 * @throws InvestigatorException el formato del correo electrónico es erróneo
	 */
	public void test31CreateInvestigatorError209() throws InvestigatorException{
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.username = "carlos067807";
		dto.email = "car567losgmailcom";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail(
					"Debe lanzarse excepción de correo con formato erróneo.");
		} catch (InvestigatorException e) {
			Assert.assertEquals("209", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se comprueba que devuelve todos los investigadores de la aplicación
	 * @throws InvestigatorException
	 */
	public void test32getAllInvestigators() throws InvestigatorException{
		
		//Se comprueba el número de investigadores existentes
		List<InvestigatorDTO> investigators = investigatorService.getListInvestigators();
		
		assertNotNull(investigators);
		assertEquals(5, investigators.size());
	}
	
}

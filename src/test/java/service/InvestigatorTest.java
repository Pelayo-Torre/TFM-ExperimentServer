package service;

import static org.junit.Assert.*;

import java.util.List;


import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
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
import com.uniovi.es.business.authentication.AuthenticationService;
import com.uniovi.es.business.dto.AuthDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.business.petition.PetitionService;
import com.uniovi.es.exceptions.AttempsException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.types.StatusPetition;
import com.uniovi.es.utils.Identifier;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExperimentServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestMethodOrder(Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class InvestigatorTest {
	
	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private ExperimentService experimentService;
	
	@Autowired 
	private PetitionService petitionService;
	
	@Autowired
	private AuthenticationService authenticateUser;
	
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
		dto.mail = "pelayo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//COMPROBAMOS QUE SE HA ALMACENADO CORRECTAMENTE
		InvestigatorDTO investigator = investigatorService.getInvestigatorByMail("PELAYO@gmail.com");
		
		assertNotNull(investigator);
		assertEquals("Pelayo", investigator.name);
		assertEquals("Garcia Torre", investigator.surname);
		assertEquals("pelayo@gmail.com", investigator.mail);
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
		dto.surname = "Garcia Torre";
		dto.mail = "pelayo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("201", e.getMessage());
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
		dto.mail = "pelayo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("202", e.getMessage());
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
		dto.mail = null;
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("203", e.getMessage());
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
		dto.mail = "pel@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR CON MISMO EMAIL
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Pelayo";
		dto2.surname = "Gonzalez";
		dto2.mail = "PEL@GMAIL.COM";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto2);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("204", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el detalle de un investigador que se encuentra registrado
	 * @throws InvestigatorException
	 */
	public void test17getDetail() throws InvestigatorException{
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Esther";
		dto.surname = "Torre";
		dto.mail = "esther@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		InvestigatorDTO investigator = investigatorService.getInvestigatorByMail("esther@gmail.com");
		
		//SE OBTIENE UN INVESTIGADOR CUYO IDENTIFICADOR SE ENCUENTRA REGISTRADO
		investigator = investigatorService.getDetail(investigator.id);
		
		assertNotNull(investigator);
		assertEquals("Esther", investigator.name);
		assertEquals("Torre", investigator.surname);
		assertEquals("esther@gmail.com", investigator.mail);
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
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("200", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de los datos de un investigador de manera correcta
	 * @throws InvestigatorException
	 */
	public void test19updateInvestigator() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Torre";
		dto.mail = "juan@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "juan@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//LO EDITAMOS
		dto = new InvestigatorDTO();
		dto.name = "Juan Antonio";
		dto.surname = "Llaneza";
		dto.mail = "juanantonio@gmail.com";
		dto.id = investigatorService.getInvestigatorByMail("juan@gmail.com").id;
		
		investigatorService.updateInvestigator(dto);
				
		//COMPROBAMOS QUE SE HA GUARDADO CORRECTAMENTE
		dto = investigatorService.getInvestigatorByMail("juanantonio@gmail.com");
		
		assertNotNull(dto);
		assertEquals("Juan Antonio", dto.name);
		assertEquals("Llaneza", dto.surname);
		assertEquals("juanantonio@gmail.com", dto.mail);
	}
	
	@Test
	/**
	 * Edición de un investigador con error 201 (El nombre es obligatorio)
	 * @throws InvestigatorException, nombre obligatorio
	 */
	public void test20UpdateInvestigatorERROR201() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pedro";
		dto.surname = "Torre";
		dto.mail = "pedri@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "pedri@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		dto.name = null;
		dto.surname = "Torre";
		dto.mail = "pedri@gmail.com";
		dto.id = investigatorService.getInvestigatorByMail(dto.mail).id;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("201", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 202 (Los apellidos son obligatorios)
	 * @throws InvestigatorException, apellidos obligatorios
	 */
	public void test21UpdateInvestigatorERROR202() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Jaime";
		dto.surname = "Torre";
		dto.mail = "jaime@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
				
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "jaime@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
				
		dto.name = "Jaime";
		dto.surname = "";
		dto.mail = "jaime@gmail.com";
		dto.id = investigatorService.getInvestigatorByMail(dto.mail).id;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("202", e.getMessage());
		}
	}

	@Test
	/**
	 * Edición de un investigador con error 203 (Email obligatorio)
	 * @throws InvestigatorException, email obligatorio
	 */
	public void test22UpdateInvestigatorERROR203() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Fonso";
		dto.surname = "Torre";
		dto.mail = "fonso@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "fonso@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
				
		dto.name = "Fonso";
		dto.surname = "Torre";
		dto.mail = "";
		dto.id = investigatorService.getInvestigatorByMail("fonso@gmail.com").id;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("203", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 204 (Email ya registrado)
	 * @throws InvestigatorException, email ya registrado
	 */
	public void test23UpdateInvestigatorERROR204() throws InvestigatorException, AttempsException, ForbiddenException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Garga";
		dto.surname = "Torre";
		dto.mail = "garga@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//CREAMOS otro investigador
		dto = new InvestigatorDTO();
		dto.name = "Celjus";
		dto.surname = "Torre";
		dto.mail = "celjus@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "garga@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
				
		dto = new InvestigatorDTO();
		dto.name = "Garga";
		dto.surname = "Torre";
		dto.mail = "celjus@gmail.com";
		dto.id = investigatorService.getInvestigatorByMail("garga@gmail.com").id;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("204", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Edición de un investigador con error 210 (El investigador en sesión quiere actualizar los datos de otro investigador)
	 * @throws InvestigatorException, investigador no registrado
	 */
	public void test26UpdateInvestigatorERROR210() throws InvestigatorException, AttempsException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Sergio";
		dto.surname = "Torre";
		dto.mail = "sergio@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "sergio@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		dto = new InvestigatorDTO();
		dto.name = "Sergio";
		dto.surname = "Torre";
		dto.mail = "sergio@gmail.com";
		dto.id = ID_NOT_EXIST;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.updateInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("210", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a registrar un experimento y enviar una petición de un investigador sobre el experimento
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws PetitionException
	 */
	public void test27getPetitionsPending() throws InvestigatorException, ExperimentException, PetitionException, AttempsException, ForbiddenException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.mail = "carlos@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//REGISTRAMOS UN INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Luisa";
		dto.surname = "Garcia";
		dto.mail = "luisa@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		dto = investigatorService.getInvestigatorByMail("carlos@gmail.com");
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "carlos@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento de prueba";
		experientDTO.description = "Esto es para hacer una prueba";
		experientDTO.idInvestigator = dto.id;
		
		experimentService.register(experientDTO);
				
		//CREAMOS UNA PETICIÓN PARA ESE EXPERIMENTO CON OTRO INVESTIGADOR
		dto = investigatorService.getInvestigatorByMail("luisa@gmail.com");
		
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = 1L;
		petitionDTO.mail = dto.mail;
		petitionDTO.manager = true;
		
		petitionService.register(petitionDTO);
				
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "luisa@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//COMPROBAMOS QUE EL INVESTIGADOR 3 TIENE 1 PETICIÓN PENDIENTE DE ACEPTAR
		List<PetitionDTO> list = investigatorService.getPetitionsPendingByIdInvestigator(dto.id);
		
		assertNotNull(list);
		assertEquals(1, list.size());
		assertEquals(StatusPetition.PENDING.name(), list.get(0).statusPetition);
		
		//SE ACEPTA LA PETICIÓN ANTERIOR
		Identifier identifier = new Identifier();
		identifier.setId(list.get(0).id);
		petitionService.accept(identifier);
				
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
		dto.mail = "car567los@gmail.com";
		dto.password = null;
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("207", e.getMessage());
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
		dto.mail = "car567los@gmail.com";
		dto.password = "123";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("208", e.getMessage());
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
		dto.mail = "car567losgmailcom";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		try {
			investigatorService.registerInvestigator(dto);
			Assert.fail("Debe lanzarse excepción.");
		} catch (InvestigatorException e) {
			assertEquals("209", e.getMessage());
		}
	}

	
}

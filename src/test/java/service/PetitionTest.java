package service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.annotation.PostConstruct;

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
import com.uniovi.es.model.PetitionNotRegistered;
import com.uniovi.es.model.types.StatusPetition;
import com.uniovi.es.utils.Identifier;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExperimentServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestMethodOrder(Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class PetitionTest {
	
	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private ExperimentService experimentService;
	
	@Autowired 
	private PetitionService petitionService;
	
	@Autowired
	private AuthenticationService authenticateUser;
	
	private static boolean primeraVez = true;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;
	
	@PostConstruct
	public void init() throws InvestigatorException, AttempsException {
		if(primeraVez) {
			//REGISTRAMOS EL INVESTIGADOR
			InvestigatorDTO dto = new InvestigatorDTO();
			dto.name = "Pelayo";
			dto.surname = "Garcia Torre";
			dto.mail = "pelayo1234@gmail.com";
			dto.password = "123456789";
			
			//LO GUARDAMOS EN BASE DE DATOS
			investigatorService.registerInvestigator(dto);
			
			//INICIAMOS SESIÓN
			AuthDTO authDTO = new AuthDTO();
			authDTO.mail = "pelayo1234@gmail.com";
			authDTO.password = "123456789";
			authenticateUser.authenticateUser(authDTO);
			
			primeraVez = false;
		}		
	}
	
	@Test
	/**
	 * Se prueba la creación de una petición de forma correcta
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws PetitionException
	 */
	public void test10RegisterPetition() throws InvestigatorException, ExperimentException, PetitionException, AttempsException, ForbiddenException {
		//CREAMOS otro investigador
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Juan";
		dto2.surname = "Torre";
		dto2.mail = "juan@gmail.com";
		dto2.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto2);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").id;
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
	
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = (experiments.get(experiments.size()-1).id);
		petitionDTO.mail = investigatorService.getInvestigatorByMail("juan@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		//COMPROBAMOS QUE LA PETICIÓN SE HA ALMACENADO CORRECTAMENTE
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		petitionDTO = petitions.get(petitions.size() -1);
		
		assertNotNull(petitionDTO);
		assertEquals(false, petitionDTO.creator);
		assertEquals(true, petitionDTO.manager);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
	}
	
	@Test
	/**
	 * Se prueba a enviar una petición de un investigador a un experimento que no existe
	 * @throws InvestigatorException
	 * @throws ExperimentException el experimento no está registrado en el sistema
	 * @throws PetitionException
	 */
	public void test11RegisterPetitionERROR100() throws InvestigatorException, ExperimentException, PetitionException, AttempsException, ForbiddenException{
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = ID_NOT_EXIST;
		petitionDTO.idInvestigator = investigatorService.getInvestigatorByMail("juan@gmail.com").id;
		petitionDTO.mail = "pelayinnnn@gmail.com";
		petitionDTO.manager = true;
		
		try {
			petitionService.register(petitionDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ExperimentException e) {
			assertEquals("100", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a enviar una petición de un investigador que no existe en el sistema
	 * @throws InvestigatorException el investigador no existe
	 * @throws ExperimentException
	 * @throws PetitionException
	 */
	public void test12RegisterPetitionERROR200() throws InvestigatorException, ExperimentException, PetitionException, AttempsException, ForbiddenException{
		
		//CREAMOS otro investigador
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Juanita";
		dto2.surname = "Torre";
		dto2.mail = "juanita@gmail.com";
		dto2.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto2);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "juanita@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("juanita@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		petitionDTO.mail = "ramonetee@gmail.com";
		petitionDTO.manager = true;
		
		petitionService.register(petitionDTO);
			
		//RECUPERAMOS LAS PETICIONES NO REGISTRADAS Y COMPROBAMOS QUE EXISTE una 
		PetitionNotRegistered p = petitionService.getPetitionNotRegistered(petitionDTO.mail, petitionDTO.idExperiment);
		
		assertNotNull(p);
		assertEquals(true, p.getManager());
		assertEquals(petitionDTO.mail, p.getMail());
		assertEquals(petitionDTO.idExperiment, p.getExperiment().getId());
	}
	
	@Test
	/**
	 * Se prueba a enviar una petición de un investigador que ya tienen una petición sobre el experimento
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws PetitionException, ya existe una petición
	 */
	public void test13RegisterPetitionERROR304() throws InvestigatorException, ExperimentException, PetitionException, AttempsException, ForbiddenException{
		
		//CREAMOS otro investigador
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Juanita";
		dto2.surname = "Torre";
		dto2.mail = "juanita67@gmail.com";
		dto2.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto2);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "juanita67@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("juanita67@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = (experiments.get(experiments.size()-1).id);
		petitionDTO.mail = investigatorService.getInvestigatorByMail("juanita67@gmail.com").mail;
		petitionDTO.manager = true;
		
		try {
			petitionService.register(petitionDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (PetitionException e) {
			assertEquals("304", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a enviar una petición sin especificar el correo electrónico
	 * @throws InvestigatorException
	 * @throws ExperimentException el experimento no está registrado en el sistema
	 * @throws PetitionException
	 */
	public void test14RegisterPetitionERROR308() throws InvestigatorException, ExperimentException, PetitionException, AttempsException, ForbiddenException{
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = 1L;
		petitionDTO.idInvestigator = null;
		petitionDTO.manager = true;
		
		try {
			petitionService.register(petitionDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (PetitionException e) {
			assertEquals("308", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el detalle de una petición que no se encuentra registrada en el sistema
	 * @throws PetitionException la petición no está registrada en el sistema
	 */
	public void test15DetailPetitionERROR300() throws PetitionException {
		
		try {
			petitionService.getDetail(ID_NOT_EXIST);
			Assert.fail("Debe lanzarse excepción.");
		} catch (PetitionException e) {
			assertEquals("300", e.getMessage());
		}
	}
	

	@Test
	/**
	 * Se prueba aceptar una petición que se encuentra en estado PENDING
	 * @throws PetitionException
	 */
	public void test16AcceptPetition() throws PetitionException, InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Joselu";
		dto.surname = "Garcia Torre";
		dto.mail = "joselu@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "joselu@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("joselu@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = (experiments.get(experiments.size()-1).id);
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		PetitionDTO petition = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petition.statusPetition);
		
		//Nos logueamos como el usuario que va a aceptar la petición
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//ACEPTAMOS LA PETICIÓN
		Identifier identifier = new Identifier(petition.id);
		petitionService.accept(identifier);
		
		petition = petitionService.getDetail(petition.id);
		assertEquals(StatusPetition.ACCEPTED.name(), petition.statusPetition);
	}
		
	@Test
	/**
	 * Se prueba a rechazar una petición que se encuentra en estado ACCEPTED
	 * @throws PetitionException No se puede rechazar una petición en estado ACCEPTED
	 */
	public void test18RejectPetitionERROR302() throws PetitionException, AttempsException, InvestigatorException, ExperimentException, ForbiddenException{
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Gracia";
		dto.surname = "Garcia Torre";
		dto.mail = "gracia@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "gracia@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("gracia@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = (experiments.get(experiments.size()-1).id);
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		PetitionDTO petition = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petition.statusPetition);
		
		//Nos logueamos como el usuario que va a aceptar la petición
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//ACEPTAMOS LA PETICIÓN
		Identifier identifier = new Identifier(petition.id);
		petitionService.accept(identifier);
		
		petition = petitionService.getDetail(petition.id);
		assertEquals(StatusPetition.ACCEPTED.name(), petition.statusPetition);
		
		try {
			petitionService.reject(identifier);
			Assert.fail("Debe lanzarse excepción.");
		} catch (PetitionException e) {
			assertEquals("302", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a cancelar una petición que se encuentra en estado ACCEPTED
	 * @throws PetitionException
	 */
	public void test19CancelPetition() throws PetitionException, AttempsException, InvestigatorException, ExperimentException, ForbiddenException{
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Flora";
		dto.surname = "Garcia Torre";
		dto.mail = "flora@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "flora@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("flora@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = (experiments.get(experiments.size()-1).id);
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		PetitionDTO petition = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petition.statusPetition);
		
		//Nos logueamos como el usuario que va a aceptar la petición
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		petitionService.accept(new Identifier(petition.id));
		
		petition = petitionService.getDetail(petition.id);
		assertEquals(StatusPetition.ACCEPTED.name(), petition.statusPetition);
		
		//CANCELAMOS LA PETICIÓN
		Identifier identifier = new Identifier(petition.id);
		petitionService.cancel(identifier);
		
		petition = petitionService.getDetail(petition.id);
		assertEquals(StatusPetition.CANCELLED.name(), petition.statusPetition);
	}
	
	@Test
	/**
	 * Se prueba a rechazar una petición
	 * @throws PetitionException
	 */
	public void test20RejectPetition() throws PetitionException, InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN NUEVO INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Gonzalo";
		dto.surname = "Torre";
		dto.mail = "gongartor@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "gongartor@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("gongartor@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//EL INVESTIGADOR RECHAZA LA PETICIÓN
		Identifier identifier = new Identifier(petitionDTO.id);
		petitionService.reject(identifier);
		
		petitionDTO = petitionService.getDetail(petitionDTO.id);
		assertEquals(StatusPetition.REJECTED.name(), petitionDTO.statusPetition);
	}
	
	//Nuevos tests
	@Test
	/**
	 * Se prueba a rechazar una aceptar una petición como investegador que no recibe la petición.
	 * @throws PetitionException
	 */
	public void test21ResponseAPetitionSecurityERROR001() throws PetitionException, InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
		//COMENZAMOS CREANDO UN NUEVO INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Alberto";
		dto.surname = "Torre";
		dto.mail = "alberto@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "alberto@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("alberto@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
		
		//La aceptamos como Alberto
		try {
			petitionService.accept(new Identifier(petitionDTO.id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("305", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a cancelar una petición en estado PENDING por el usuario que la recibe
	 * @throws PetitionException
	 */
	public void test22ResponseCancelAPetitionSecurityERROR002() throws PetitionException, InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
		//COMENZAMOS CREANDO UN NUEVO INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Daniela";
		dto.surname = "Torre";
		dto.mail = "daniela@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "daniela@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("daniela@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//La cancelamos como Pelayo. Al estar PENDING debe saltar error de sguridad
		try {
			petitionService.cancel(new Identifier(petitionDTO.id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("306", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a cancelar una petición en estado PENDING por el usuario que la envía
	 * @throws PetitionException
	 */
	public void test23ResponseCancelAPetitionSecurity() throws PetitionException, InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
		//COMENZAMOS CREANDO UN NUEVO INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Paola";
		dto.surname = "Torre";
		dto.mail = "paola@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "paola@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("paola@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
		
		//La cancelamos como Paola. Al estar PENDING no debe saltar error de seguridad
		petitionService.cancel(new Identifier(petitionDTO.id));
		petitions = petitionService.getPetitionsSent();
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.CANCELLED.name(), petitionDTO.statusPetition);
	}
	
	@Test
	/**
	 * Se prueba a cancelar una petición en estado ACEPTADA por el usuario que la recibe y la acepta
	 * @throws PetitionException
	 */
	public void test24ResponseCancelAcceptedPetitionSecurity() throws PetitionException, InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
		//COMENZAMOS CREANDO UN NUEVO INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Coral";
		dto.surname = "Torre";
		dto.mail = "coral@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "coral@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("coral@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//La aceptamos como Pelayo. Al estar PENDING no debe saltar error de sguridad
		petitionService.accept(new Identifier(petitionDTO.id));
		
		petitions = petitionService.getPetitionsReceived();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.ACCEPTED.name(), petitionDTO.statusPetition);
		
		//La cancelamos
		petitionService.cancel(new Identifier(petitionDTO.id));
		petitions = petitionService.getPetitionsReceived();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.CANCELLED.name(), petitionDTO.statusPetition);
	}
	
	@Test
	/**
	 * Se prueba a cancelar una petición en estado ACEPTADA por un usuario que ni la recibe ni la emite
	 * @throws PetitionException
	 */
	public void test25ResponseCancelAcceptedPetitionSecurityERROR003() throws PetitionException, InvestigatorException, ExperimentException, AttempsException, ForbiddenException {
		//COMENZAMOS CREANDO UN NUEVO INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Celsa";
		dto.surname = "Torre";
		dto.mail = "celsa@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "celsa@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en La Felgura";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("celsa@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		petitionDTO.mail = investigatorService.getInvestigatorByMail("pelayo1234@gmail.com").mail;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		List<PetitionDTO> petitions = petitionService.getPetitionsSent();
		
		petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
		
		//INICIAMOS SESIÓN
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//La aceptamos como Pelayo. Al estar PENDING no debe saltar error de sguridad
		petitionService.accept(new Identifier(petitionDTO.id));
		
		petitionDTO = petitionService.getDetail(petitionDTO.id);
		
		//petitionDTO = petitions.get(petitions.size() - 1);
		assertEquals(StatusPetition.ACCEPTED.name(), petitionDTO.statusPetition);
		
		//CREAMOS UN NUEVO INVESTIGADOR QUE NO ESTÁ RELACIONADO Y LA CANCELAMOS
		
		dto = new InvestigatorDTO();
		dto.name = "Mamasa";
		dto.surname = "Torre";
		dto.mail = "mamasa@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		authDTO = new AuthDTO();
		authDTO.mail = "mamasa@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//La cancelamos
		try {
			petitionService.cancel(new Identifier(petitionDTO.id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("307", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a enviar una petición por un investigador que no está asociado al experimento
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws PetitionException, ya existe una petición
	 */
	public void test26RegisterPetitionERROR309() throws InvestigatorException, ExperimentException, PetitionException, AttempsException, ForbiddenException{
		
		//CREAMOS otro investigador
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Pepita";
		dto2.surname = "Torre";
		dto2.mail = "pepita1@gmail.com";
		dto2.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto2);
		
		//INICIAMOS SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "pepita1@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("pepita1@gmail.com").id;
		
		experimentService.register(experientDTO);
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		
		//INICIAMOS SESIÓN con otro usuario sin asociar al experimento
		authDTO = new AuthDTO();
		authDTO.mail = "pelayo1234@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = (experiments.get(experiments.size()-1).id);
		petitionDTO.mail = "juan123456@gmail.com";
		petitionDTO.manager = false;
		
		try {
			petitionService.register(petitionDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("309", e.getMessage());
		}
	}

}

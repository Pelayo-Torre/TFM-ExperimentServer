package service;


import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.annotation.PostConstruct;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.Alphanumeric;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.uniovi.es.ExperimentServerApplication;
import com.uniovi.es.business.administration.AdministrationService;
import com.uniovi.es.business.authentication.AuthenticationService;
import com.uniovi.es.business.dto.AuthDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.RequestDTO;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.exceptions.AttempsException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.types.Role;
import com.uniovi.es.model.types.StatusRequest;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.utils.Identifier;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExperimentServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestMethodOrder(Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class AdministrationTest {
	
	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private AuthenticationService authenticateUser;
	
	@Autowired
	private AdministrationService administrationService;
	
	private static boolean primeraVez = true;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;
	
	@PostConstruct
	public void init() throws InvestigatorException, AttempsException {
		if(primeraVez) {
			
			//REGISTRAMOS EL INVESTIGADOR ADMINISTRADOR
			Investigator dto = new Investigator("celia@gmail.com");
			dto.setName("Celia");
			dto.setSurname("Garcia Torre");
			dto.setPassword(bCryptPasswordEncoder.encode("123456789"));
			dto.setToAdministrator(); 
			
			//LO GUARDAMOS EN BASE DE DATOS
			investigatorDAO.save(dto);
			primeraVez = false;
		}		
	}

	@Test
	public void test10RegisterRequest() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Mamasa";
		dto.surname = "Garcia";
		dto.mail = "mamasa@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "mamasa@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail(dto.mail);
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		administrationService.register(requestDTO);
		
		//OBTENEMOS LA solicitud
		List<RequestDTO> requests = administrationService.getPendingRequests();
		
		assertEquals(dto.id, requests.get(requests.size() - 1).idInvestigator);
		assertEquals(StatusRequest.PENDING.name(), requests.get(requests.size() - 1).status);
	}

	@Test
	public void test11RegisterRequestERROR_ROL() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		Investigator investigator = new Investigator("escanciano@gmail.com");
		investigator.setName("Escanciano");
		investigator.setSurname("Garcia Torre");
		investigator.setPassword(bCryptPasswordEncoder.encode("123456789"));
		investigator.setToValidated();
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorDAO.save(investigator);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "escanciano@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		InvestigatorDTO dto = investigatorService.getInvestigatorByMail(investigator.getMail());
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		try {
			administrationService.register(requestDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (AdministrationException e) {
			assertEquals("500", e.getMessage());
		}
	}
	
	@Test
	public void test12RegisterRequestERRORRequestsAlreadyRegistered() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Cunino";
		dto.surname = "Garcia";
		dto.mail = "cunino@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "cunino@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail(dto.mail);
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		administrationService.register(requestDTO);
		
		//OBTENEMOS LA solicitud
		List<RequestDTO> requests = administrationService.getPendingRequests();
		
		assertEquals(dto.id, requests.get(requests.size() - 1).idInvestigator);
		assertEquals(StatusRequest.PENDING.name(), requests.get(requests.size() - 1).status);
		
		//SE VUELVE A REGISTRAR UNA NUEVA SOLICITUD
		requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		try {
			administrationService.register(requestDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch(AdministrationException e) {
			assertEquals("501", e.getMessage());
		}
	}
	
	@Test
	public void test13RegisterRequestERROR_InvestigatorNotInSession() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Miguel";
		dto.surname = "Garcia";
		dto.mail = "miguel99@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);

		//COMENZAMOS CREANDO UN INVESTIGADOR
		dto = new InvestigatorDTO();
		dto.name = "Delita";
		dto.surname = "Garcia";
		dto.mail = "delita@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "delita@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("miguel99@gmail.com");
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		try {
			administrationService.register(requestDTO);
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("503", e.getMessage());
		}
	}
	
	@Test
	public void test14AcceptRequest() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Quiroga";
		dto.surname = "Garcia";
		dto.mail = "quiroga@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "quiroga@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("quiroga@gmail.com");
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		administrationService.register(requestDTO);
		
		//OBTENEMOS LA solicitud
		List<RequestDTO> requests = administrationService.getPendingRequests();
		
		assertEquals(dto.id, requests.get(requests.size() - 1).idInvestigator);
		assertEquals(StatusRequest.PENDING.name(), requests.get(requests.size() - 1).status);
			
		//NOS LOGUEAMOS AHORA COMO ADMINISTRADOR PARA ACEPTAR LA SOLICTUD
		authDTO = new AuthDTO();
		authDTO.mail = "celia@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		administrationService.accept(new Identifier(requests.get(requests.size() - 1).id));
		
		RequestDTO r = administrationService.getDetail(requests.get(requests.size() - 1).id);
		
		assertEquals(dto.id, r.idInvestigator);
		assertEquals(StatusRequest.ACCEPTED.name(), r.status);
		
		//OBTENEMOS EL INVESTIGADOR Y COMRPOBAMOS QUE HA CAMBIADO SUR ROL
		InvestigatorDTO inDto = investigatorService.getDetail(r.idInvestigator);
		
		assertEquals(Role.INVESTIGATOR_VALIDATED.name(), inDto.role);
	}
	
	@Test
	public void test15RejectRequest() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Victor";
		dto.surname = "Garcia";
		dto.mail = "chaves@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "chaves@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("chaves@gmail.com");
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		administrationService.register(requestDTO);
		
		//OBTENEMOS LA solicitud
		List<RequestDTO> requests = administrationService.getPendingRequests();
		
		assertEquals(dto.id, requests.get(requests.size() - 1).idInvestigator);
		assertEquals(StatusRequest.PENDING.name(), requests.get(requests.size() - 1).status);
			
		//NOS LOGUEAMOS AHORA COMO ADMINISTRADOR PARA ACEPTAR LA SOLICTUD
		authDTO = new AuthDTO();
		authDTO.mail = "celia@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		administrationService.reject(new Identifier(requests.get(requests.size() - 1).id));
		
		RequestDTO r = administrationService.getDetail(requests.get(requests.size() - 1).id);
		
		assertEquals(dto.id, r.idInvestigator);
		assertEquals(StatusRequest.REJECTED.name(), r.status);
		
		//OBTENEMOS EL INVESTIGADOR Y COMRPOBAMOS QUE NO HA CAMBIADO SUR ROL
		InvestigatorDTO inDto = investigatorService.getDetail(r.idInvestigator);
		
		assertEquals(Role.INVESTIGATOR_EVALUATION.name(), inDto.role);
	}
	
	@Test
	public void test16AcceptRequestERROR_InvestigatorIsNotAdministrator() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "leopoldo";
		dto.surname = "Garcia";
		dto.mail = "leopoldo@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "leopoldo@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("leopoldo@gmail.com");
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		administrationService.register(requestDTO);
		
		//OBTENEMOS LA solicitud
		List<RequestDTO> requests = administrationService.getPendingRequests();
		
		assertEquals(dto.id, requests.get(requests.size() - 1).idInvestigator);
		assertEquals(StatusRequest.PENDING.name(), requests.get(requests.size() - 1).status);
		
		try {
			administrationService.accept(new Identifier(requests.get(requests.size() - 1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("502", e.getMessage());
		}
	}
	
	@Test
	public void test17RejectRequestERROR_RequestNotPending() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Lorena";
		dto.surname = "Garcia";
		dto.mail = "lorena@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//INICIAMO SESIÓN
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "lorena@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("lorena@gmail.com");
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.idInvestigator = dto.id;
		
		administrationService.register(requestDTO);
		
		//OBTENEMOS LA solicitud
		List<RequestDTO> requests = administrationService.getPendingRequests();
		
		assertEquals(dto.id, requests.get(requests.size() - 1).idInvestigator);
		assertEquals(StatusRequest.PENDING.name(), requests.get(requests.size() - 1).status);
			
		//NOS LOGUEAMOS AHORA COMO ADMINISTRADOR PARA ACEPTAR LA SOLICTUD
		authDTO = new AuthDTO();
		authDTO.mail = "celia@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		administrationService.reject(new Identifier(requests.get(requests.size() - 1).id));
		
		RequestDTO r = administrationService.getDetail(requests.get(requests.size() - 1).id);
		
		assertEquals(dto.id, r.idInvestigator);
		assertEquals(StatusRequest.REJECTED.name(), r.status);
		
		//SE PRUEBA A ACEPTAR LA PETICIÓN RECHAZADA
		try {
			administrationService.accept(new Identifier(requests.get(requests.size() - 1).id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (AdministrationException e) {
			assertEquals("504", e.getMessage());
		}
	}
	
	@Test
	public void test18AcceptRequestERROR_RequestNotFound() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//REGISTRAMOS la solicitud
		RequestDTO requestDTO = new RequestDTO();
		requestDTO.id = ID_NOT_EXIST;
		
		//SE PRUEBA A ACEPTAR LA PETICIÓN
		try {
			administrationService.accept(new Identifier(requestDTO.id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (AdministrationException e) {
			assertEquals("505", e.getMessage());
		}
	}
	
	@Test
	public void test19ConvertInvestigatorAdministrator() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Fredi";
		dto.surname = "Garcia";
		dto.mail = "fredi@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("fredi@gmail.com");
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "celia@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		assertEquals(Role.INVESTIGATOR_EVALUATION.name(), dto.role);
		
		administrationService.convertInvestigatorIntoAdministrator(new Identifier(dto.id));
		
		//OBTENEMOS DE NUEVO LOS DATOS DEL INVESTIGADOR
		dto = investigatorService.getInvestigatorByMail("fredi@gmail.com");
		
		assertEquals(Role.ADMINISTRATOR.name(), dto.role);
	}
	
	@Test
	public void test20ConvertInvestigatorAdministratorERROR_InvestigatorIsAlreadyAdministrator() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Mercuri";
		dto.surname = "Garcia";
		dto.mail = "mercuri@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("mercuri@gmail.com");
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "celia@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		assertEquals(Role.INVESTIGATOR_EVALUATION.name(), dto.role);
		
		administrationService.convertInvestigatorIntoAdministrator(new Identifier(dto.id));
		
		//OBTENEMOS DE NUEVO LOS DATOS DEL INVESTIGADOR
		dto = investigatorService.getInvestigatorByMail("mercuri@gmail.com");
		
		assertEquals(Role.ADMINISTRATOR.name(), dto.role);
		
		//SE VUELVE A CONVERTIR A ADMINISTRADOR
		try {
			administrationService.convertInvestigatorIntoAdministrator(new Identifier(dto.id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (AdministrationException e) {
			assertEquals("506", e.getMessage());
		}		
	}
	
	@Test
	public void test21ConvertInvestigatorAdministratorERROR_InvestigatorNotIsAdministrator() throws InvestigatorException, AttempsException, AdministrationException, ForbiddenException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Graciela";
		dto.surname = "Garcia";
		dto.mail = "graciela@gmail.com";
		dto.password = "123456789";
		
		investigatorService.registerInvestigator(dto);
		
		//OBTENEMOS EL INVESTIGADOR PARA SABER SU ID
		dto = investigatorService.getInvestigatorByMail("graciela@gmail.com");
		
		AuthDTO authDTO = new AuthDTO();
		authDTO.mail = "graciela@gmail.com";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		assertEquals(Role.INVESTIGATOR_EVALUATION.name(), dto.role);
		
		//SE VUELVE A CONVERTIR A ADMINISTRADOR
		try {
			administrationService.convertInvestigatorIntoAdministrator(new Identifier(dto.id));
			Assert.fail("Debe lanzarse excepción.");
		} catch (ForbiddenException e) {
			assertEquals("507", e.getMessage());
		}		
	}
	
}

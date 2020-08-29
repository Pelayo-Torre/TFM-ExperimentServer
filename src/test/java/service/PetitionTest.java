package service;

import static org.junit.jupiter.api.Assertions.*;

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
class PetitionTest {
	
	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private ExperimentService experimentService;
	
	@Autowired 
	private PetitionService petitionService;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;
	
	@Test
	/**
	 * Se prueba la creación de una petición de forma correcta
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws PetitionException
	 */
	public void test10RegisterPetition() throws InvestigatorException, ExperimentException, PetitionException {
		
		//REGISTRAMOS EL INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Pelayo";
		dto.surname = "Garcia Torre";
		dto.username = "pelgarTor";
		dto.mail = "pelayo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//CREAMOS otro investigador
		dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Torre";
		dto.username = "juanele27";
		dto.mail = "juan@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
		ExperimentDTO experientDTO = new ExperimentDTO();
		experientDTO.title = "Experimento en Langreo";
		experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
		experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("pelayo@gmail.com").id;
	
		experimentService.register(experientDTO);
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = 1L;
		petitionDTO.idInvestigator = investigatorService.getInvestigatorByMail("juan@gmail.com").id;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		//COMPROBAMOS QUE LA PETICIÓN SE HA ALMACENADO CORRECTAMENTE
		petitionDTO = petitionService.getDetail(2L);
		
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
	public void test11RegisterPetitionERROR100() throws InvestigatorException, ExperimentException, PetitionException{
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = ID_NOT_EXIST;
		petitionDTO.idInvestigator = investigatorService.getInvestigatorByMail("juan@gmail.com").id;
		petitionDTO.manager = true;
		
		try {
			petitionService.register(petitionDTO);
			Assert.fail("El experimento no existe");
		} catch (ExperimentException e) {
			Assert.assertEquals("100", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a enviar una petición de un investigador que no existe en el sistema
	 * @throws InvestigatorException el investigador no existe
	 * @throws ExperimentException
	 * @throws PetitionException
	 */
	public void test12RegisterPetitionERROR200() throws InvestigatorException, ExperimentException, PetitionException{
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = 1L;
		petitionDTO.idInvestigator = ID_NOT_EXIST;
		petitionDTO.manager = true;
		
		try {
			petitionService.register(petitionDTO);
			Assert.fail("El investigador no existe");
		} catch (InvestigatorException e) {
			Assert.assertEquals("200", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a enviar una petición de un investigador que ya tienen una petición sobre el experimento
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws PetitionException, ya existe una petición
	 */
	public void test13RegisterPetitionERROR304() throws InvestigatorException, ExperimentException, PetitionException{
		
		//CREAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = 1L;
		petitionDTO.idInvestigator = investigatorService.getInvestigatorByMail("juan@gmail.com").id;;
		petitionDTO.manager = true;
		
		try {
			petitionService.register(petitionDTO);
			Assert.fail("Ya hay una petición pendiente entre el investigador y el experimento");
		} catch (PetitionException e) {
			Assert.assertEquals("304", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba el detalle de una petición
	 * @throws PetitionException
	 */
	public void test14DetailPetition() throws PetitionException{
		
		PetitionDTO dto = petitionService.getDetail(2L);
		
		assertNotNull(dto);
		assertEquals(false, dto.creator);
		assertEquals(true, dto.manager);
		assertEquals(StatusPetition.PENDING.name(), dto.statusPetition);
	}
	
	@Test
	/**
	 * Se prueba el detalle de una petición que no se encuentra registrada en el sistema
	 * @throws PetitionException la petición no está registrada en el sistema
	 */
	public void test15DetailPetitionERROR300() throws PetitionException {
		
		try {
			petitionService.getDetail(ID_NOT_EXIST);
			Assert.fail("La petición no se encuentra registrada en el sistema");
		} catch (PetitionException e) {
			Assert.assertEquals("300", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a cancelar una petición en estado PENDING
	 * @throws PetitionException No se puede Cancelar una petición en estado PENDING
	 */
	public void test16CancelPetitionERROR303() throws PetitionException{
		
		try {
			Identifier identifier = new Identifier(2L);
			petitionService.cancel(identifier);
			Assert.fail("No se puede Cancelar una petición en estado PENDING");
		} catch (PetitionException e) {
			Assert.assertEquals("303", e.getMessage());
		}
	}

	@Test
	/**
	 * Se prueba aceptar una petición que se encuentra en estado PENDING
	 * @throws PetitionException
	 */
	public void test17AcceptPetition() throws PetitionException {
		
		PetitionDTO dto = petitionService.getDetail(2L);
		assertEquals(StatusPetition.PENDING.name(), dto.statusPetition);
		
		//ACEPTAMOS LA PETICIÓN
		Identifier identifier = new Identifier(2L);
		petitionService.accept(identifier);
		
		dto = petitionService.getDetail(2L);
		assertEquals(StatusPetition.ACCEPTED.name(), dto.statusPetition);
	}
	
	@Test
	/**
	 * Se prueba a rechazar una petición que se encuentra en estado ACCEPTED
	 * @throws PetitionException No se puede rechazar una petición en estado ACCEPTED
	 */
	public void test18RejectPetitionERROR302() throws PetitionException{
		
		try {
			Identifier identifier = new Identifier(2L);
			petitionService.reject(identifier);
			Assert.fail("No se puede rechazar una petición en estado ACCEPTED");
		} catch (PetitionException e) {
			Assert.assertEquals("302", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a cancelar una petición que se encuentra en estado ACCEPTED
	 * @throws PetitionException
	 */
	public void test19CancelPetition() throws PetitionException{
		
		PetitionDTO dto = petitionService.getDetail(2L);
		assertEquals(StatusPetition.ACCEPTED.name(), dto.statusPetition);
		
		//CANCELAMOS LA PETICIÓN
		Identifier identifier = new Identifier(2L);
		petitionService.cancel(identifier);
		
		dto = petitionService.getDetail(2L);
		assertEquals(StatusPetition.CANCELLED.name(), dto.statusPetition);
	}
	
	@Test
	/**
	 * Se prueba a rechazar una petición
	 * @throws PetitionException
	 */
	public void test20RejectPetition() throws PetitionException, InvestigatorException, ExperimentException {
		
		//COMENZAMOS CREANDO UN NUEVO INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Gonzalo";
		dto.surname = "Torre";
		dto.username = "gongartor";
		dto.mail = "gonzalo@gmail.com";
		dto.password = "123456789";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//REGISTRAMOS LA PETICIÓN
		PetitionDTO petitionDTO = new PetitionDTO();
		petitionDTO.idExperiment = 1L;
		petitionDTO.idInvestigator = investigatorService.getInvestigatorByMail("gonzalo@gmail.com").id;
		petitionDTO.manager = true;
		
		//LA GUARDAMOS EN BASE DE DATOS
		petitionService.register(petitionDTO);
		
		petitionDTO = petitionService.getDetail(3L);
		assertEquals(StatusPetition.PENDING.name(), petitionDTO.statusPetition);
		
		//EL INVESTIGADOR RECHAZA LA PETICIÓN
		Identifier identifier = new Identifier(3L);
		petitionService.reject(identifier);
		
		petitionDTO = petitionService.getDetail(3L);
		assertEquals(StatusPetition.REJECTED.name(), petitionDTO.statusPetition);
	}
}

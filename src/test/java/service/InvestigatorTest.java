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
		dto.email = "pelayo@gmail.com";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//COMPROBAMOS QUE SE HA ALMACENADO CORRECTAMENTE
		InvestigatorDTO investigator = investigatorService.getInvestigatorByMail("pelayo@gmail.com");
		
		assertNotNull(investigator);
		assertEquals("Pelayo", investigator.name);
		assertEquals("Garcia Torre", investigator.surname);
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
		dto.surname = "Garcia Torre";
		dto.email = "pelayo@gmail.com";
		
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
		dto.email = "pelayo@gmail.com";
		
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
		dto.email = null;
		
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
		dto.email = "pel@gmail.com";
		
		investigatorService.registerInvestigator(dto);
		
		//REGISTRAMOS UN NUEVO INVESTIGADOR CON MISMO EMAIL
		InvestigatorDTO dto2 = new InvestigatorDTO();
		dto2.name = "Pelayo";
		dto2.surname = "Gonzalez";
		dto2.email = "pel@gmail.com";
		
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
	 * Se prueba el detalle de un investigador que se encuentra registrado
	 * @throws InvestigatorException
	 */
	public void test15getDetail() throws InvestigatorException{
		
		InvestigatorDTO investigator = investigatorService.getInvestigatorByMail("pelayo@gmail.com");
		
		//SE OBTIENE UN INVESTIGADOR CUYO IDENTIFICADOR SE ENCUENTRA REGISTRADO
		investigator = investigatorService.getDetail(investigator.id);
		
		assertNotNull(investigator);
		assertEquals("Pelayo", investigator.name);
		assertEquals("Garcia Torre", investigator.surname);
		assertEquals("pelayo@gmail.com", investigator.email);
	}
	
	@Test
	/**
	 * Se prueba el detalle de un investigador que no se encuentra registrado
	 * @throws InvestigatorException ya que el investigador no se encuentra registrado
	 */
	public void test16getDetailERROR200() throws InvestigatorException{
		
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
	public void test17updateInvestigator() throws InvestigatorException {
		
		//CREAMOS el investigador
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Torre";
		dto.email = "juan@gmail.com";
		
		//LO GUARDAMOS EN BASE DE DATOS
		investigatorService.registerInvestigator(dto);
		
		//LO EDITAMOS
		dto = new InvestigatorDTO();
		dto.name = "Juan Antonio";
		dto.surname = "Llaneza";
		dto.email = "juanantonio@gmail.com";
		dto.id = 3L;
		
		investigatorService.updateInvestigator(dto);
				
		//COMPROBAMOS QUE SE HA GUARDADO CORRECTAMENTE
		dto = investigatorService.getInvestigatorByMail("juanantonio@gmail.com");
		
		assertNotNull(dto);
		assertEquals("Juan Antonio", dto.name);
		assertEquals("Llaneza", dto.surname);
		assertEquals("juanantonio@gmail.com", dto.email);
	}
	
	@Test
	/**
	 * Edición de un investigador con error 201 (El nombre es obligatorio)
	 * @throws InvestigatorException, nombre obligatorio
	 */
	public void test18UpdateInvestigatorERROR201() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = null;
		dto.surname = "Llaneza";
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
	public void test19UpdateInvestigatorERROR202() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "";
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
	public void test20UpdateInvestigatorERROR203() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
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
	public void test21UpdateInvestigatorERROR204() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
		dto.email = "pelayo@gmail.com";
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
	 * Edición de un investigador con error 200 (No existe en base de datos)
	 * @throws InvestigatorException, investigador no registrado
	 */
	public void test22UpdateInvestigatorERROR200() throws InvestigatorException {
		
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Juan";
		dto.surname = "Llaneza";
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
	public void test23getPetitionsPending() throws InvestigatorException, ExperimentException, PetitionException {
		
		//REGISTRAMOS UN INVESTIGADOR
		InvestigatorDTO dto = new InvestigatorDTO();
		dto.name = "Carlos";
		dto.surname = "Garcia";
		dto.email = "carlos@gmail.com";
		
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
	public void test24getPetitionsAccepted() throws PetitionException, InvestigatorException {
		
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
	 * Se comprueba que devuelve todos los investigadores de la aplicación
	 * @throws InvestigatorException
	 */
	public void test25getAllInvestigators() throws InvestigatorException{
		
		//Se comprueba el número de investigadores existentes
		List<InvestigatorDTO> investigators = investigatorService.getListInvestigators();
		
		assertNotNull(investigators);
		assertEquals(4, investigators.size());
	}
	
}

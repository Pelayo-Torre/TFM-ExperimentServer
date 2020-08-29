package service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
import com.uniovi.es.business.binnacle.BinnacleService;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.NoteException;
import com.uniovi.es.utils.Identifier;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ExperimentServerApplication.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestMethodOrder(Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class BinnacleTest {
	
	@Autowired
	private BinnacleService binnacleService;
	
	@Autowired
	private InvestigatorService investigatorService;
	
	@Autowired
	private ExperimentService experimentService;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;

	@Test
	/**
	 * Prueba el registro de una nota en la bitácora de un experimento de manera correcta
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws NoteException
	 */
	void test10RegisterNote() throws InvestigatorException, ExperimentException, NoteException {
		
		//COMENZAMOS CREANDO UN INVESTIGADOR Y UN EXPERIMENTO
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
		
		experimentService.register(experientDTO);
		
		//REGISTRAMOS LA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = 1L;
		
		binnacleService.registerNote(noteDTO);
		
		//COMPROBAMOS QUE LA NOTA SE HA CREADO CORRECTAMENTE
		
		noteDTO = binnacleService.detail(1L);
		
		assertNotNull(noteDTO);
		assertEquals("Creación de experimento", noteDTO.title);
		assertEquals("Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años", noteDTO.description);
	}
	
	@Test
	/**
	 * Se prueba a añadir una nota a un experimento que no se encuentra registrado en el sistema
	 * @throws ExperimentException experimento no registrado en el sistema
	 * @throws NoteException
	 */
	public void test11RegisterNoteERROR100() throws ExperimentException, NoteException{
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = ID_NOT_EXIST;
				
		try {
			binnacleService.registerNote(noteDTO);
			Assert.fail("Se debe lanzar error por experimento no registrado en el sistema");
		} catch (ExperimentException e) {
			Assert.assertEquals("100", e.getMessage());
		}
	}

	@Test
	/**
	 * Se prueba a registrar una nota sin título
	 * @throws ExperimentException
	 * @throws NoteException el título es obligatorio
	 */
	public void test12RegisterNoteERROR401() throws ExperimentException, NoteException{
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = null;
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = 1L;
				
		try {
			binnacleService.registerNote(noteDTO);
			Assert.fail("El titulo es un campo obligatorio");
		} catch (NoteException e) {
			Assert.assertEquals("401", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a registrar una nota sin descripción
	 * @throws ExperimentException
	 * @throws NoteException la descripción es obligatoria
	 */
	public void test13RegisterNoteERROR402() throws ExperimentException, NoteException{
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "";
		noteDTO.idExperiment = 1L;
				
		try {
			binnacleService.registerNote(noteDTO);
			Assert.fail("La descripción es un campo obligatorio");
		} catch (NoteException e) {
			Assert.assertEquals("402", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de los datos de una nota de una bitácora
	 * @throws NoteException
	 */
	public void test14UpdateNote() throws NoteException {
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento para menores";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años";
		noteDTO.id = 1L;
				
		binnacleService.updateNote(noteDTO);
		
		noteDTO = binnacleService.detail(1L);
		
		assertNotNull(noteDTO);
		assertEquals("Creación de experimento para menores", noteDTO.title);
		assertEquals("Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años", noteDTO.description);
	}
	
	@Test
	/**
	 * Prueba la edición de una nota que no existe en el sistema
	 * @throws NoteException La nota especificada no se encuentra registrada en el sistema
	 */
	public void test15UpdateNoteERROR400() throws NoteException {
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento para menores";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años";
		noteDTO.id = ID_NOT_EXIST;
				
		try {
			binnacleService.updateNote(noteDTO);
			Assert.fail("La nota especificada no se encuentra registrada en el sistema");
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
		
	}
	
	@Test
	/**
	 * Prueba la edición de una nota que no tiene título
	 * @throws NoteException La nota especificada no tiene título
	 */
	public void test16UpdateNoteERROR401() throws NoteException {
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = null;
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años";
		noteDTO.id = 1L;
				
		try {
			binnacleService.updateNote(noteDTO);
			Assert.fail("La nota especificada no tiene título");
		} catch (NoteException e) {
			assertEquals("401", e.getMessage());
		}
		
	}
	
	@Test
	/**
	 * Prueba la edición de una nota que no tiene descripción
	 * @throws NoteException La nota especificada no tiene descripción
	 */
	public void test17UpdateNoteERROR402() throws NoteException {
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento para menores";
		noteDTO.description = "";
		noteDTO.id = 1L;
				
		try {
			binnacleService.updateNote(noteDTO);
			Assert.fail("La nota especificada no tiene descripción");
		} catch (NoteException e) {
			assertEquals("402", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Prueba el obtener las notas de un experimento
	 * @throws NoteException
	 * @throws ExperimentException
	 */
	public void test18NotesByExperiment() throws NoteException, ExperimentException{
		
		//OBTENEMOS LAS NOTAS DE UN EXPERIMENTO
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(1L);
		
		assertNotNull(notes);
		assertEquals(1, notes.size());
		assertEquals("Creación de experimento para menores", notes.get(0).title);
		assertEquals("Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años", notes.get(0).description);
	
	}
	
	@Test
	/**
	 * Prueba el obtener las notas de un experimento que no se encuentra registrado en el sistema
	 * @throws NoteException
	 * @throws ExperimentException El experimento no existe
	 */
	public void test19NotesByExperiment() throws ExperimentException{
		
		//OBTENEMOS LAS NOTAS DE UN EXPERIMENTO
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(ID_NOT_EXIST);
		
		assertNotNull(notes);
		assertEquals(0, notes.size());
	}
	
	@Test
	/**
	 * Prueba el obtener el detalle de una nota
	 * @throws NoteException
	 */
	public void test20DetailNote() throws NoteException{
		
		//OBTENEMOS LAS NOTAS DE UN EXPERIMENTO
		NoteDTO dto = binnacleService.detail(1L);
		
		assertNotNull(dto);
		assertEquals("Creación de experimento para menores", dto.title);
		assertEquals("Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años", dto.description);
	
	}
	
	@Test
	/**
	 * Prueba el obtener el detalle de una nota que no se encuentra registrada en el sistema
	 * @throws NoteException la nota no existe
	 */
	public void test21DetailNoteERROR400() throws NoteException{
		
		//OBTENEMOS LAS NOTAS DE UN EXPERIMENTO
		try {
			binnacleService.detail(ID_NOT_EXIST);
			Assert.fail("El experimento no existe");
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la eliminación de una nota de un experimento
	 * @throws NoteException
	 */
	public void test22DeleteNote() throws NoteException{
		
		//BORRAMOS LA NOTA CREADA
		Identifier identifier = new Identifier(1L);
		binnacleService.deleteNote(identifier);
		
		//COMPROBAMOS QUE SE HA BORRADO
		try {
			binnacleService.detail(1L);
			Assert.fail("El experimento no existe");
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a eliminar una nota que no existe en el sistema
	 * @throws NoteException la nota no eiste en el sistema
	 */
	public void test23DeleteNoteERROR400() throws NoteException {
		
		try {
			Identifier identifier = new Identifier(ID_NOT_EXIST);
			binnacleService.deleteNote(identifier);
			Assert.fail("El experimento no existe");
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
		
	}
	
}

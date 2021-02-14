package service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

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
import com.uniovi.es.business.binnacle.BinnacleService;
import com.uniovi.es.business.dto.AuthDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.business.experiment.ExperimentService;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.AttempsException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.NoteException;
import com.uniovi.es.model.types.Device;
import com.uniovi.es.model.types.Gender;
import com.uniovi.es.model.types.Laterality;
import com.uniovi.es.persistence.DeviceDAO;
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
	
	@Autowired
	private DeviceDAO deviceDAO;
	
	@Autowired
	private AuthenticationService authenticateUser;
	
	private static boolean primeraVez = true;
	
	private static final Long ID_NOT_EXIST = 4345245786396523496L;
	
	@PostConstruct
	public void init() throws InvestigatorException, AttempsException, ExperimentException, ForbiddenException {
		if(primeraVez) {
			//COMENZAMOS CREANDO UN INVESTIGADOR Y UN EXPERIMENTO
			InvestigatorDTO dto = new InvestigatorDTO();
			dto.name = "Ramiro";
			dto.surname = "Garcia";
			dto.username = "ramiro123";
			dto.mail = "ramiro@gmail.com";
			dto.password = "123456789";
			
			investigatorService.registerInvestigator(dto);
			
			//CREAMOS OTRO INVESTIGADOR
			dto = new InvestigatorDTO();
			dto.name = "Angela";
			dto.surname = "Garcia";
			dto.username = "angela123";
			dto.mail = "angela@gmail.com";
			dto.password = "123456789";
			
			investigatorService.registerInvestigator(dto);
			
			//INICIAMOS SESIÓN
			AuthDTO authDTO = new AuthDTO();
			authDTO.username = "ramiro123";
			authDTO.password = "123456789";
			authenticateUser.authenticateUser(authDTO);
			
			Device d = new Device("MOUSE");
			Device d1 = new Device("TOUCHPAD");
			
			deviceDAO.save(d);
			deviceDAO.save(d1);
			
			//REGISTRAMOS UN EXPERIMENTO ASOCIADO AL INVESTIGDOR ANTERIOR
			ExperimentDTO experientDTO = new ExperimentDTO();
			experientDTO.title = "Experimento en Langreo";
			experientDTO.description = "Prueba en ordenadores con niños de 12 a 16 años";
			experientDTO.idInvestigator = investigatorService.getInvestigatorByMail("ramiro@gmail.com").id;
			
			experientDTO.birthDate = new Date();
			experientDTO.gender = Gender.MALE.name();
			experientDTO.laterality = Laterality.LEFT_HANDED.name();
			experientDTO.idDevice = 1L;
			
			experimentService.register(experientDTO);
			
			primeraVez = false;
		}		
	}

	@Test
	/**
	 * Prueba el registro de una nota en la bitácora de un experimento de manera correcta
	 * @throws InvestigatorException
	 * @throws ExperimentException
	 * @throws NoteException
	 */
	void test10RegisterNote() throws InvestigatorException, ExperimentException, NoteException, ForbiddenException {
				
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//REGISTRAMOS LA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		
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
	public void test11RegisterNoteERROR100() throws ExperimentException, NoteException, ForbiddenException{
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = ID_NOT_EXIST;
				
		try {
			binnacleService.registerNote(noteDTO);
		} catch (ExperimentException e) {
			assertEquals("100", e.getMessage());
		}
	}

	@Test
	/**
	 * Se prueba a registrar una nota sin título
	 * @throws ExperimentException
	 * @throws NoteException el título es obligatorio
	 */
	public void test12RegisterNoteERROR401() throws ExperimentException, NoteException, ForbiddenException{
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = null;
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = experiments.get(experiments.size() -1).id;
				
		try {
			binnacleService.registerNote(noteDTO);
		} catch (NoteException e) {
			assertEquals("401", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a registrar una nota sin descripción
	 * @throws ExperimentException
	 * @throws NoteException la descripción es obligatoria
	 */
	public void test13RegisterNoteERROR402() throws ExperimentException, NoteException, ForbiddenException{
		List<ExperimentDTO> experiments = experimentService.getExperiments();

		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "";
		noteDTO.idExperiment = experiments.get(experiments.size() -1).id;
				
		try {
			binnacleService.registerNote(noteDTO);
		} catch (NoteException e) {
			assertEquals("402", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la edición de los datos de una nota de una bitácora
	 * @throws NoteException
	 */
	public void test14UpdateNote() throws NoteException, ExperimentException, ForbiddenException {
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento para menores";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años";
		noteDTO.id = notes.get(notes.size() - 1).id;
				
		binnacleService.updateNote(noteDTO);
		
		noteDTO = binnacleService.detail(notes.get(notes.size() - 1).id);
		
		assertNotNull(noteDTO);
		assertEquals("Creación de experimento para menores", noteDTO.title);
		assertEquals("Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años", noteDTO.description);
	}
	
	@Test
	/**
	 * Prueba la edición de una nota que no existe en el sistema
	 * @throws NoteException La nota especificada no se encuentra registrada en el sistema
	 */
	public void test15UpdateNoteERROR400() throws NoteException, ForbiddenException {
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento para menores";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años";
		noteDTO.id = ID_NOT_EXIST;
				
		try {
			binnacleService.updateNote(noteDTO);
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
		
	}
	
	@Test
	/**
	 * Prueba la edición de una nota que no tiene título
	 * @throws NoteException La nota especificada no tiene título
	 */
	public void test16UpdateNoteERROR401() throws NoteException, ExperimentException, ForbiddenException {
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = null;
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años";
		noteDTO.id = notes.get(notes.size() - 1).id;
				
		try {
			binnacleService.updateNote(noteDTO);
		} catch (NoteException e) {
			assertEquals("401", e.getMessage());
		}
		
	}
	
	@Test
	/**
	 * Prueba la edición de una nota que no tiene descripción
	 * @throws NoteException La nota especificada no tiene descripción
	 */
	public void test17UpdateNoteERROR402() throws NoteException, ExperimentException, ForbiddenException {
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento para menores";
		noteDTO.description = "";
		noteDTO.id = notes.get(notes.size() - 1).id;
				
		try {
			binnacleService.updateNote(noteDTO);
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
	public void test18NotesByExperiment() throws NoteException, ExperimentException, ForbiddenException{
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		
		//OBTENEMOS LAS NOTAS DE UN EXPERIMENTO
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		assertNotNull(notes);
		assertEquals(1, notes.size());
		assertEquals("Creación de experimento para menores", notes.get(notes.size() - 1).title);
		assertEquals("Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años", notes.get(notes.size()-1).description);
	
	}
	
	@Test
	/**
	 * Prueba el obtener el detalle de una nota
	 * @throws NoteException
	 */
	public void test20DetailNote() throws NoteException, ExperimentException, ForbiddenException{
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//OBTENEMOS LAS NOTAS DE UN EXPERIMENTO
		NoteDTO dto = binnacleService.detail(notes.get(notes.size()-1).id);
		
		assertNotNull(dto);
		assertEquals("Creación de experimento para menores", dto.title);
		assertEquals("Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años", dto.description);
	
	}
	
	@Test
	/**
	 * Prueba el obtener el detalle de una nota que no se encuentra registrada en el sistema
	 * @throws NoteException la nota no existe
	 */
	public void test21DetailNoteERROR400() throws NoteException, ForbiddenException{
		
		//OBTENEMOS LAS NOTAS DE UN EXPERIMENTO
		try {
			binnacleService.detail(ID_NOT_EXIST);
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba la eliminación de una nota de un experimento
	 * @throws NoteException
	 */
	public void test22DeleteNote() throws NoteException, ExperimentException, ForbiddenException{
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//BORRAMOS LA NOTA CREADA
		Identifier identifier = new Identifier(notes.get(notes.size()-1).id);
		binnacleService.deleteNote(identifier);
		
		//COMPROBAMOS QUE SE HA BORRADO
		try {
			binnacleService.detail(notes.get(notes.size()-1).id);
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
		
		//REGISTRAMOS LA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = experiments.get(experiments.size() - 1).id;
		
		binnacleService.registerNote(noteDTO);
	}
	
	@Test
	/**
	 * Se prueba a eliminar una nota que no existe en el sistema
	 * @throws NoteException la nota no eiste en el sistema
	 */
	public void test23DeleteNoteERROR400() throws NoteException, ForbiddenException {
		
		try {
			Identifier identifier = new Identifier(ID_NOT_EXIST);
			binnacleService.deleteNote(identifier);
		} catch (NoteException e) {
			assertEquals("400", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a eliminar una nota por un investigador que no está asociado al experimento
	 * @throws NoteException
	 */
	public void test23DeleteNoteByInvestigatorNotAssociated() throws NoteException, ExperimentException, AttempsException, ForbiddenException{
		
		//INICIAMOS SESIÓN como investigador asociado
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "ramiro123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();		
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//INICIAMOS SESIÓN como investigador no asociado
		authDTO = new AuthDTO();
		authDTO.username = "angela123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//BORRAMOS LA NOTA CREADA
		Identifier identifier = new Identifier(notes.get(notes.size()-1).id);
		
		//COMPROBAMOS QUE SE HA BORRADO
		try {
			binnacleService.deleteNote(identifier);
		} catch (ForbiddenException e) {
			assertEquals("403", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a acceder al detalle de una nota por un investigador que no está asociado al experimento
	 * @throws NoteException
	 */
	public void test24DetailNoteByInvestigatorNotAssociated() throws NoteException, ExperimentException, AttempsException, ForbiddenException{
		
		//INICIAMOS SESIÓN como investigador asociado
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "ramiro123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();		
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//INICIAMOS SESIÓN como investigador no asociado
		authDTO = new AuthDTO();
		authDTO.username = "angela123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
	
		try {
			binnacleService.detail(notes.get(notes.size()-1).id);
		} catch (ForbiddenException e) {
			assertEquals("403", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a registrar una nota por un investigador que no está asociado al experimento
	 * @throws NoteException
	 */
	public void test25RegisterNoteByInvestigatorNotAssociated() throws NoteException, ExperimentException, AttempsException{
		
		//INICIAMOS SESIÓN como investigador asociado
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "ramiro123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();		
		
		//INICIAMOS SESIÓN como investigador no asociado
		authDTO = new AuthDTO();
		authDTO.username = "angela123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		//REGISTRAMOS LA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre personas entre 12 y 16 años";
		noteDTO.idExperiment = experiments.get(experiments.size() - 1).id;
	
		try {
			binnacleService.registerNote(noteDTO);
		} catch (ForbiddenException e) {
			assertEquals("403", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a actualizar los datos una nota por un investigador que no está asociado al experimento
	 * @throws NoteException
	 */
	public void test26UpdateNoteByInvestigatorNotAssociated() throws NoteException, ExperimentException, AttempsException, ForbiddenException{
		
		//INICIAMOS SESIÓN como investigador asociado
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "ramiro123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();	
		
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		
		//CREAMOS UNA NOTA
		NoteDTO noteDTO = new NoteDTO();
		noteDTO.title = "Creación de experimento para menores";
		noteDTO.description = "Se ha creado el experimento para hacer pruebas sobre menores entre 12 y 16 años";
		noteDTO.id = notes.get(notes.size() - 1).id;
		
		//INICIAMOS SESIÓN como investigador no asociado
		authDTO = new AuthDTO();
		authDTO.username = "angela123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
	
		try {
			binnacleService.updateNote(noteDTO);
		} catch (ForbiddenException e) {
			assertEquals("403", e.getMessage());
		}
	}
	
	@Test
	/**
	 * Se prueba a obtener todas las notas de un experiento por un investigador que no está asociado al experimento
	 * @throws NoteException
	 */
	public void test27GetAllNotesByInvestigatorNotAssociated() throws NoteException, ExperimentException, AttempsException{
		
		//INICIAMOS SESIÓN como investigador asociado
		AuthDTO authDTO = new AuthDTO();
		authDTO.username = "ramiro123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		List<ExperimentDTO> experiments = experimentService.getExperiments();		
		
		//INICIAMOS SESIÓN como investigador no asociado
		authDTO = new AuthDTO();
		authDTO.username = "angela123";
		authDTO.password = "123456789";
		authenticateUser.authenticateUser(authDTO);
		
		try {
			binnacleService.getNotesByExperiment(experiments.get(experiments.size() - 1).id);
		} catch (ForbiddenException e) {
			assertEquals("403", e.getMessage());
		}
	}
}

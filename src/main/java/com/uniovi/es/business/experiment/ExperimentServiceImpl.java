package com.uniovi.es.business.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uniovi.es.business.authentication.UserInSession;
import com.uniovi.es.business.binnacle.BinnacleService;
import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.DemographicDataTypeDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.experiment.commands.Close;
import com.uniovi.es.business.experiment.commands.Delete;
import com.uniovi.es.business.experiment.commands.Open;
import com.uniovi.es.business.experiment.commands.ReOpen;
import com.uniovi.es.business.user.UserDataService;
import com.uniovi.es.business.validators.ExperimentValidator;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.NoteException;
import com.uniovi.es.model.DemographicData;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.User;
import com.uniovi.es.model.types.StatusPetition;
import com.uniovi.es.model.types.DemographicDataType;
import com.uniovi.es.persistence.DemographicDataDAO;
import com.uniovi.es.persistence.ExperimentDAO;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.persistence.UserDAO;
import com.uniovi.es.utils.Identifier;

@Service
public class ExperimentServiceImpl implements ExperimentService{

	private static final Logger logger = LoggerFactory.getLogger(ExperimentServiceImpl.class);
	
	private ActionManager actionManager = new ActionManager();
	
	@Autowired
	private ExperimentValidator experimentValidator;
	
	@Autowired
	private ExperimentDAO experimentDAO;
	
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private DemographicDataDAO demographicDataDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UserDataService userDataService;
	
	@Autowired
	private BinnacleService binnacleService;
	
	@Autowired
	private UserInSession userInSession;
			
	@Override
	public void register(ExperimentDTO dto) throws ExperimentException, InvestigatorException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- register experiment");
		
		//Se comprueba que el id del investigador que nos llega del cliente es el que se encuentra en sesión
		Investigator investigatorInSession = userInSession.getInvestigator();
		if(investigatorInSession == null || investigatorInSession.getId() != dto.idInvestigator) {
			logger.error("[ERROR - 115] -- Los identificadores del investigador del cliente con el de sesión del servidor son distintos");
			throw new ForbiddenException("115");
		}
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + dto.idInvestigator);
		Optional<Investigator> optional = investigatorDAO.findById(dto.idInvestigator);
		Investigator investigator = getInvestigator(optional);
		
		experimentValidator.validate(dto);
		
		Experiment experiment = new Experiment();
		DtoAssembler.fillData(experiment, dto);
				
		//Creación de la petición
		Petition petition = new Petition(investigator, experiment);
		petition.setAnswerDate(new Date());
		petition.setCreator(true);
		petition.setManager(true);
		petition.accept();
		
		logger.info("\t \t Registrando el experimento en base de datos");
		experimentDAO.save(experiment);
		
		//Creación de los datos demográficos
		if(dto.demographicData != null && dto.demographicData.size() != 0) {
			logger.info("\t \t Creación de los datos demográficos del experimento. TOTAL: " + dto.demographicData.size());
			for(DemographicDataDTO dd : dto.demographicData) {
				demographicDataDAO.save(new DemographicData(dd.name, DemographicDataType.valueOf(dd.type), experiment));
			}
		}
		
		investigatorDAO.save(investigator);
		
		logger.info("\t \t Registrando la petitición en base de datos");
		petitionDAO.save(petition);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- register experiment");
	}
	
	@Override
	@Transactional
	public void update(ExperimentDTO dto) throws ExperimentException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- update experiment");
		
		isManager(dto.id);

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + dto.id);
		Optional<Experiment> optional = experimentDAO.findById(dto.id);
		Experiment experiment = getExperiment(optional);
		
		experimentValidator.validate(dto);		
		DtoAssembler.fillData(experiment, dto);
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		//Se actualizan los datos demográficos del experimento si el experimento está en estado creado
		if(experiment.isCreated()) {
			logger.info("\t \t Se borran los datos previamente asociados.");
			demographicDataDAO.deleteDemographicDataByExperiment(experiment.getId());
			if(dto.demographicData != null && dto.demographicData.size() != 0) {
				logger.info("\t \t Creación de los datos demográficos del experimento. TOTAL: " + dto.demographicData.size());
				for(DemographicDataDTO dd : dto.demographicData) {
					demographicDataDAO.save(new DemographicData(dd.name, DemographicDataType.valueOf(dd.type), experiment));
				}
			}
		}
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- update experiment");
	}
	
	@Override
	public ExperimentDTO getDetail(Long id) throws ExperimentException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- detail experiment");

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id);
		Experiment experiment = getExperiment(optional);
		
		Investigator investigatorInSessionRol = userInSession.getInvestigator();
		Petition petition = petitionDAO.isInvestigatorAssociatedExperiment(investigatorInSessionRol.getId(), id);
		
		if(petition == null) {
			logger.error("[ERROR -- 117] - Los datos del experimento solo pueden ser visualizados por investigadores asociados al experimento.");
			throw new ForbiddenException("117");
		}
		
		logger.info("\t \t Obteniendo los datos del creador del experimento con ID: " + id);
		Investigator investigator = investigatorDAO.findCreatorOfExperiment(id);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- detail experiment");
		
		return DtoAssembler.toDTO(experiment, investigator, petition.getManager());
	}
	
	@Override
	public void open(Identifier id) throws ExperimentException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- open experiment");
		
		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}
		
		isManager(id.getId());

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ABIERTO");
		actionManager.execute(new Open(), experiment);
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("\t \t Se registra una nota de cambio de estado");
		registerNote(experiment.getId(), "Apertura de experimento", "Se produce la apertura del experimento.");
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- open experiment");
	}


	@Override
	public void reOpen(Identifier id) throws ExperimentException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- reOpen experiment");
		
		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}
		
		isManager(id.getId());

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ABIERTO");
		actionManager.execute(new ReOpen(), experiment);
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("\t \t Se registra una nota de cambio de estado");
		registerNote(experiment.getId(), "Reapertura de experimento", "Se produce la reapertura del experimento.");
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- reOpen experiment");
	}

	@Override
	public void close(Identifier id) throws ExperimentException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- close experiment");
		
		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}
		
		isManager(id.getId());

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a CERRADO");
		actionManager.execute(new Close(), experiment);
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		//Se calculan los datos de los usuarios a partir de su IP
		List<User> users = userDAO.getUsersNotProcessed(id.getId());
		if(users != null && users.size() > 0) {
			logger.info("\t \t Procesamiento de datos a partir de IP de " + users.size() + " usuairos");
			userDataService.calculateIp(users);
		}
		else {
			logger.info("\t \t No existen usuarios para procesar sus datos");
		}
		
		logger.info("\t \t Se registra una nota de cambio de estado");
		registerNote(experiment.getId(), "Cierre de experimento", "Se produce el cierre del experimento.");
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- close experiment");
	}

	@Override
	public void delete(Identifier id) throws ExperimentException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- delete experiment");

		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}
		
		isManager(id.getId());
		
		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ELIMINADO");
		actionManager.execute(new Delete(), experiment);
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- delete experiment");
	}
	
	@Override
	public List<InvestigatorDTO> getInvestigatorsOfExperiment(Long id) throws ExperimentException, ForbiddenException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- getInvestigatorsOfExperiment");
		
		if(petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), id) == null) {
			logger.error("[ERROR -- 117] - Los datos del experimento solo pueden ser visualizados por investigadores asociados al experimento.");
			throw new ForbiddenException("117");
		}
		
		List<Petition> list = petitionDAO.findByIdExperiment(id, StatusPetition.ACCEPTED);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- getInvestigatorsOfExperiment");
		return DtoAssembler.toListInvestigatorsOfPetitions(list);
	}
	
	/**
	 * Comprueba que el investigador que se encuentra en sesión tiene permisos para administrar el experimento
	 * @param idExperiment identificador del experimento que se quiere pasar como parámetro.
	 * @throws ExperimentException 
	 */
	private void isManager(Long idExperiment) throws ExperimentException, ForbiddenException {
		
		logger.info("Se comprueba si el investigador es gestor del experimento");
		
		Investigator investigator = userInSession.getInvestigator();
		if(investigator == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores gestores");
			throw new ForbiddenException("116");
		}
		
		//Comprobamos si el investigador es gestor del experimento
		Petition p = petitionDAO.isManager(idExperiment, investigator.getId());
		
		if(p == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores gestores");
			throw new ForbiddenException("116");
		}
		
	}
	
	@Override
	public List<ExperimentDTO> getExperiments() {
		logger.info("[INICIO] EXPERIMENT SERVICE -- all Experiments");
		
		List<Experiment> list = new ArrayList<Experiment>();
		experimentDAO.findAll().forEach(list::add);
				
		logger.info("[FINAL] EXPERIMENT SERVICE -- all Experiments");
		return DtoAssembler.toList(list);
	}
	
	@Override
	public List<InvestigatorDTO> getInvestigatorsNotAssociatedAnExperiment(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- investigators not associated an experiment");
		
		List<Investigator> list = experimentDAO.findInvestigatorsNotAssociatedAnExperiment(id);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- investigators not associated an experiment");
		return DtoAssembler.toListInvestigators(list);
	}
	
	@Override
	public List<DemographicDataTypeDTO> getListDemographicDataTypes() {
		logger.info("[INICIO] EXPERIMENT SERVICE -- list demographicData types");
		
		List<DemographicDataType> list = new ArrayList<DemographicDataType>();
		Collections.addAll(list, DemographicDataType.values());
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- list demographicData types");
		return DtoAssembler.toListDemographicDataType(list);
	}
	
	/**
	 * Devuelve el experimento a partir del optional que se pasa como parámetro
	 * @param optional, parámetro de entrada
	 * @return experimento encontrado
	 * @throws ExperimentException, en caso de que el experimento no exista en base de datos.
	 */
	private Experiment getExperiment(Optional<Experiment> optional) throws ExperimentException {
		Experiment experiment = null;
		if(optional.isPresent()) {
			experiment = optional.get();
		}
		else {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}
		return experiment;
	}
	
	/**
	 * Devuelve el investigador a partir del optional que se pasa como parámetro
	 * @param optional, parámetro de entrada
	 * @return investigador encontrado
	 * @throws InvestigatorException, en caso de que el investigador no exista en base de datos.
	 */
	private Investigator getInvestigator(Optional<Investigator> optional) throws InvestigatorException{
		Investigator investigator = null;
		if(optional.isPresent()) {
			investigator = optional.get();
		}
		else {
			logger.error("[ERROR - 200] -- El investigador especificado no se encuentra registrado en el sistema");
			throw new InvestigatorException("200");
		}
		return investigator;
	}
	
	private void registerNote(Long idExperiment, String title, String description) {
		NoteDTO note = new NoteDTO();
		note.idExperiment = idExperiment;
		note.title = title;
		note.description = description;
		try {
			binnacleService.registerNote(note);
		} catch (NoteException e) {
			logger.error("[ERROR] -- Error al registrar la nota de cambio de estado. Excepción de bitácora: " + e);
		} catch (ExperimentException e) {
			logger.error("[ERROR] -- Error al registrar la nota de cambio de estado. Excepción de experimento: " + e);
		} catch (ForbiddenException e) {
			logger.error("[ERROR] -- Error al registrar la nota de cambio de estado. Excepción de permisos:" + e);
		}
	}

}

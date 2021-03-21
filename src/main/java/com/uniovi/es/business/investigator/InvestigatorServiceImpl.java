package com.uniovi.es.business.investigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.authentication.UserInSession;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.validators.InvestigatorValidator;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.Associations;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.PetitionNotRegistered;
import com.uniovi.es.model.Request;
import com.uniovi.es.model.types.StatusExperiment;
import com.uniovi.es.model.types.Role;
import com.uniovi.es.model.types.StatusPetition;
import com.uniovi.es.persistence.AdministrationDAO;
import com.uniovi.es.persistence.ExperimentDAO;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.persistence.PetitionNotRegisteredDAO;

@Service
public class InvestigatorServiceImpl implements InvestigatorService{
	
	private static final Logger logger = LoggerFactory.getLogger(InvestigatorServiceImpl.class);
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private InvestigatorValidator investigatorValidator;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserInSession userInSession;
	
	@Autowired
	private AdministrationDAO administrationDAO;
	
	@Autowired
	private PetitionNotRegisteredDAO petitionNotRegisteredDAO;
	
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private ExperimentDAO experimentDAO;

	@Override
	public void registerInvestigator(InvestigatorDTO dto) throws InvestigatorException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- register INVESTIGATOR ");
		
		investigatorValidator.validate(dto);
		investigatorValidator.validateExistenceOfMail(dto.mail);
		investigatorValidator.validatePassword(dto.password);
		
		Investigator investigator = new Investigator(dto.mail);
		DtoAssembler.fillData(investigator, dto);
		
		investigator.setPassword(bCryptPasswordEncoder.encode(investigator.getPassword()));
		//investigator.setRole(Role.INVESTIGATOR_EVALUATION);
		
		logger.info("\t \t Registrando el investigador en base de datos");
		investigatorDAO.save(investigator);
		
		//Una vez se haya registrado correctamente, se procede a comprobar si el investigador tenía petiones no registradas
		generatePetitions(dto);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- register INVESTIGATOR ");
	}

	/**
	 * En caso de que el investigador que se acaba de registrar tuviese peticiones no registradas, se envía 
	 * las peticiones asociadas al propio investigador y al experimento correspondiente.
	 * Finalmente, las peticiones no registradas son eliminadas del sistema.
	 * @param dto datos de entrada
	 * @throws InvestigatorException
	 */
	private void generatePetitions(InvestigatorDTO dto) throws InvestigatorException {
		logger.info("\t \t Se comprueba la existencia de peticiones no registradas");
		List<PetitionNotRegistered> list = petitionNotRegisteredDAO.getPetitionsByMail(dto.mail);
		if(list != null && list.size() != 0) {
			//Se procede a crear las peticiones
			logger.info("\t \t Se han encontrado un total de " + list.size() + " peticiones no registradas");
			for(PetitionNotRegistered p : list) {
				logger.info("\t \t Se trata la petición no registrada: " + p);
				Investigator i = investigatorDAO.findByMail(dto.mail);
				
				Optional<Experiment> optional = experimentDAO.findById(p.getExperiment().getId());
				Experiment experiment;
				try {
					experiment = getExperiment(optional);
				} catch (ExperimentException e) {
					logger.error("[ERROR -- 100] - El experimento especificado no se encuentra registrado en el sistema.");
					throw new InvestigatorException("100");
				}
				
				logger.info("\t \t Se crea la petición con investigador y experimento.");
				Petition petition = new Petition(i, experiment, null);
				DtoAssembler.fillData(petition, p);
				
				logger.info("\t \t Se almacena la petición en base de datos");
				petitionDAO.save(petition);
				
				logger.info("\t \t Se elimina la petición no registrada en base de datos");
				Associations.PetitionNotRegisteredExperiment.unlink(p, experiment);
				petitionNotRegisteredDAO.delete(p);
			}
		}
	}

	@Override
	public InvestigatorDTO getDetail(Long id) throws InvestigatorException{
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- detail INVESTIGATOR ");
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + id);
		Optional<Investigator> optional = investigatorDAO.findById(id);
		Investigator investigator = getInvestigator(optional);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- detail INVESTIGATOR ");
		return DtoAssembler.toDTO(investigator);
	}

	@Override
	public void updateInvestigator(InvestigatorDTO dto) throws InvestigatorException, ForbiddenException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- update INVESTIGATOR ");
		
		Investigator investigatorInSession = userInSession.getInvestigator();
		if(investigatorInSession == null || investigatorInSession.getId() != dto.id) {
			logger.error("[ERROR - 210] -- Un investigador solo puede modificar sus propios datos");
			throw new ForbiddenException("210");
		}
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + dto.id);
		Optional<Investigator> optional = investigatorDAO.findById(dto.id);
		Investigator investigator = getInvestigator(optional);
		
		investigatorValidator.validate(dto);
		
		logger.info("\t \t Validando existencia del mail: " + dto.mail);
		if(!investigator.getMail().toLowerCase().equals(dto.mail.toLowerCase())) {
			Investigator i = investigatorDAO.findByMail(dto.mail.toLowerCase());
			if(i != null) {
				logger.error("[ERROR - 204] -- El email del investigador ya se encuentra registrado en la aplicación");
				throw new InvestigatorException("204");
			}
		}
		
		DtoAssembler.fillDataUpdate(investigator, dto);
		
		logger.info("\t \t Registrando el investigador en base de datos");
		investigatorDAO.save(investigator);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- update INVESTIGATOR ");
	}
	
	@Override
	public List<ExperimentDTO> getExperimentsAcceptedByIdInvestigator(Long idInvestigator) throws InvestigatorException, ForbiddenException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- getExperimentsAcceptedByIdInvestigator INVESTIGATOR ");
		
		Investigator investigatorInSession = userInSession.getInvestigator();
		if(investigatorInSession == null || investigatorInSession.getId() != idInvestigator) {
			logger.error("[ERROR - 211] -- Un investigador solo puede ver los experimentos que él ha aceptado");
			throw new ForbiddenException("211");
		}
		
		logger.info("\t \t Obteniendo los experimentos del investigador: " + idInvestigator);
		List<Experiment> experiments = investigatorDAO.findExperimentsByIdInvestigator(idInvestigator, 
				StatusPetition.ACCEPTED, StatusExperiment.DELETED);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- getExperimentsAcceptedByIdInvestigator INVESTIGATOR ");
		return DtoAssembler.toList(experiments);
	}
	
	@Override
	public List<PetitionDTO> getPetitionsPendingByIdInvestigator(Long idInvestigator) throws InvestigatorException, ForbiddenException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- getPetitionsPendingByIdInvestigator INVESTIGATOR ");
		
		Investigator investigatorInSession = userInSession.getInvestigator();
		if(investigatorInSession == null || investigatorInSession.getId() != idInvestigator) {
			logger.error("[ERROR - 212] -- Un investigador solo puede ver los experimentos que él tiene pendientes de responder");
			throw new ForbiddenException("212");
		}
		
		logger.info("\t \t Obteniendo las peticiones del investigador: " + idInvestigator);
		List<Petition> petitions = investigatorDAO.findPetitionsByIdInvestigator(idInvestigator, StatusPetition.PENDING);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- getPetitionsPendingByIdInvestigator INVESTIGATOR ");
		return DtoAssembler.toListPetitions(petitions);
	}

	@Override
	public List<InvestigatorDTO> getListInvestigators() {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- all investigators ");

		List<Investigator> list = new ArrayList<Investigator>();
		investigatorDAO.findAll().forEach(list::add);;
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- all investigators ");
		return DtoAssembler.toListInvestigators(list);
	}
	
	@Override
	public InvestigatorDTO getInvestigatorByMail(String mail) throws InvestigatorException  {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- investigator by mail ");
		
		if(mail == null) {
			logger.error("[ERROR - 203] -- El mail es un campo obligatorio");
			throw new InvestigatorException("203");
		}
		
		Investigator investigator = investigatorDAO.findByMail(mail.toLowerCase());
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- investigator by mail ");
		
		if(investigator == null)
			return null;
		return DtoAssembler.toDTO(investigator);
	}
	
	@Override
	public InvestigatorDTO getInvestigatorInSession() {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- investigator in session ");
		
		Investigator investigator = userInSession.getInvestigator();
		InvestigatorDTO dto = DtoAssembler.toDTO(investigator);
		
		//Se busca si tiene solicitud enviada
		Request r = administrationDAO.findRequestPending(investigator.getId());
		if(r != null) {
			dto.requestPending = true;
		}
		else {
			dto.requestPending = false;
		}
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- investigator in session ");
		return dto;
	}

	@Override
	public List<InvestigatorDTO> getInvestigatorsNotAdministrator() {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- investigators not administrator");
		
		List<Investigator> list = new ArrayList<Investigator>();
		investigatorDAO.getInvestigatorsNotAdministrator().forEach(list::add);;
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- investigators not administrator");
		return DtoAssembler.toListInvestigators(list);
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
}

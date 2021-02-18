package com.uniovi.es.business.petition;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.authentication.UserInSession;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.petition.commands.Accept;
import com.uniovi.es.business.petition.commands.Cancel;
import com.uniovi.es.business.petition.commands.Reject;
import com.uniovi.es.business.validators.PetitionValidator;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.types.StatusPetition;
import com.uniovi.es.persistence.ExperimentDAO;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.utils.Identifier;

@Service
public class PetitionServiceImpl implements PetitionService{
	
	private static final Logger logger = LoggerFactory.getLogger(PetitionServiceImpl.class);
	
	private ActionManager actionManager = new ActionManager(null);
	
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private ExperimentDAO experimentDAO;
	
	@Autowired
	private PetitionValidator petitionValidator;
	
	@Autowired
	private UserInSession userInSession;

	@Override
	public void register(PetitionDTO dto) throws PetitionException, ExperimentException, InvestigatorException {
		logger.info("[INICIO] PETITION SERVICE -- register petition");
				
		logger.info("\t \t Obteniendo el investigador receptor a partir del ID: " + dto.idInvestigator);
		Optional<Investigator> optional = investigatorDAO.findById(dto.idInvestigator);
		Investigator investigator = getInvestigator(optional);
		
		logger.info("\t \t Obteniendo el experimento a partir del ID: " + dto.idExperiment);
		Optional<Experiment> optional2 = experimentDAO.findById(dto.idExperiment);
		Experiment experiment = getExperiment(optional2);
		
		logger.info("\t \t Comprobando que el investigador no tenga ya una petición PENDIENTE o ACEPTADA sobre el experimento");
		petitionValidator.validatePetitionExistence(dto.idInvestigator, dto.idExperiment);
		
		Petition petition = new Petition(investigator, experiment, dto.manager);
		DtoAssembler.fillData(petition, dto);
		
		petition.setIdInvestigatorSend(userInSession.getInvestigator().getId());
				
		logger.info("\t \t Registrando la petitición en base de datos");
		petitionDAO.save(petition);
		
		logger.info("[FINAL] PETITION SERVICE -- register petition");
	}

	@Override
	public void accept(Identifier id) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION SERVICE -- accept petition");
		
		if(id == null || id.getId() == null) {
			logger.error("[ERROR - 300] -- La petición especificada no se encuentra registrada en el sistema");
			throw new PetitionException("300");
		}
		
		logger.info("\t \t Obteniendo la petición a partir del ID: " + id.getId());
		Optional<Petition> optional = petitionDAO.findById(id.getId());
		Petition petition = getPetition(optional);
		
		Investigator investigator = userInSession.getInvestigator();
		if(investigator == null || investigator.getId() != petition.getInvestigator().getId()) {
			logger.error("[ERROR -- 305] - Una petición solo puede ser aceptada o rechazada por el investigador receptor de dicha petición");
			throw new ForbiddenException("305");
		}
		
		actionManager.setPetition(petition);
		actionManager.execute(new Accept());
		
		logger.info("\t \t Actualizando estado de la petitición en base de datos");
		petitionDAO.save(petition);
		
		logger.info("[FINAL] PETITION SERVICE -- accept petition");
	}
	
	@Override
	public void reject(Identifier id) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION SERVICE -- reject petition");
		
		if(id == null) {
			logger.error("[ERROR - 300] -- La petición especificada no se encuentra registrada en el sistema");
			throw new PetitionException("300");
		}
		
		logger.info("\t \t Obteniendo la petición a partir del ID: " + id.getId());
		Optional<Petition> optional = petitionDAO.findById(id.getId());
		Petition petition = getPetition(optional);
		
		Investigator investigator = userInSession.getInvestigator();
		if(investigator == null || investigator.getId() != petition.getInvestigator().getId()) {
			logger.error("[ERROR -- 305] - Una petición solo puede ser aceptada o rechazada por el investigador receptor de dicha petición");
			throw new ForbiddenException("305");
		}
		
		actionManager.setPetition(petition);
		actionManager.execute(new Reject());
		
		logger.info("\t \t Actualizando estado de la petitición en base de datos");
		petitionDAO.save(petition);
		
		logger.info("[FINAL] PETITION SERVICE -- reject petition");
	}

	@Override
	public void cancel(Identifier id) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION SERVICE -- cancel petition");
		
		if(id == null || id.getId() == null) {
			logger.error("[ERROR - 300] -- La petición especificada no se encuentra registrada en el sistema");
			throw new PetitionException("300");
		}
		
		logger.info("\t \t Obteniendo la petición a partir del ID: " + id.getId());
		Optional<Petition> optional = petitionDAO.findById(id.getId());
		Petition petition = getPetition(optional);
		
		//Si la petitición está en estado PENDING solo la pueda cancelar el emisor
		Investigator investigator = userInSession.getInvestigator();
		if(petition.getStatus().equals(StatusPetition.PENDING) && 
				(investigator == null || investigator.getId() != petition.getIdInvestigatorSend())) {
			logger.error("[ERROR -- 306] - Una petición solo puede ser cancelada por el investigador emisor de dicha petición si está en estado PENDING");
			throw new ForbiddenException("306");
		}
		
		//Si la petición está en estado ACEPTADA solo la pueden cancelar los investigadores asociados al experimento como gestores y el receptor
		if(petition.getStatus().equals(StatusPetition.ACCEPTED) && 
				(investigator == null || 
				(petitionDAO.isManager(petition.getExperiment().getId(), investigator.getId()) == null
				&& investigator.getId() != petition.getInvestigator().getId()))) {
			logger.error("[ERROR -- 307] - Una petición solo puede ser cancelada por los investigadores asociados al experimento como gestores y el investigador que la recibe si está en estado ACEPTADA");
			throw new ForbiddenException("307");
		}
		
		actionManager.setPetition(petition);
		actionManager.execute(new Cancel());
		
		logger.info("\t \t Actualizando estado de la petitición en base de datos");
		petitionDAO.save(petition);
		
		logger.info("[FINAL] PETITION SERVICE -- cancel petition");
	}

	@Override
	public PetitionDTO getDetail(Long id) throws PetitionException {
		logger.info("[INICIO] PETITION SERVICE -- detail petition");
		
		logger.info("\t \t Obteniendo la petición a partir del ID: " + id);
		Optional<Petition> optional = petitionDAO.findById(id);
		Petition petition = getPetition(optional);
		
		logger.info("[FINAL] PETITION SERVICE -- detail petition");
		return DtoAssembler.toDTO(petition);
	}
	
	@Override
	public List<PetitionDTO> getPetitionsReceived(){
		logger.info("[INICIO] PETITION SERVICE -- list petitions received");
		
		List<Petition> list = petitionDAO.findPetitionsReceived(userInSession.getInvestigator().getId());
		
		logger.info("[FINAL] PETITION SERVICE -- list petitions received");
		return DtoAssembler.toListPetitions(list);
	}
	
	@Override
	public List<PetitionDTO> getPetitionsSent(){
		logger.info("[INICIO] PETITION SERVICE -- list petitions sent");
		
		List<Petition> list = petitionDAO.findPetitionsSent(userInSession.getInvestigator().getId());
		
		logger.info("[FINAL] PETITION SERVICE -- list petitions sent");
		return DtoAssembler.toListPetitions(list);
	}
	
	@Override
	public void cancel(PetitionDTO dto) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION SERVICE -- cancel association between Investigator and Experiment");
		
		Petition petition = petitionDAO.isInvestigatorAssociatedExperiment(dto.idInvestigator, dto.idExperiment);
		
		if(petition == null) 
			cancel(new Identifier(null));
		else
			cancel(new Identifier(petition.getId()));
		
		
		logger.info("[FINAL] PETITION SERVICE -- cancel association between Investigator and Experiment");
	}
	
	/**
	 * Devuelve la petición a partir del optional que se pasa como parámetro
	 * @param optional, parámetro de entrada
	 * @return petición encontrada
	 * @throws PetitionException, en caso de que la petición no exista en base de datos
	 */
	private Petition getPetition(Optional<Petition> optional) throws PetitionException{
		Petition petition = null;
		if(optional.isPresent()) {
			petition = optional.get();
		}
		else {
			logger.error("[ERROR - 300] -- La petición especificada no se encuentra registrada en el sistema");
			throw new PetitionException("300");
		}
		return petition;
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

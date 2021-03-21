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
import com.uniovi.es.business.mail.MailSenderService;
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
import com.uniovi.es.model.PetitionNotRegistered;
import com.uniovi.es.model.types.StatusPetition;
import com.uniovi.es.persistence.ExperimentDAO;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.persistence.PetitionNotRegisteredDAO;
import com.uniovi.es.utils.Identifier;

@Service
public class PetitionServiceImpl implements PetitionService{
	
	private static final Logger logger = LoggerFactory.getLogger(PetitionServiceImpl.class);
	
	private ActionManager actionManager = new ActionManager();
	
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private ExperimentDAO experimentDAO;
	
	@Autowired
	private PetitionNotRegisteredDAO petitionNotRegisteredDAO;
	
	@Autowired
	private PetitionValidator petitionValidator;
	
	@Autowired
	private UserInSession userInSession;
	
	@Autowired
	private MailSenderService mailSenderService;
	
	@Override
	public void register(PetitionDTO dto) throws PetitionException, ExperimentException, InvestigatorException, ForbiddenException {
		logger.info("[INICIO] PETITION SERVICE -- register petition");
		
		if(dto.mail == null || dto.mail.equals("")) {
			logger.error("[ERROR -- 308] - En el registro de un petición, el correo electrónico es un campo obligatorio");
			throw new PetitionException("308");
		}
				
		logger.info("\t \t Obteniendo el investigador receptor a partir del mail: " + dto.mail);
		Investigator investigator = investigatorDAO.findByMail(dto.mail);
				
		logger.info("\t \t Obteniendo el experimento a partir del ID: " + dto.idExperiment);
		Optional<Experiment> optional2 = experimentDAO.findById(dto.idExperiment);
		Experiment experiment = getExperiment(optional2);
		
		Investigator inSession = userInSession.getInvestigator();
		if(inSession == null || petitionDAO.isManager(dto.idExperiment, inSession.getId()) == null) {
			logger.error("[ERROR -- 309] - Una petición solo puede ser creada por un investigador gestor del experimento");
			throw new ForbiddenException("309");
		}
		
		//Si el investigador es == null quiere decir que no está registrado en la aplicación
		if(investigator == null) {
			logger.info("\t \t Comprobando que no tenga ya una petición no registrada sobre el experimento");
			petitionValidator.validatePetitionNotRegisteredExistence(dto.mail, dto.idExperiment);
			
			logger.info("\t \t Se almacena la petición no registrada en el sistema");
			PetitionNotRegistered p = new PetitionNotRegistered(dto.mail, experiment);
			p.setManager(dto.manager);
			p.setIdInvestigatorSend(userInSession.getInvestigator().getId());
			
			petitionNotRegisteredDAO.save(p);
		}
		//Si el investigador es != null quiere decir que está registrado en base de datos
		else {
			logger.info("\t \t Comprobando que el investigador no tenga ya una petición PENDIENTE o ACEPTADA sobre el experimento");
			petitionValidator.validatePetitionExistence(investigator.getId(), dto.idExperiment);
			
			Petition petition = new Petition(investigator, experiment, dto.manager);
			DtoAssembler.fillData(petition, dto);
			
			petition.setIdInvestigatorSend(userInSession.getInvestigator().getId());
					
			logger.info("\t \t Registrando la petitición en base de datos");
			petitionDAO.save(petition);
		}
		
		//Se envía por correo el aviso de que se le ha enviado la petición
		try {
			mailSenderService.sendMail(
					dto.mail,
					"Invitación a experimento", 
					"Hola.\n\nUsted ha recibido una invitación para participar en el siguiente experimento:\n\n " 
							+ "\tTítulo: " + experiment.getTitle() 
							+ "\n\tDescripción: " + experiment.getDescription()
							+ "\n\nPara acceder al experimento debe aceptarlo desde la siguiente url: ");
		} catch (Exception e) {
			logger.error("[ERROR -- 600] - Se ha producido un error al enviar el correo electrónico.  " + e);
		}
		
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
		
		actionManager.execute(new Accept(), petition);
		
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
		
		actionManager.execute(new Reject(), petition);
		
		logger.info("\t \t Actu)alizando estado de la petitición en base de datos");
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
		if(petition.isPending() && 
				(investigator == null || investigator.getId() != petition.getIdInvestigatorSend())) {
			logger.error("[ERROR -- 306] - Una petición solo puede ser cancelada por el investigador emisor de dicha petición si está en estado PENDING");
			throw new ForbiddenException("306");
		}
		
		//Si la petición está en estado ACEPTADA solo la pueden cancelar los investigadores asociados al experimento como gestores y el receptor
		if(petition.isAccepted() && 
				(investigator == null || 
				(petitionDAO.isManager(petition.getExperiment().getId(), investigator.getId()) == null
				&& investigator.getId() != petition.getInvestigator().getId()))) {
			logger.error("[ERROR -- 307] - Una petición solo puede ser cancelada por los investigadores asociados al experimento como gestores y el investigador que la recibe si está en estado ACEPTADA");
			throw new ForbiddenException("307");
		}
		
		actionManager.execute(new Cancel(), petition);
		
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
	
	@Override
	public PetitionNotRegistered getPetitionNotRegistered(String mail, Long idExperiment) {
		logger.info("[INICIO] PETITION SERVICE -- get petition not registered by mail and idInvestigator");
		
		PetitionNotRegistered p = petitionNotRegisteredDAO.getPetitionByMailAndExperiment(mail, idExperiment);
		
		logger.info("[FINAL] PETITION SERVICE -- get petition not registered by mail and idInvestigator");
		return p;
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

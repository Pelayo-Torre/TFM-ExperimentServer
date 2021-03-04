package com.uniovi.es.business.administration;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.administration.commands.Accept;
import com.uniovi.es.business.administration.commands.Reject;
import com.uniovi.es.business.authentication.UserInSession;
import com.uniovi.es.business.dto.RequestDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Request;
import com.uniovi.es.model.types.Role;
import com.uniovi.es.persistence.AdministrationDAO;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.utils.Identifier;

@Service
public class AdministrationServiceImpl implements AdministrationService{
	
	@Autowired
	private AdministrationDAO administrationDAO;
	
	@Autowired
	private UserInSession userInSession;
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	private ActionManager actionManager = new ActionManager();
	
	private static final Logger logger = LoggerFactory.getLogger(AdministrationServiceImpl.class);

	@Override
	public void register(RequestDTO dto) throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION SERVICE -- register request");
		
		logger.info("\t \t Se valida que la petición sea enviada por el usuario en sesión");
		Investigator investigator = userInSession.getInvestigator();
		
		if(investigator.getId() != dto.idInvestigator) {
			logger.error("[ERROR - 503] -- El investigador que envía la petición no es el investigador que se encuentra en sesión");
			throw new ForbiddenException("503");
		}
		
		logger.info("\t \t Se comprueba que el investigador no tenga alguna solicitud pendiente.");
		Request request = administrationDAO.findRequestAccepted(dto.idInvestigator);
		if(request != null) {
			logger.error("[ERROR - 501] -- El investigador tiene una petición en estado PENDING o ACCEPTED");
			throw new AdministrationException("501");
		}
		
		logger.info("\t \t Se valida que el investigador tenga el rol de Evaluación");
		if(!investigator.getRole().name().equals(Role.INVESTIGATOR_EVALUATION.name())) {
			logger.error("[ERROR - 500] -- Para el envío de una solicitud de aprobación de cuenta, el estado del investigador debe ser INVESTIGATOR_EVALUATION");
			throw new AdministrationException("500");
		}
		
		logger.info("\t \t Se almacena la solicitud en el sistema");
		Request r = new Request(investigator);
		DtoAssembler.fillData(r, dto);
		administrationDAO.save(r);
		
		logger.info("[FINAL] ADMINISTRATION SERVICE -- register request");
	}

	@Override
	public void accept(Identifier id) throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION SERVICE -- accept request");
		
		if(id == null || id.getId() == null) {
			logger.error("[ERROR - 505] -- La solicitud especificada no se encuentra registrada en el sistema");
			throw new AdministrationException("505");
		}
		
		logger.info("\t \t Obteniendo la solicitud a partir del ID: " + id.getId());
		Optional<Request> optional = administrationDAO.findById(id.getId());
		Request request = getRequest(optional);
		
		logger.info("\t \t Se comprueba que el investigador que acepta la petición tiene rol ADMINISTRATOR");
		Investigator investigator = userInSession.getInvestigator();
		
		if(investigator == null || 
				!investigator.getRole().name().equals(Role.ADMINISTRATOR.name())) {
			logger.error("[ERROR - 502] -- Una solicitud solo puede ser aprobada o rechazada por aquellos investigadores con rol ADMINISTRATOR");
			throw new ForbiddenException("502");
		}
		
		logger.info("\t \t Se acepta la solicitud");
		actionManager.execute(new Accept(), request);
		
		logger.info("\t \t Se registran los datos en base de datos");
		administrationDAO.save(request);
		investigatorDAO.save(request.getInvestigator());
		
		logger.info("[FINAL] ADMINISTRATION SERVICE -- accept request");
	}

	@Override
	public void reject(Identifier id) throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION SERVICE -- reject request");
		
		if(id == null || id.getId() == null) {
			logger.error("[ERROR - 505] -- La solicitud especificada no se encuentra registrada en el sistema");
			throw new AdministrationException("505");
		}
		
		logger.info("\t \t Obteniendo la solicitud a partir del ID: " + id.getId());
		Optional<Request> optional = administrationDAO.findById(id.getId());
		Request request = getRequest(optional);
		
		logger.info("\t \t Se comprueba que el investigador que rechaza la petición tiene rol ADMINISTRATOR");
		Investigator investigator = userInSession.getInvestigator();
		
		if(investigator == null || 
				!investigator.getRole().name().equals(Role.ADMINISTRATOR.name())) {
			logger.error("[ERROR - 502] -- Una solicitud solo puede ser aprobada o rechazada por aquellos investigadores con rol ADMINISTRATOR");
			throw new ForbiddenException("502");
		}
		
		logger.info("\t \t Se rechaza la solicitud");
		actionManager.execute(new Reject(), request);
		
		logger.info("\t \t Se registran los datos en base de datos");
		administrationDAO.save(request);
		
		logger.info("[FINAL] ADMINISTRATION SERVICE -- reject request");
	}

	@Override
	public List<RequestDTO> getPendingRequests() throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION SERVICE -- pending requests");
		
		List<RequestDTO> list = DtoAssembler.toListRequests(administrationDAO.getPendingRequests());
		
		logger.info("[FINAL] ADMINISTRATION SERVICE -- pending requests");
		return list;
	}

	@Override
	public RequestDTO getDetail(Long id) throws AdministrationException {
		logger.info("[INICIO] ADMINISTRATION SERVICE -- detail request");
		
		RequestDTO dto = DtoAssembler.toDto(getRequest(administrationDAO.findById(id)));
		
		logger.info("[FINAL] ADMINISTRATION SERVICE -- detail request");
		return dto;
	}
	
	@Override
	public void convertInvestigatorIntoAdministrator(Identifier id) throws AdministrationException, ForbiddenException, InvestigatorException {
		logger.info("[INICIO] ADMINISTRATION SERVICE -- convert administrator");
		
		if(id == null || id.getId() == null) {
			logger.error("[ERROR -- 200] - El investigador especificado no se encuentra registrado en el sistema");
			throw new InvestigatorException("200");
		}
		
		Investigator sesion = userInSession.getInvestigator();
		if(sesion == null || 
				!sesion.getRole().name().equals(Role.ADMINISTRATOR.name())) {
			logger.error("[ERROR -- 507] - Solamente un Investigador con rol ADMINISTRATOR puede establecerle a otro investigador el rol ADMINISTRATOR");
			throw new ForbiddenException("507");
		}
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + id);
		Optional<Investigator> optional = investigatorDAO.findById(id.getId());
		Investigator investigator = getInvestigator(optional);
		
		logger.info("\t \t Se comprueba el rol del investigador al que se le va a establecer el rol ADMINISTRATOR");
		if(investigator.getRole().name().equals(Role.ADMINISTRATOR.name())) {
			logger.error("[ERROR -- 506] - Para convertir un investigador en administrador, éste debe tener el rol INVESTIGATOR_EVALUATION o INVESTIGATOR_VALIDATED");
			throw new AdministrationException("506");
		}
		
		logger.info("\t \t Se procede a cambiar el rol al investigador y a registrarlo en base de datos");
		investigator.setRole(Role.ADMINISTRATOR);
		investigatorDAO.save(investigator);
		
		//Se acepta la petición pendiente en caso de que investigador la tenga
		Request request = administrationDAO.findRequestAccepted(id.getId());
		if(request != null) {
			request.accept();
			administrationDAO.save(request);
		}
		
		logger.info("[FINAL] ADMINISTRATION SERVICE -- convert administrator");
	}
	
	/**
	 * Devuelve la solicitud a partir del optional que se le pasa como parámetro
	 * @param optional, parámetro de entrada con la posible solicitud
	 * @return la solicitud obtenida del optional
	 * @throws AdministrationException  en caso de que el optional devuelva null
	 */
	private Request getRequest(Optional<Request> optional) throws AdministrationException {
		Request request = null;
		if(optional.isPresent()) {
			request = optional.get();
		}
		else {
			logger.error("[ERROR - 505] -- La solicitud especificada no se encuentra registrada en el sistema");
			throw new AdministrationException("505");
		}
		return request;
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


}

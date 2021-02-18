package com.uniovi.es.presentation.administration;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.es.business.administration.AdministrationService;
import com.uniovi.es.business.dto.RequestDTO;
import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.utils.Identifier;

@RestController
@RequestMapping("/administration")
@CrossOrigin(origins = "http://localhost:4200")
public class AdministrationControllerImpl implements AdministrationController{
	
	private static final Logger logger = LoggerFactory.getLogger(AdministrationControllerImpl.class);

	@Autowired
	private AdministrationService administrationService;
	
	@Override
	@RequestMapping(value = "/register/request", method = RequestMethod.POST)
	public void register(@RequestBody RequestDTO dto) throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION CONTROLLER -- register request");
		logger.info("\t\t Parámetros de entrada: " + dto);
		
		administrationService.register(dto);
		
		logger.info("[FINAL] ADMINISTRATION CONTROLLER -- register request");
	}

	@Override
	@RequestMapping(value = "/accept/request", method = RequestMethod.PUT)
	public void accept(@RequestBody Identifier id) throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION CONTROLLER -- accept request");
		logger.info("\t\t Parámetros de entrada: " + id);
		
		administrationService.accept(id);
		
		logger.info("[FINAL] ADMINISTRATION CONTROLLER -- accept request");
	}

	@Override
	@RequestMapping(value = "/reject/request", method = RequestMethod.PUT)
	public void reject(@RequestBody Identifier id) throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION CONTROLLER -- reject request");
		logger.info("\t\t Parámetros de entrada: " + id);
		
		administrationService.reject(id);
		
		logger.info("[FINAL] ADMINISTRATION CONTROLLER -- reject request");
	}

	@Override
	@RequestMapping(value = "/requests", method = RequestMethod.GET)
	public List<RequestDTO> getPendingRequests() throws AdministrationException, ForbiddenException {
		logger.info("[INICIO] ADMINISTRATION CONTROLLER -- getPending request");
		
		List<RequestDTO> lista = administrationService.getPendingRequests();
		
		logger.info("[FINAL] ADMINISTRATION CONTROLLER -- getPending requests");
		return lista;
	}

	@Override
	@RequestMapping(value = "/request/detail/{idRequest}", method = RequestMethod.GET)
	public RequestDTO getDetail(@PathVariable Long idRequest) throws AdministrationException {
		logger.info("[INICIO] ADMINISTRATION CONTROLLER -- detail request");
		logger.info("\t\t Parámetros de entrada: " + idRequest);
		
		RequestDTO dto = administrationService.getDetail(idRequest);
		
		logger.info("[FINAL] ADMINISTRATION CONTROLLER -- detail request");
		return dto;
	}

}

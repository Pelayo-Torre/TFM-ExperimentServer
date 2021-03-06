package com.uniovi.es.presentation.petition;

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

import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.petition.PetitionService;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.utils.Identifier;

@RestController
@RequestMapping("/petition")
@CrossOrigin(origins = "http://localhost:4200")
public class PetitionControllerImpl implements PetitionController{
	
	private static final Logger logger = LoggerFactory.getLogger(PetitionControllerImpl.class);
	
	@Autowired
	private PetitionService petitionService;

	@Override
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void register(@RequestBody PetitionDTO dto) throws PetitionException, ExperimentException, InvestigatorException {
		logger.info("[INICIO] PETITION CONTROLLER -- register petition");
		
		petitionService.register(dto);
		
		logger.info("[FINAL] PETITION CONTROLLER -- register petition");
	}

	@Override
	@RequestMapping(value = "/accept", method = RequestMethod.PUT)
	public void accept(@RequestBody Identifier id) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION CONTROLLER -- accept petition");
		
		petitionService.accept(id);
		
		logger.info("[FINAL] PETITION CONTROLLER -- accept petition");
	}
	
	@Override
	@RequestMapping(value = "/reject", method = RequestMethod.PUT)
	public void reject(@RequestBody Identifier id) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION CONTROLLER -- reject petition");
		
		petitionService.reject(id);
		
		logger.info("[FINAL] PETITION CONTROLLER -- reject petition");
	}

	@Override
	@RequestMapping(value = "/cancel", method = RequestMethod.PUT)
	public void cancel(@RequestBody Identifier id) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION CONTROLLER -- cancel petition");
		
		petitionService.cancel(id);
		
		logger.info("[FINAL] PETITION CONTROLLER -- cancel petition");
	}

	@Override
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public PetitionDTO getDetail(@PathVariable Long id) throws PetitionException, InvestigatorException {
		logger.info("[INICIO] PETITION CONTROLLER -- detail petition");
		
		PetitionDTO dto = petitionService.getDetail(id);
		
		logger.info("[FINAL] PETITION CONTROLLER -- detail petition");
		return dto;
	}

	@Override
	@RequestMapping(value = "/received", method = RequestMethod.GET)
	public List<PetitionDTO> getPetitionReceived() {
		logger.info("[INICIO] PETITION CONTROLLER -- list petitions received");
		
		List<PetitionDTO> list = petitionService.getPetitionsReceived();
		
		logger.info("[FINAL] PETITION CONTROLLER -- list petitions received");
		return list;
	}
	
	@Override
	@RequestMapping(value = "/sent", method = RequestMethod.GET)
	public List<PetitionDTO> getPetitionSent() {
		logger.info("[INICIO] PETITION CONTROLLER -- list petitions sent");
		
		List<PetitionDTO> list = petitionService.getPetitionsSent();
		
		logger.info("[FINAL] PETITION CONTROLLER -- list petitions sent");
		return list;
	}

	@Override
	@RequestMapping(value = "/cancel/association", method = RequestMethod.PUT)
	public void cancel(@RequestBody PetitionDTO dto) throws PetitionException, ForbiddenException {
		logger.info("[INICIO] PETITION CONTROLLER -- cancel association between Investigator and Experiment");
		
		petitionService.cancel(dto);
		
		logger.info("[FINAL] PETITION CONTROLLER -- cancel association between Investigator and Experiment");
	}

}

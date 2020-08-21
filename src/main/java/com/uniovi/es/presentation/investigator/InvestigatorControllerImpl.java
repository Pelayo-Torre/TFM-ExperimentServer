package com.uniovi.es.presentation.investigator;

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

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.InvestigatorException;


@RestController
@RequestMapping("/investigator")
@CrossOrigin(origins = "http://localhost:4200")
public class InvestigatorControllerImpl implements InvestigatorController{
	
	private static final Logger logger = LoggerFactory.getLogger(InvestigatorControllerImpl.class);
	
	@Autowired
	private InvestigatorService investigatorService;

	@Override
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void registerInvestigator(@RequestBody InvestigatorDTO dto) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- register investigator");
		
		investigatorService.registerInvestigator(dto);
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- register investigator");
	}

	@Override
	@RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
	public InvestigatorDTO getInvestigator(@PathVariable Long id) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- detail investigator");
		
		InvestigatorDTO dto =  investigatorService.getDetail(id);
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- detail investigator");
		return dto;
	}

	@Override
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public void updateInvestigator(@RequestBody InvestigatorDTO dto) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- update investigator");
		
		investigatorService.updateInvestigator(dto);
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- update investigator");
	}
	
	@Override
	@RequestMapping(value = "/experiments/accepted{idInvestigator}", method = RequestMethod.GET)
	public List<ExperimentDTO> getExperimentsAcceptedByIdInvestigator(@PathVariable Long idInvestigator) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- experiments accepted investigator");
		
		List<ExperimentDTO> list = investigatorService.getExperimentsAcceptedByIdInvestigator(idInvestigator);
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- experiments accepted investigator");
		return list;
	}

	@Override
	@RequestMapping(value = "/petitions/pending/{idInvestigator}", method = RequestMethod.GET)
	public List<PetitionDTO> getPetitionsPendingByIdInvestigator(Long idInvestigator) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- petitions pending investigator");
		
		List<PetitionDTO> list = investigatorService.getPetitionsPendingByIdInvestigator(idInvestigator);
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- petitions pending investigator");
		return list;
	}

	@Override
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public List<InvestigatorDTO> getListInvestigators() {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- all investigators");

		List<InvestigatorDTO> list = investigatorService.getListInvestigators();
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- all investigators");
		return list;
	}
	
	@Override
	@RequestMapping(value = "/mail/{mail}", method = RequestMethod.GET)
	public InvestigatorDTO getInvestigatorByMail(@PathVariable String mail) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- investigator by mail");

		InvestigatorDTO dto = investigatorService.getInvestigatorByMail(mail);
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- investigator by mail");
		return dto;
	}

	@Override
	@RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
	public InvestigatorDTO getInvestigatorByUsername(@PathVariable String username) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- investigator by username");

		InvestigatorDTO dto = investigatorService.getInvestigatorByUsername(username);
		
		logger.info("[FINAL] INVESTIGATOR CONTROLLER -- investigator by username");
		return dto;
	}
	
}

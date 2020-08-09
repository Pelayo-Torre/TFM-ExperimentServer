package com.uniovi.es.presentation.investigator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.investigator.InvestigatorService;
import com.uniovi.es.exceptions.InvestigatorException;


@RestController
@RequestMapping("/investigator")
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
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- register investigator");
		
		return investigatorService.getDetail(id);
	}

	@Override
	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	public void updateInvestigator(@RequestBody InvestigatorDTO dto) throws InvestigatorException {
		logger.info("[INICIO] INVESTIGATOR CONTROLLER -- update investigator");
		
		investigatorService.updateInvestigator(dto);
		
		logger.info("[INAL] INVESTIGATOR CONTROLLER -- update investigator");
	}
	
	@Override
	@RequestMapping(value = "/experiments/{idInvestigator}", method = RequestMethod.GET)
	public List<ExperimentDTO> getExperimentsByInvestigator(@PathVariable Long idInvestigator) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

package com.uniovi.es.business.investigator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.InvestigatorDTO;

@Service
public class InvestigatorServiceImpl implements InvestigatorService{
	
	private static final Logger logger = LoggerFactory.getLogger(InvestigatorServiceImpl.class);

	@Override
	public void registerInvestigator(InvestigatorDTO dto) {
		logger.debug("[INICIO -- INVESTIGATOR-SERVICE -- REGISTER INVESTIGATOR] ");
		
		
		
		logger.debug("[FINAL -- INVESTIGATOR-SERVICE -- REGISTER INVESTIGATOR] ");
	}

	@Override
	public InvestigatorDTO getInvestigator(Long id) {
		logger.debug("[INICIO -- INVESTIGATOR-SERVICE -- GET INVESTIGATOR] ");
		
		
		
		logger.debug("[FINAL -- INVESTIGATOR-SERVICE -- REGISTER INVESTIGATOR] ");
		return null;
	}

	@Override
	public void updateInvestigator(InvestigatorDTO dto) {
		// TODO Auto-generated method stub
		
	}

	
	
}

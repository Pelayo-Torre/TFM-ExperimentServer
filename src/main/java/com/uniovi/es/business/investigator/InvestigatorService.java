package com.uniovi.es.business.investigator;


import com.uniovi.es.business.dto.InvestigatorDTO;


public interface InvestigatorService {
	
	public void registerInvestigator(InvestigatorDTO dto);

	public InvestigatorDTO getInvestigator(Long id);

	public void updateInvestigator(InvestigatorDTO dto);

}

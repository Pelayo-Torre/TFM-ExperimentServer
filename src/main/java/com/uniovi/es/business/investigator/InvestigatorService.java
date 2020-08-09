package com.uniovi.es.business.investigator;


import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.exceptions.InvestigatorException;


public interface InvestigatorService {
	
	public void registerInvestigator(InvestigatorDTO dto) throws InvestigatorException;

	public InvestigatorDTO getDetail(Long id) throws InvestigatorException;

	public void updateInvestigator(InvestigatorDTO dto) throws InvestigatorException;

}

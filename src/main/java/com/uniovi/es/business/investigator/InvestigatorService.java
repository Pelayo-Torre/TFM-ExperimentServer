package com.uniovi.es.business.investigator;


import java.util.List;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;


public interface InvestigatorService {
	
	public void registerInvestigator(InvestigatorDTO dto) throws InvestigatorException;

	public InvestigatorDTO getDetail(Long id) throws InvestigatorException;
	
	public InvestigatorDTO getInvestigatorByMail(String mail) throws InvestigatorException ;

	public void updateInvestigator(InvestigatorDTO dto) throws InvestigatorException, ForbiddenException;

	public List<ExperimentDTO> getExperimentsAcceptedByIdInvestigator(Long idInvestigator) throws InvestigatorException, ForbiddenException;

	public List<PetitionDTO> getPetitionsPendingByIdInvestigator(Long idInvestigator) throws InvestigatorException, ForbiddenException;

	public List<InvestigatorDTO> getListInvestigators();

	public InvestigatorDTO getInvestigatorInSession();
	
	public List<InvestigatorDTO> getInvestigatorsNotAdministrator();

}

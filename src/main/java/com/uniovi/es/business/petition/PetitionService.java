package com.uniovi.es.business.petition;

import java.util.List;

import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.PetitionNotRegistered;
import com.uniovi.es.utils.Identifier;

public interface PetitionService {
	
	public void register(PetitionDTO dto) throws PetitionException, ExperimentException, InvestigatorException, ForbiddenException;
	
	public void accept(Identifier id) throws PetitionException, ForbiddenException;
	
	public void reject(Identifier id) throws PetitionException, ForbiddenException;
	
	public void cancel(Identifier id) throws PetitionException, ForbiddenException;
	
	public PetitionDTO getDetail(Long id) throws PetitionException;

	public List<PetitionDTO> getPetitionsReceived();

	public List<PetitionDTO> getPetitionsSent();
	
	public void cancel(PetitionDTO dto) throws PetitionException, ForbiddenException;
	
	public PetitionNotRegistered getPetitionNotRegistered(String mail, Long idExperiment);

}

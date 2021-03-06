package com.uniovi.es.business.experiment;

import java.util.List;

import com.uniovi.es.business.dto.DemographicDataTypeDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.utils.Identifier;

public interface ExperimentService {
	
	public void open(Identifier id) throws ExperimentException, ForbiddenException;
	
	public void reOpen(Identifier id) throws ExperimentException, ForbiddenException;
	
	public void close(Identifier id) throws ExperimentException, ForbiddenException;
	
	public void delete(Identifier id) throws ExperimentException, ForbiddenException;

	public void update(ExperimentDTO dto) throws ExperimentException, ForbiddenException;

	public void register(ExperimentDTO dto) throws ExperimentException, InvestigatorException, ForbiddenException;

	public ExperimentDTO getDetail(Long id)  throws ExperimentException, ForbiddenException;

	public List<InvestigatorDTO> getInvestigatorsOfExperiment(Long id)  throws ExperimentException, ForbiddenException;

	public List<ExperimentDTO> getExperiments();
	
	public List<InvestigatorDTO> getInvestigatorsNotAssociatedAnExperiment(Long id) throws ExperimentException;

	public List<DemographicDataTypeDTO> getListDemographicDataTypes();

}

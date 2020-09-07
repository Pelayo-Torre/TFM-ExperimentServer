package com.uniovi.es.business.experiment;

import java.util.List;

import com.uniovi.es.business.dto.DeviceDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.utils.Identifier;

public interface ExperimentService {
	
	public void open(Identifier id) throws ExperimentException;
	
	public void reOpen(Identifier id) throws ExperimentException;
	
	public void close(Identifier id) throws ExperimentException;
	
	public void delete(Identifier id) throws ExperimentException;

	public void update(ExperimentDTO dto) throws ExperimentException;

	public void register(ExperimentDTO dto) throws ExperimentException, InvestigatorException;

	public ExperimentDTO getDetail(Long id)  throws ExperimentException;

	public List<InvestigatorDTO> getInvestigatorsOfExperiment(Long id)  throws ExperimentException;

	public List<ExperimentDTO> getExperiments();
	
	public List<DeviceDTO> getAllDevices();

}

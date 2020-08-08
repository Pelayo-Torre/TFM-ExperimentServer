package com.uniovi.es.business.experiment;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.exceptions.ExperimentException;

public interface ExperimentService {
	
	public void open(Long id) throws ExperimentException;
	
	public void reOpen(Long id) throws ExperimentException;
	
	public void close(Long id) throws ExperimentException;
	
	public void delete(Long id) throws ExperimentException;

	public void update(ExperimentDTO dto) throws ExperimentException;

}

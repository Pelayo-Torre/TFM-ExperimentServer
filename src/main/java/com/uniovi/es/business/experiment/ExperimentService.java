package com.uniovi.es.business.experiment;

import com.uniovi.es.exceptions.ExperimentException;

public interface ExperimentService {
	
	public void open(String id) throws ExperimentException;
	
	public void reOpen(String id) throws ExperimentException;
	
	public void close(String id) throws ExperimentException;
	
	public void delete(String id) throws ExperimentException;

}

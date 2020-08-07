package com.uniovi.es.persistence;

import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Experiment;

public interface ExperimentDAO extends CrudRepository <Experiment, Long> {
	
	
	
}

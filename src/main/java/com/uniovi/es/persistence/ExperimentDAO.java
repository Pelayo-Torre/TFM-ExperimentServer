package com.uniovi.es.persistence;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;

public interface ExperimentDAO extends CrudRepository <Experiment, Long> {

	@Query("SELECT DISTINCT i FROM Investigator i, Petition p "
			+ "WHERE i.id NOT IN "
			+ 		"(SELECT p.investigator.id FROM Petition p WHERE p.experiment.id = ?1 AND (p.status = 'PENDING' OR p.status = 'ACCEPTED') ORDER BY p.investigator.name ASC, p.investigator.surname ASC) ")
	public List<Investigator> findInvestigatorsNotAssociatedAnExperiment(Long idExperiment);
	
}

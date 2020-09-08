package com.uniovi.es.persistence;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.types.StatusPetition;

public interface ExperimentDAO extends CrudRepository <Experiment, Long> {

	@Query("SELECT DISTINCT i FROM Investigator i, Petition p "
			+ "WHERE (p.investigator.id = i.id AND p.experiment.id = ?1 AND (p.status = ?2 OR p.status = ?3)) "
			+ " OR (i.id NOT IN (SELECT p.investigator.id FROM Petition p WHERE p.experiment.id = ?1))")
	public List<Investigator> findInvestigatorsNotAssociatedAnExperiment(Long idExperiment, StatusPetition rejected, StatusPetition cancelled);
	
}

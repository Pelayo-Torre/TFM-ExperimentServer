package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.StatusPetition;

public interface InvestigatorDAO extends CrudRepository <Investigator, Long> {
	
	@Query("SELECT i FROM Investigator i WHERE LOWER(i.mail) = ?1")
	public Investigator findByMail(String mail);
	
	@Query("SELECT i FROM Investigator i WHERE LOWER(i.username) = ?1")
	public Investigator findByUsername(String username);
	
	@Query("SELECT e FROM Experiment e, Petition p WHERE p.investigator.id = ?1 AND p.experiment.id = e.id AND p.status = ?2")
	public List<Experiment> findExperimentsByIdInvestigator(Long idInvestigator, StatusPetition status);

	@Query("SELECT p FROM Petition p WHERE p.investigator.id = ?1 AND p.status = ?2")
	public List<Petition> findPetitionsByIdInvestigator(Long idInvestigator, StatusPetition pending);

}

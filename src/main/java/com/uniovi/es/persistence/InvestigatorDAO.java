package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.types.StatusExperiment;
import com.uniovi.es.model.types.StatusPetition;

public interface InvestigatorDAO extends CrudRepository <Investigator, Long> {
	
	@Query("SELECT i FROM Investigator i WHERE LOWER(i.mail) = ?1")
	public Investigator findByMail(String mail);
	
	@Query("SELECT e "
			+ "FROM Experiment e, Petition p "
			+ "WHERE p.investigator.id = ?1 AND p.experiment.id = e.id AND p.status = ?2 AND e.status != ?3 "
			+ "ORDER BY e.creationDate DESC")
	public List<Experiment> findExperimentsByIdInvestigator(Long idInvestigator, StatusPetition status, StatusExperiment statusExperiment);

	@Query("SELECT p FROM Petition p WHERE p.investigator.id = ?1 AND p.status = ?2 ORDER BY p.investigator.name DESC")
	public List<Petition> findPetitionsByIdInvestigator(Long idInvestigator, StatusPetition pending);
	
	@Query("SELECT i FROM Investigator i, Petition p WHERE p.experiment.id = ?1 AND p.creator = true AND p.investigator.id = i.id")
	public Investigator findCreatorOfExperiment(Long idExperiment);
	
	@Query("SELECT i FROM Investigator i WHERE i.role != 'ADMINISTRATOR' ORDER BY i.name ASC, i.surname ASC")
	public List<Investigator> getInvestigatorsNotAdministrator();

}

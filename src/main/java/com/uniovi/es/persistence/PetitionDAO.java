package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Petition;
import com.uniovi.es.model.types.StatusPetition;

public interface PetitionDAO extends CrudRepository <Petition, Long> {

	@Query("SELECT p FROM Petition p WHERE p.experiment.id = ?1 AND p.status = ?2 ORDER BY p.investigator.name ASC, p.investigator.surname ASC")
	public List<Petition> findByIdExperiment(Long id, StatusPetition status);

	@Query("SELECT p FROM Petition p WHERE p.investigator.id = ?1 AND p.experiment.id = ?2 AND (p.status = 'PENDING' OR p.status = 'ACCEPTED')")
	public Petition findPetitionByIdInvestigatorAndIdExperiment(Long idInvestigator, Long idExperiment);

	@Query("SELECT p FROM Petition p WHERE p.investigator.id = ?1 AND p.creator = false ORDER BY p.shippingDate DESC")
	public List<Petition> findPetitionsReceived(Long id);
	
	@Query("SELECT p FROM Petition p WHERE p.idInvestigatorSend = ?1 ORDER BY p.shippingDate DESC")
	public List<Petition> findPetitionsSent(Long id);

	@Query("SELECT p FROM Petition p WHERE p.experiment.id = ?1 AND p.status = 'ACCEPTED' AND p.investigator.id = ?2 AND manager = true")
	public Petition isManager(Long idExperiment, Long idInvestigator);
	
	@Query("SELECT p FROM Petition p WHERE p.investigator.id = ?1 AND p.experiment.id = ?2 AND p.status = 'ACCEPTED'")
	public Petition isInvestigatorAssociatedExperiment(Long idInvestigator, Long idExperiment);
}

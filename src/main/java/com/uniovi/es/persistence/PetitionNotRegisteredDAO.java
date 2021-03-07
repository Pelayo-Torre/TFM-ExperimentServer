package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.PetitionNotRegistered;

public interface PetitionNotRegisteredDAO extends CrudRepository <PetitionNotRegistered, Long> {

	@Query("SELECT p FROM PetitionNotRegistered p WHERE p.mail = ?1 AND p.experiment.id = ?2")
	public PetitionNotRegistered getPetitionByMailAndExperiment(String mail, Long idExperiment);
	
	@Query("SELECT p FROM PetitionNotRegistered p WHERE p.mail = ?1")
	public List<PetitionNotRegistered> getPetitionsByMail(String mail);
	
}

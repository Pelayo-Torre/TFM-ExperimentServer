package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.User;

public interface UserDAO extends CrudRepository <User, Long> {

	@Query("SELECT u FROM User u WHERE u.experiment.id = ?1")
	public List<User> getUsersByExperiment(Long idExperiment);
	
	@Query("SELECT u FROM User u WHERE u.experiment.id = ?1 AND u.sessionId = ?2")
	public User getUserByExperimentAndSession(Long idExperiment, String sessionId);
	
}

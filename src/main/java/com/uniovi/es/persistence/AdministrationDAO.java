package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Request;

public interface AdministrationDAO extends CrudRepository <Request, Long> {

	@Query("SELECT r FROM Request r WHERE r.investigator.id = ?1 AND ( r.status = 'ACCEPTED' OR r.status = 'PENDING' )")
	public Request findRequestAccepted(Long idInvestigator);

	@Query("SELECT r FROM Request r WHERE r.status = 'PENDING' AND r.investigator.role != 'ADMINISTRATOR'")
	public List<Request> getPendingRequests();

	@Query("SELECT r FROM Request r WHERE r.investigator.id = ?1 AND r.status = 'PENDING'")
	public Request findRequestPending(Long id);

}

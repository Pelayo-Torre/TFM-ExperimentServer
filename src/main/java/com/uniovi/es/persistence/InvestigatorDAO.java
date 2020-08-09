package com.uniovi.es.persistence;

import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Investigator;

public interface InvestigatorDAO extends CrudRepository <Investigator, Long> {
	
	public Investigator findByMail(String mail);

}

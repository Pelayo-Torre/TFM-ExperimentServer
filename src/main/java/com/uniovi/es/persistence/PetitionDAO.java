package com.uniovi.es.persistence;

import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Petition;

public interface PetitionDAO extends CrudRepository <Petition, Long> {

}

package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.Note;

public interface BinnacleDAO extends CrudRepository <Note, Long> {

	@Query("SELECT n FROM Note n WHERE n.experiment.id = ?1")
	List<Note> findNotesByExperiment(Long idExperiment);

}

package com.uniovi.es.persistence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.DemographicData;

public interface DemographicDataDAO extends CrudRepository <DemographicData, Long>{
	
	@Modifying
	@Query("delete from DemographicData d WHERE d.experiment.id = ?1")
	public void deleteDemographicDataByExperiment(Long idExperiment);

}

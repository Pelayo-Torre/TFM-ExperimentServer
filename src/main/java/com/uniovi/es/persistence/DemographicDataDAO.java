package com.uniovi.es.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uniovi.es.model.DemographicData;

public interface DemographicDataDAO extends CrudRepository <DemographicData, Long>{
	
	@Modifying
	@Query("delete from DemographicData d WHERE d.experiment.id = ?1")
	public void deleteDemographicDataByExperiment(Long idExperiment);
	
	@Query("SELECT d FROM DemographicData d WHERE d.experiment.id = ?1")
	public List<DemographicData> getDemographicData(Long idExperiment);
}

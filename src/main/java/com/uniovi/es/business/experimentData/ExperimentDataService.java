package com.uniovi.es.business.experimentData;

import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.ExperimentDataDTO;
import com.uniovi.es.business.experimentData.calculations.CalculateData;

@Service
public interface ExperimentDataService {

	public ExperimentDataDTO getData(CalculateData data);
	
}

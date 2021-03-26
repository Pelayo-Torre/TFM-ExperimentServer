package com.uniovi.es.business.experimentData;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.CalculateDataDTO;

@Service
public interface ExperimentDataService {

	public Map<Integer, Double> getData(CalculateDataDTO data);
	
}

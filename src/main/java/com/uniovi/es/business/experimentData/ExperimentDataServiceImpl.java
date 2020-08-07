package com.uniovi.es.business.experimentData;

import com.uniovi.es.business.dto.ExperimentDataDTO;
import com.uniovi.es.business.experimentData.calculations.CalculateData;

public class ExperimentDataServiceImpl implements ExperimentDataService{

	@Override
	public ExperimentDataDTO getData(CalculateData data) {
		return data.calculate();
	}

	

}

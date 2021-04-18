package com.uniovi.es.business.dto;

import java.util.List;

public class DemographicDataDTO {

	public String name;
	public String type;
	public Long id;
	public Long idExperiment;
	
	public List<DemographicDataValueDTO> values;

	@Override
	public String toString() {
		return "DemographicDataDTO [name=" + name + ", type=" + type + ", id=" + id + ", idExperiment=" + idExperiment
				+ ", values=" + values + "]";
	}
	
}

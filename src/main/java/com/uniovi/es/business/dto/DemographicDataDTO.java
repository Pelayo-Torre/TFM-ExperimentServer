package com.uniovi.es.business.dto;

import java.io.Serializable;
import java.util.List;

public class DemographicDataDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

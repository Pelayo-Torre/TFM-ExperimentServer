package com.uniovi.es.model;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.uniovi.es.model.types.DemographicDataType;

@Entity
public class DemographicData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	
	@Enumerated(EnumType.STRING)
	private DemographicDataType type;
	
	@ManyToOne
	private Experiment experiment;
	
	DemographicData() {}
	
	public DemographicData(String name, DemographicDataType type, Experiment experiment) {
		this.name = name;
		this.type = type;
		this.experiment = experiment;
		Associations.ExperimentDemographicData.link(experiment, this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DemographicDataType getType() {
		return type;
	}

	public void setType(DemographicDataType type) {
		this.type = type;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
		
}

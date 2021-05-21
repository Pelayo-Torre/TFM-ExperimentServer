package com.uniovi.es.model;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.uniovi.es.model.types.DemographicDataType;

@Entity
public class DemographicData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DemographicDataType type;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Experiment experiment;
	
	@OneToMany(mappedBy = "demographicData")
	private Set<DemographicDataDate> demographicDataDate = new HashSet<DemographicDataDate>(); 
	
	@OneToMany(mappedBy = "demographicData")
	private Set<DemographicDataNumber> demographicDataNumber = new HashSet<DemographicDataNumber>(); 
	
	@OneToMany(mappedBy = "demographicData")
	private Set<DemographicDataString> demographicDataString = new HashSet<DemographicDataString>(); 
	
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

	public Set<DemographicDataDate> getDemographicDataDate() {
		return demographicDataDate;
	}

	public void setDemographicDataDate(Set<DemographicDataDate> demographicDataDate) {
		this.demographicDataDate = demographicDataDate;
	}

	public Set<DemographicDataNumber> getDemographicDataNumber() {
		return demographicDataNumber;
	}

	public void setDemographicDataNumber(Set<DemographicDataNumber> demographicDataNumber) {
		this.demographicDataNumber = demographicDataNumber;
	}

	public Set<DemographicDataString> getDemographicDataString() {
		return demographicDataString;
	}

	public void setDemographicDataString(Set<DemographicDataString> demographicDataString) {
		this.demographicDataString = demographicDataString;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "DemographicData [id=" + id + ", name=" + name + ", type=" + type + ", experiment=" + experiment
				+ ", demographicDataDate=" + demographicDataDate + ", demographicDataNumber=" + demographicDataNumber
				+ ", demographicDataString=" + demographicDataString + "]";
	}
	
	
		
}

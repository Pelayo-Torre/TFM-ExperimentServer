package com.uniovi.es.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class DemographicDataNumber implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private DemographicData demographicData;
	
	@OneToOne
	@JoinColumn(nullable = false)
	private User user;
	
	@Column(nullable = false)
	private Double value;
	
	DemographicDataNumber() {}
	
	public DemographicDataNumber(DemographicData demographicData, User user, Double value) {
		this.demographicData = demographicData;
		this.user = user;
		this.value = value;
	}

	public DemographicData getDemographicData() {
		return demographicData;
	}

	public void setDemographicData(DemographicData demographicData) {
		this.demographicData = demographicData;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		return "DemographicDataNumber [id=" + id + ", demographicData=" + demographicData + ", user=" + user
				+ ", value=" + value + "]";
	}
	
	

}

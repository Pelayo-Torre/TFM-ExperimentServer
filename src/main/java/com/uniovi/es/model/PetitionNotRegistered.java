package com.uniovi.es.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class PetitionNotRegistered implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String mail;
	
	private Date shippingDate;
	
	private Boolean manager;
	
	private Long idInvestigatorSend;
	
	@ManyToOne
	private Experiment experiment;
	
	public PetitionNotRegistered(String mail, Experiment experiment) {
		this.mail = mail;
		this.experiment = experiment;
		this.shippingDate = new Date();
		Associations.PetitionNotRegisteredExperiment.link(this, experiment);
	}
	
	PetitionNotRegistered() {}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public Long getId() {
		return id;
	}

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public Boolean getManager() {
		return manager;
	}

	public void setManager(Boolean manager) {
		this.manager = manager;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Long getIdInvestigatorSend() {
		return idInvestigatorSend;
	}

	public void setIdInvestigatorSend(Long idInvestigatorSend) {
		this.idInvestigatorSend = idInvestigatorSend;
	}

	@Override
	public String toString() {
		return "PetitionNotRegistered [id=" + id + ", mail=" + mail + ", shippingDate=" + shippingDate + ", manager="
				+ manager + ", idInvestigatorSend=" + idInvestigatorSend + ", experiment=" + experiment.getId() + "]";
	}
	
	
}

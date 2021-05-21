package com.uniovi.es.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.uniovi.es.model.types.StatusPetition;

@Entity
public class Petition implements Serializable {
		
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusPetition status;
	
	private Date answerDate;
	
	@Column(nullable = false)
	private Date shippingDate;
	
	private String description;
	
	@Column(nullable = false)
	private Boolean manager;
	
	@Column(nullable = false)
	private Boolean creator;
	
	private Long idInvestigatorSend;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Investigator investigator;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Experiment experiment;
	
	Petition() {}
	
	public Petition(Investigator investigator, Experiment experiment) {
		this.status = StatusPetition.PENDING;
		this.creator = false;
		this.manager = false;
		this.shippingDate = new Date();
		Associations.InvestigatorPetitionExperiment.link(investigator, this, experiment);
	}
	
	public Petition(Investigator investigator, Experiment experiment, Boolean manager) {
		this.status = StatusPetition.PENDING;
		this.creator = false;
		if(manager == null) {
			this.manager = false;
		}
		else {
			this.manager = manager;
		}
		this.shippingDate = new Date();
		Associations.InvestigatorPetitionExperiment.link(investigator, this, experiment);
	}

	public StatusPetition getStatus() {
		return status;
	}

	public Date getAnswerDate() {
		return answerDate;
	}

	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public Investigator getInvestigator() {
		return investigator;
	}

	public void setInvestigator(Investigator investigator) {
		this.investigator = investigator;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Boolean getManager() {
		return manager;
	}

	public void setManager(Boolean manager) {
		this.manager = manager;
	}

	public Boolean getCreator() {
		return creator;
	}

	public void setCreator(Boolean creator) {
		this.creator = creator;
	}
	
	public void accept() {
		this.status = StatusPetition.ACCEPTED;
	}
	
	public void reject() {
		this.status = StatusPetition.REJECTED;
	}
	
	public void cancel() {
		this.status = StatusPetition.CANCELLED;
	}
	
	public boolean isAccepted() {
		return this.status.name().equals(StatusPetition.ACCEPTED.name());
	}
	
	public boolean isCancelled() {
		return this.status.name().equals(StatusPetition.CANCELLED.name());
	}
	
	public boolean isPending() {
		return this.status.name().equals(StatusPetition.PENDING.name());
	}
	
	public boolean isRejected() {
		return this.status.name().equals(StatusPetition.REJECTED.name());
	}

	public Long getIdInvestigatorSend() {
		return idInvestigatorSend;
	}

	public void setIdInvestigatorSend(Long idInvestigatorSend) {
		this.idInvestigatorSend = idInvestigatorSend;
	}
			
}

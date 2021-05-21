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

import com.uniovi.es.model.types.StatusRequest;

@Entity
public class Request implements Serializable {
		
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private StatusRequest status;
	
	@Column(nullable = false)
	private Date shippingDate;
	
	private Date answerDate;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Investigator investigator;
	
	Request() {}
	
	public Request(Investigator investigator) {
		this.investigator = investigator;
		this.status = StatusRequest.PENDING;
		this.shippingDate = new Date();
		Associations.InvestigatorRequest.link(investigator, this);
	}

	public StatusRequest getStatus() {
		return status;
	}

	public void setStatus(StatusRequest status) {
		this.status = status;
	}

	public Date getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(Date shippingDate) {
		this.shippingDate = shippingDate;
	}

	public Date getAnswerDate() {
		return answerDate;
	}

	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}

	public Investigator getInvestigator() {
		return investigator;
	}

	public void setInvestigator(Investigator investigator) {
		this.investigator = investigator;
	}

	public Long getId() {
		return id;
	}
	
	public void accept() {
		this.status = StatusRequest.ACCEPTED;
	}
	
	public void reject() {
		this.status = StatusRequest.REJECTED;
	}
	
	public boolean isAccepted() {
		return this.status.name().equals(StatusRequest.ACCEPTED.name());
	}
	
	public boolean isPending() {
		return this.status.name().equals(StatusRequest.PENDING.name());
	}
	
	public boolean isRejected() {
		return this.status.name().equals(StatusRequest.REJECTED.name());
	}

	@Override
	public String toString() {
		return "Request [id=" + id + ", status=" + status + ", shippingDate=" + shippingDate + ", answerDate="
				+ answerDate + ", investigator=" + investigator + "]";
	}
		
}

package com.uniovi.es.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "TUser")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String sessionID;
		
	private String ip;
	private Date timeStamp;
	private String locale;
	private Integer timezone;
	private Integer remotePort;
	private Integer remoteHost;
	
	@ManyToOne
	private Experiment experiment;
	
	User() {}
	
	public User(String sessionID, Experiment experiment) {
		this.sessionID = sessionID;
		Associations.UserExperiment.link(this, experiment);
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Integer getTimezone() {
		return timezone;
	}

	public void setTimezone(Integer timezone) {
		this.timezone = timezone;
	}

	public Integer getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(Integer remotePort) {
		this.remotePort = remotePort;
	}

	public Integer getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(Integer remoteHost) {
		this.remoteHost = remoteHost;
	}

	public Long getId() {
		return id;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	
	
	
}

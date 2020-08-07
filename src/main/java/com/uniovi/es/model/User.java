package com.uniovi.es.model;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@Entity
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(unique = true) 
	private String id;
	
	@Column(unique = true)
	private String sessionID;
	
	private Integer age;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	private Laterality laterality;
	
	@Enumerated(EnumType.STRING)
	private Device device;
	
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
		this(UUID.randomUUID().toString(), sessionID, experiment);
	}
	
	public User(String id, String sessionID, Experiment experiment) {
		this.id = id;
		this.sessionID = sessionID;
		Associations.UserExperiment.link(this, experiment);
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Laterality getLaterality() {
		return laterality;
	}

	public void setLaterality(Laterality laterality) {
		this.laterality = laterality;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
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

	public String getId() {
		return id;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	
	
	
}

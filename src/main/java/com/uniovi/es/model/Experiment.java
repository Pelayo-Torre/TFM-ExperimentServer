package com.uniovi.es.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Experiment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	private String description;
	
	@Enumerated(EnumType.STRING)
	private StatusExperiment status;
	
	@OneToMany(mappedBy = "experiment")
	private Set<Petition> petitions = new HashSet<Petition>();
	
	@OneToMany(mappedBy = "experiment")
	private Set<User> users = new HashSet<User>();
	
	public Experiment() {}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public StatusExperiment getStatus() {
		return status;
	}

	public void setStatus(StatusExperiment status) {
		this.status = status;
	}

	public Set<Petition> getPetitions() {
		return petitions;
	}

	public void setPetitions(Set<Petition> petitions) {
		this.petitions = petitions;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Long getId() {
		return id;
	}
	
	/**
	 * Abre el experimento cambiéndole el estado a ABIERTO
	 */
	public void open() {
		this.status = StatusExperiment.OPEN;
	}

	/**
	 * Cierra el experimento cambiándole el estado a CERRADO
	 */
	public void close() {
		this.status = StatusExperiment.CLOSED;
	}
	
	/**
	 * Elimina el experimento cabiándole el estado a ELIMINADO
	 */
	public void delete() {
		this.status = StatusExperiment.DELETED;
	}
	
}

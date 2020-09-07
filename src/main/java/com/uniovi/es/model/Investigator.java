package com.uniovi.es.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.uniovi.es.model.types.Role;

@Entity
public class Investigator implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	private String surname;
	
	@Column(unique = true) 
	private String mail;
	
	@Column(unique = true) 
	private String username;
	
	private String password;
	
	private Date registrationDate;
	
	@OneToMany(mappedBy = "investigator")
	private Set<Petition> petitions = new HashSet<Petition>();
	
	@Enumerated(EnumType.STRING)
	private Role role;
	
	public Investigator(String mail, String username) {
		this.mail = mail;
		this.username = username;
		this.registrationDate = new Date();
	}
	
	Investigator() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Long getId() {
		return id;
	}

	public Set<Petition> getPetitions() {
		return petitions;
	}

	public void setPetitions(Set<Petition> petitions) {
		this.petitions = petitions;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
}

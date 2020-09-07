package com.uniovi.es.model.types;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class DemographicData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Date birthdate;
	
	@Enumerated(EnumType.STRING)
	private Laterality laterality;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	private Long idDevice;
	
	DemographicData() {}
	
	public DemographicData(Date birthdate, Laterality laterality, Gender gender, Long idDevice) {
		this.birthdate = birthdate;
		this.laterality = laterality;
		this.gender = gender;
		this.idDevice = idDevice;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public Laterality getLaterality() {
		return laterality;
	}

	public void setLaterality(Laterality laterality) {
		this.laterality = laterality;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Long getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Long idDevice) {
		this.idDevice = idDevice;
	}

	@Override
	public String toString() {
		return "DemographicData [birthdate=" + birthdate + ", laterality=" + laterality + ", gender=" + gender
				+ ", idDevice=" + idDevice + "]";
	}
		
}

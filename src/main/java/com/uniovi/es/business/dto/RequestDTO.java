package com.uniovi.es.business.dto;

import java.util.Date;

public class RequestDTO {
	
	public Long id;
	public Date shippingDate;
	public Date answerDate;
	public String status;
	
	//Datos del investigador asociado a la solicitud
	public Long idInvestigator;
	public String name;
	public String surname;
	public String mail;
	
	
	@Override
	public String toString() {
		return "RequestDTO [id=" + id + ", shippingDate=" + shippingDate + ", answerDate=" + answerDate + ", status="
				+ status + ", idInvestigator=" + idInvestigator + "]";
	}
	
}

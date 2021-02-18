package com.uniovi.es.business.administration.commands;

import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.model.Request;

public interface RequestCommand {

	public void execute(Request request) throws AdministrationException;
	
}

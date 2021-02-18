package com.uniovi.es.business.administration;

import com.uniovi.es.business.administration.commands.RequestCommand;
import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.model.Request;

public class ActionManager {

	private Request request;
	
	public ActionManager(Request request) {
		this.request = request;
	}
	
	public void execute(RequestCommand command) throws AdministrationException {
		command.execute(this.request);
	}

	public void setRequest(Request request) {
		this.request = request;
	}
}

package com.uniovi.es.business.administration;

import com.uniovi.es.business.administration.commands.RequestCommand;
import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.model.Request;

public class ActionManager {
	
	public ActionManager() {}
	
	public void execute(RequestCommand command, Request request) throws AdministrationException {
		command.execute(request);
	}

}

package com.uniovi.es.business.petition;

import com.uniovi.es.business.petition.commands.PetitionCommand;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.Petition;

public class ActionManager {
	
	public ActionManager() {}
	
	public void execute(PetitionCommand command, Petition petition) throws PetitionException {
		command.execute(petition);
	}
	

}

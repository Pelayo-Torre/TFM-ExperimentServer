package com.uniovi.es.business.petition;

import com.uniovi.es.business.petition.commands.PetitionCommand;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.Petition;

public class ActionManager {
	
	private Petition petition;
	
	public ActionManager(Petition petition) {
		this.petition = petition;
	}
	
	public void execute(PetitionCommand command) throws PetitionException {
		command.execute(petition);
	}
	
	public void setPetition(Petition petition) {
		this.petition = petition;
	}

}

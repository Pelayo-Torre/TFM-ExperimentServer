package com.uniovi.es.business.petition.commands;

import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.model.Petition;

public interface PetitionCommand {
	
	public void execute(Petition petition) throws PetitionException;

}

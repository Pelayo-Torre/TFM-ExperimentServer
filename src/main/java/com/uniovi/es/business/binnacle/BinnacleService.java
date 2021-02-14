package com.uniovi.es.business.binnacle;

import java.util.List;

import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.exceptions.NoteException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.utils.Identifier;

public interface BinnacleService {
	
	public void registerNote(NoteDTO dto) throws NoteException, ExperimentException, ForbiddenException;
	
	public void updateNote(NoteDTO dto) throws NoteException, ForbiddenException;
	
	public List<NoteDTO> getNotesByExperiment(Long idExperiment) throws ExperimentException, NoteException, ForbiddenException;
	
	public void deleteNote(Identifier id) throws NoteException, ForbiddenException;
	
	public NoteDTO detail(Long id) throws NoteException, ForbiddenException;

}

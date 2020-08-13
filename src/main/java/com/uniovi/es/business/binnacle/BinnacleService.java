package com.uniovi.es.business.binnacle;

import java.util.List;

import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.exceptions.BinnacleException;
import com.uniovi.es.exceptions.ExperimentException;

public interface BinnacleService {
	
	public void registerNote(NoteDTO dto) throws BinnacleException, ExperimentException;
	
	public void updateNote(NoteDTO dto) throws BinnacleException ;
	
	public List<NoteDTO> getNotesByExperiment(Long idExperiment) throws ExperimentException ;
	
	public void deleteNote(Long id) throws BinnacleException ;
	
	public NoteDTO detail(Long id) throws BinnacleException ;

}

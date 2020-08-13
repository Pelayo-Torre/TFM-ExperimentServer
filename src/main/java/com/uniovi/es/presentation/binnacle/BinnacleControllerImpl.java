package com.uniovi.es.presentation.binnacle;

import java.util.List;

import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.exceptions.BinnacleException;
import com.uniovi.es.exceptions.ExperimentException;

public class BinnacleControllerImpl implements BinnacleController{

	@Override
	public void registerNote(NoteDTO dto) throws BinnacleException, ExperimentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateNote(NoteDTO dto) throws BinnacleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<NoteDTO> getNotesByExperiment(Long idExperiment) throws ExperimentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteNote(Long id) throws BinnacleException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NoteDTO detail(Long id) throws BinnacleException {
		// TODO Auto-generated method stub
		return null;
	}

}

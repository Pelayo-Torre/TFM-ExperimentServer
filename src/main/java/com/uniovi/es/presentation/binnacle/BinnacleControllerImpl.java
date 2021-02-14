package com.uniovi.es.presentation.binnacle;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.uniovi.es.business.binnacle.BinnacleService;
import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.exceptions.NoteException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.utils.Identifier;

@RestController
@RequestMapping("/binnacle")
@CrossOrigin(origins = "http://localhost:4200")
public class BinnacleControllerImpl implements BinnacleController{
	
	private static final Logger logger = LoggerFactory.getLogger(BinnacleControllerImpl.class);
	
	@Autowired
	private BinnacleService binnacleService;

	@Override
	@RequestMapping(value = "/register/note", method = RequestMethod.POST)
	public void registerNote(@RequestBody NoteDTO dto) throws NoteException, ExperimentException, ForbiddenException {
		logger.info("[INICIO] BINNACLE CONTROLLER -- register note");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + dto);
		
		binnacleService.registerNote(dto);
		
		logger.info("[FINAL] BINNACLE CONTROLLER -- register note");
	}

	@Override
	@RequestMapping(value = "/update/note", method = RequestMethod.PUT)
	public void updateNote(@RequestBody NoteDTO dto) throws NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE CONTROLLER -- update note");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + dto);
		
		binnacleService.updateNote(dto);
		
		logger.info("[FINAL] BINNACLE CONTROLLER -- update note");
	}

	@Override
	@RequestMapping(value = "/notes/experiment/{idExperiment}", method = RequestMethod.GET)
	public List<NoteDTO> getNotesByExperiment(@PathVariable Long idExperiment) throws ExperimentException, NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE CONTROLLER -- notes by experiment");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + idExperiment);
		
		List<NoteDTO> notes = binnacleService.getNotesByExperiment(idExperiment);
		
		logger.info("[FINAL] BINNACLE CONTROLLER -- notes by experiment");
		
		return notes;
	}

	@Override
	@RequestMapping(value = "/delete/note", method = RequestMethod.PUT)
	public void deleteNote(@RequestBody Identifier id) throws NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE CONTROLLER -- delete note");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);
		
		binnacleService.deleteNote(id);
		
		logger.info("[FINAL] BINNACLE CONTROLLER -- delete note");
	}

	@Override
	@RequestMapping(value = "/detail/note/{id}", method = RequestMethod.GET)
	public NoteDTO detail(@PathVariable Long id) throws NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE CONTROLLER -- detail note");
		logger.info("\t \t PARÁMETROS DE ENTRADA: " + id);
		
		NoteDTO note = binnacleService.detail(id);
		
		logger.info("[FINAL] BINNACLE CONTROLLER -- detail note");
		
		return note;
	}

}

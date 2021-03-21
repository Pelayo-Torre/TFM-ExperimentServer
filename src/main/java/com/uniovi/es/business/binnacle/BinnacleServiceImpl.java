package com.uniovi.es.business.binnacle;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.authentication.UserInSession;
import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.validators.NoteValidator;
import com.uniovi.es.exceptions.NoteException;
import com.uniovi.es.model.Associations;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Note;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.persistence.BinnacleDAO;
import com.uniovi.es.persistence.ExperimentDAO;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.utils.Identifier;

@Service
public class BinnacleServiceImpl implements BinnacleService{
	
	private static final Logger logger = LoggerFactory.getLogger(BinnacleServiceImpl.class);
	
	@Autowired
	private BinnacleDAO binnacleDAO;
	
	@Autowired
	private ExperimentDAO experimentDAO;
	
	@Autowired
	private NoteValidator noteValidator;
	
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private UserInSession userInSession;

	@Override
	public void registerNote(NoteDTO dto) throws NoteException, ExperimentException, ForbiddenException {
		logger.info("[INICIO] BINNACLE SERVICE -- register note");
		
		if(dto.idExperiment == null) {
			logger.error("[ERROR -- 404] - El experimento asociado a la nota es un campo obligatorio.");
			throw new NoteException("404");
		}
		
		logger.info("\t \t Obteniendo el experimento a partir del ID: " + dto.idExperiment);
		Optional<Experiment> optional = experimentDAO.findById(dto.idExperiment);
		Experiment experiment = getExperiment(optional);
		
		
		isInvestigatorAssociatedExperiment(dto.idExperiment);
		noteValidator.validate(dto);
		
		Note note = new Note(experiment);
		DtoAssembler.fillData(note, dto);
		
		logger.info("\t \t Registrando la nota en base de datos.");
		binnacleDAO.save(note);
		
		logger.info("[FINAL] BINNACLE SERVICE -- register note");
	}

	@Override
	public void updateNote(NoteDTO dto) throws NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE SERVICE -- update note");
		
		if(dto.id == null) {
			logger.error("[ERROR - 400] -- La nota especificada no se encuentra registrada en el sistema");
			throw new NoteException("400");
		}
		
		logger.info("\t \t Obteniendo la nota a partir del ID: " + dto.id);
		Optional<Note> optional = binnacleDAO.findById(dto.id);
		Note note = getNote(optional);
		
		isInvestigatorAssociatedExperiment(note.getExperiment().getId());
		noteValidator.validate(dto);
		
		DtoAssembler.fillData(note, dto);
		
		logger.info("\t \t Registrando la nota en base de datos.");
		binnacleDAO.save(note);
		
		logger.info("[FINAL] BINNACLE SERVICE -- update note");
	}

	@Override
	public List<NoteDTO> getNotesByExperiment(Long idExperiment) throws ExperimentException, NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE SERVICE -- notes by experiment");
		
		isInvestigatorAssociatedExperiment(idExperiment);
		List<Note> notes = binnacleDAO.findNotesByExperiment(idExperiment);
		
		logger.info("[FINAL] BINNACLE SERVICE -- notes by experiment");
		return DtoAssembler.toListNotes(notes);
	}

	@Override
	public void deleteNote(Identifier id) throws NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE SERVICE -- delete note");
		
		if(id == null || id.getId() == null) {
			logger.error("[ERROR - 400] -- La nota especificada no se encuentra registrada en el sistema");
			throw new NoteException("400");
		}

		logger.info("\t \t Obteniendo la nota a partir del ID: " + id.getId());
		Optional<Note> optional = binnacleDAO.findById(id.getId());
		Note note = getNote(optional);
		
		isInvestigatorAssociatedExperiment(note.getExperiment().getId());
		
		logger.info("\t \t Desasociando la nota del experimento.");
		Associations.NoteExperiment.unlink(note, note.getExperiment());
		
		binnacleDAO.delete(note);
		
		logger.info("[FINAL] BINNACLE SERVICE -- delete note");
	}

	@Override
	public NoteDTO detail(Long id) throws NoteException, ForbiddenException {
		logger.info("[INICIO] BINNACLE SERVICE -- detail note");
		
		if(id == null) {
			logger.error("[ERROR - 400] -- La nota especificada no se encuentra registrada en el sistema");
			throw new NoteException("400");
		}

		logger.info("\t \t Obteniendo la nota a partir del ID: " + id);
		Optional<Note> optional = binnacleDAO.findById(id);
		Note note = getNote(optional);
		
		isInvestigatorAssociatedExperiment(note.getExperiment().getId());
		
		logger.info("[FINAL] BINNACLE SERVICE -- detail note");
		return DtoAssembler.toDto(note);
	}
	
	/**
	 * Validamos que el investigador tiene acceso al experimento para gestionar la bitácora
	 * @param idExperiment
	 * @throws NoteException
	 * @throws ForbiddenException 
	 */
	private void isInvestigatorAssociatedExperiment(Long idExperiment) throws NoteException, ForbiddenException{
		Investigator investigator = userInSession.getInvestigator();
		if(petitionDAO.isInvestigatorAssociatedExperiment(investigator.getId(), idExperiment) == null) {
			logger.error("[ERROR -- 403] - El investigador que quiere gestionar la bitácora no se encuentra asociado al experimento.");
			throw new ForbiddenException("403");
		}
	}
	
	/**
	 * Devuelve el experimento a partir del optional que se pasa como parámetro
	 * @param optional, parámetro de entrada
	 * @return experimento encontrado
	 * @throws ExperimentException, en caso de que el experimento no exista en base de datos.
	 */
	private Experiment getExperiment(Optional<Experiment> optional) throws ExperimentException {
		Experiment experiment = null;
		if(optional.isPresent()) {
			experiment = optional.get();
		}
		else {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}
		return experiment;
	}

	/**
	 * Devuelve la nota a partir del optional que se pasa como parámetro
	 * @param optional, parámetro de entrada
	 * @return nota encontrada
	 * @throws NoteException en caso de que la nota no exista en base de datos
	 */
	private Note getNote(Optional<Note> optional) throws NoteException{
		Note note = null;
		if(optional.isPresent()) {
			note = optional.get();
		}
		else {
			logger.error("[ERROR - 400] -- La nota especificada no se encuentra registrada en el sistema");
			throw new NoteException("400");
		}
		return note;
	}

	
}

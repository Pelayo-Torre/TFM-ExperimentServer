package com.uniovi.es.presentation.binnacle;

import java.util.List;

import com.uniovi.es.business.dto.NoteDTO;
import com.uniovi.es.exceptions.NoteException;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.utils.Identifier;

public interface BinnacleController {
	
	/**
	 * Recibe una petición para registrar un nota en la bitácora de un experimento
	 * @param dto, datos de entrada de la nota
	 * @throws NoteException, en caso de que los datos de la nota sean incorrectos
	 * @throws ExperimentException en caso de que el experimento al que se quiere asociar la nota no exista
	 */
	public void registerNote(NoteDTO dto) throws NoteException, ExperimentException;
	
	/**
	 * Recibe una petición para realizar la edición de los datos de una nota
	 * @param dto, parámetros de entrada
	 * @throws NoteException en caso de que los datos de la nota sean incorrectos
	 */
	public void updateNote(NoteDTO dto) throws NoteException;
	
	/**
	 * Recibe una petición para devolver la lista de notas de la bitácora de un experimento
	 * @param idExperiment, el identificador del experimento del que se quiere saber las notas
	 * @return lista de notas
	 * @throws ExperimentException en caso de que el experimento no exista
	 */
	public List<NoteDTO> getNotesByExperiment(Long idExperiment) throws ExperimentException, NoteException;
	
	/**
	 * Recibe una petición para realizar la eliminación de una nota a partir de su identificador
	 * @param id identificador de la nota
	 * @throws NoteException en caso de que la nota que se pasa como parámetro no exista en el sistema
	 */
	public void deleteNote(Identifier id) throws NoteException;
	
	/**
	 * Recibe una petición para obtener el detalle de los datos de una nota a partir de su identificador
	 * @param id identificador de la nota
	 * @return detalle de la nota
	 * @throws NoteException en caso de que la nota que se pasa como parámetro no exista en el sistema
	 */
	public NoteDTO detail(Long id) throws NoteException;

}

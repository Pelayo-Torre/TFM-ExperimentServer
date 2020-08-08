package com.uniovi.es.presentation.experiment;


import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.exceptions.ExperimentException;

public interface ExperimentController {
	
	/**
	 * Recibe una petición para realizar el registro de un experimento
	 * @param dto, datos del experimento a registrar
	 */
	public void registerExperiment(ExperimentDTO dto);
	
	/**
	 * Recibe una petición para obtener el experimento a partir del id
	 * @param id, paramétro de entrada
	 * @return los datos del experimento
	 */
	public ExperimentDTO getExperiment(Long id);
	
	/**
	 * Recibe una petición para realizar la edición de los datos de un experimento
	 * @param dto, los nuevos datos del experimento
	 * @throws ExperimentException 
	 */
	public void editExperiment(ExperimentDTO dto) throws ExperimentException;
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a ABIERTO
	 * @param id, identificador del experimento que se desea abrir
	 * @throws ExperimentException 
	 */
	public void openExperiment(Long id) throws ExperimentException;
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a CERRADO
	 * @param id, identificador del experimento que se desea abrir
	 * @throws ExperimentException 
	 */
	public void closeExperiment(Long id) throws ExperimentException;
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a BORRADO
	 * @param id, identificador del experimento que se desea abrir
	 * @throws ExperimentException 
	 */
	public void deleteExperiment(Long id) throws ExperimentException;
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a ABIERTO
	 * @param id, identificador del experimento que se desea abrir
	 * @throws ExperimentException 
	 */
	public void reOpenExperiment(Long id) throws ExperimentException;

}

package com.uniovi.es.presentation.experiment;

import com.uniovi.es.business.dto.ExperimentDTO;

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
	public ExperimentDTO getExperiment(String id);
	
	/**
	 * Recibe una petición para realizar la edición de los datos de un experimento
	 * @param dto, los nuevos datos del experimento
	 */
	public void editExperiment(ExperimentDTO dto);
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a ABIERTO
	 * @param id, identificador del experimento que se desea abrir
	 */
	public void openExperiment(String id);
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a CERRADO
	 * @param id, identificador del experimento que se desea abrir
	 */
	public void closeExperiment(String id);
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a BORRADO
	 * @param id, identificador del experimento que se desea abrir
	 */
	public void deleteExperiment(String id);
	

}

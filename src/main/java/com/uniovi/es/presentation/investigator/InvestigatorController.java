package com.uniovi.es.presentation.investigator;


import java.util.List;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.exceptions.InvestigatorException;


public interface InvestigatorController {
	
	/**
	 * Punto de entrada para registrar un nuevo investigador
	 * @param dto, contiene los datos del investigador que va a ser registrado
	 */
	public void registerInvestigator(InvestigatorDTO dto) throws InvestigatorException;
	
	/**
	 * Punto de entrada para obtener los datos de un investigador a partir de su identificador
	 * @param id, identificador del investigador
	 * @return data, los datos del investigador
	 * @throws InvestigatorException 
	 */
	public InvestigatorDTO getInvestigator(Long id) throws InvestigatorException;

	/**
	 * Punto de entrada para actualizar los datos de un investigador
	 * @param dto, los datos nuevos del investigador 
	 */
	public void updateInvestigator(InvestigatorDTO dto) throws InvestigatorException;

	
	/**
	 * Recibe una petición para realizar la consulta de la lista de experimentos de un investigador
	 * @param idInvestigator, el identificador del investigador
	 * @return lista de experimentos asociados
	 */
	public List<ExperimentDTO> getExperimentsByInvestigator(Long idInvestigator);
	
}

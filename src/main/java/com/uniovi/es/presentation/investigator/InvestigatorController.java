package com.uniovi.es.presentation.investigator;


import com.uniovi.es.business.dto.InvestigatorDTO;


public interface InvestigatorController {
	
	/**
	 * Punto de entrada para registrar un nuevo investigador
	 * @param dto, contiene los datos del investigador que va a ser registrado
	 */
	public void registerInvestigator(InvestigatorDTO dto);
	
	/**
	 * Punto de entrada para obtener los datos de un investigador a partir de su identificador
	 * @param id, identificador del investigador
	 * @return data, los datos del investigador
	 */
	public InvestigatorDTO getInvestigator(Long id);

	/**
	 * Punto de entrada para actualizar los datos de un investigador
	 * @param dto, los datos nuevos del investigador 
	 */
	public void updateInvestigator(InvestigatorDTO dto);
	
}

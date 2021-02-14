package com.uniovi.es.presentation.petition;

import java.util.List;

import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.exceptions.PetitionException;
import com.uniovi.es.utils.Identifier;

public interface PetitionController {
	
	/**
	 * Punto de entrada para realizar el registro de una petición en base de datos
	 * @param dto, parámetros de entrada 
	 * @throws PetitionException, en caso de que la petición contenga datos incorrectos
	 * @throws ExperimentException, en caso de que el experimento asociado a la peticición no exista
	 * @throws InvestigatorException, en caso de que el investigador asociado a la petición no exista
	 */
	public void register(PetitionDTO dto) throws PetitionException, ExperimentException, InvestigatorException;
	
	/**
	 * Punto de entrada para marcar una petición como aceptada
	 * @param id, identificador de la petición a aceptar
	 * @throws PetitionException, en caso de que la petición no exista en la base de datos
	 */
	public void accept(Identifier id) throws PetitionException, ForbiddenException;
	
	/**
	 * Punto de entrada para marcar una petición como rechazada
	 * @param id, identificador de la petición a rechazar
	 * @throws PetitionException, en caso de que la petición no exista en la base de datos
	 */
	public void reject(Identifier id) throws PetitionException, ForbiddenException;
	
	/**
	 * Punto de entrada para marcar una petición como cancelada
	 * @param id, identificador de la petición a cancelar
	 * @throws PetitionException, en caso de que la petición no exista en la base de datos
	 */
	public void cancel(Identifier id) throws PetitionException, ForbiddenException;
	
	/**
	 * Punto de entrada para obtener el detalle de una petición
	 * @param id, identificador de la petición
	 * @return la petición con sus datos
	 * @throws PetitionException, en caso de que la petición pasada por parámetro no exista en base de datos
	 * @throws InvestigatorException 
	 */
	public PetitionDTO getDetail(Long id) throws PetitionException, InvestigatorException;

	/**
	 * Punto de entrada para obtener la lista de peticiones recibidas del usuario en sesión
	 * @return lista de peticiones recibidas
	 */
	public List<PetitionDTO> getPetitionReceived();

	/**
	 * Punto de entrada para obtener la lista de peticiones enviadas por el usuario que se encuentra en sesión
	 * @return lista de peticiones enviadas
	 */
	public List<PetitionDTO> getPetitionSent();

}

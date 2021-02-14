package com.uniovi.es.presentation.investigator;


import java.util.List;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.exceptions.ForbiddenException;
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
	 * @throws InvestigatorException, en caso de que el investigador no exista en base de datos
	 */
	public InvestigatorDTO getInvestigator(Long id) throws InvestigatorException;

	/**
	 * Punto de entrada para actualizar los datos de un investigador
	 * @param dto, los datos nuevos del investigador 
	 */
	public void updateInvestigator(InvestigatorDTO dto) throws InvestigatorException, ForbiddenException;

	
	/**
	 * Recibe una petición para realizar la consulta de la lista de experimentos de un investigador que tiene aceptados
	 * @param idInvestigator, el identificador del investigador
	 * @return lista de experimentos asociados en estado ACEPTADO
	 * @throws InvestigatorException  en caso de que el investigador no exista en base de datos
	 */
	public List<ExperimentDTO> getExperimentsAcceptedByIdInvestigator(Long idInvestigator) throws InvestigatorException, ForbiddenException;
	
	/**
	 * Recibe una petición para realizar la consulta de la lista de petiticones pendientes de responder de un investigador
	 * @param idInvestigator, el identificador del investigador
	 * @return lista de peticiones asociados en estado PENDIENTE
	 * @throws InvestigatorException  en caso de que el investigador no exista en base de datos
	 */
	public List<PetitionDTO> getPetitionsPendingByIdInvestigator(Long idInvestigator) throws InvestigatorException, ForbiddenException;
	
	/**
	 * Recibe una petiticón para devolver la lista de investigadores registrados en el sistema
	 * @return la lista de investigadores
	 */
	public List<InvestigatorDTO> getListInvestigators();

	/**
	 * Recibe una petición para devolver el investigador a partir de su mail
	 * @param mail , parámetro de entrada por el que se filtra
	 * @return Investigador encontrado
	 * @throws InvestigatorException si el parámetro de entrada el nulo
	 */
	public InvestigatorDTO getInvestigatorByMail(String mail) throws InvestigatorException ;
	
	/**
	 * Recibe una petición para devolver el investigador a partir de su nombre de usuario
	 * @param username , parámetro de entrada por el que se filtra
	 * @return Investigador encontrado
	 * @throws InvestigatorException  si el parámetro de entrada es nulo
	 */
	public InvestigatorDTO getInvestigatorByUsername(String username) throws InvestigatorException ;

	/**
	 * Devuelve al investigador que se encuentra autenticado en sesión
	 * @return el investigador en sesión
	 * @throws InvestigatorException
	 */
	public InvestigatorDTO validateNameOfAccount() throws InvestigatorException;
	
}

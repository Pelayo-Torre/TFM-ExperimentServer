package com.uniovi.es.presentation.administration;

import java.util.List;

import com.uniovi.es.business.dto.RequestDTO;
import com.uniovi.es.exceptions.AdministrationException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.utils.Identifier;

public interface AdministrationController {

	/**
	 * Punto de entrada para registrar una solicitud de aprobación de cuenta de un investigador
	 * @param dto, datos de la solicitud
	 * @throws AdministrationException
	 */
	public void register(RequestDTO dto) throws AdministrationException, ForbiddenException;
	
	/**
	 * Punto de entrada para aceptar una solicitud de aprobación de cuenta de un investigador
	 * @param id identificador de la solicitud a aceptar	
	 * @throws AdministrationException 
	 * @throws ForbiddenException, en caso de que el investigador que la acepte no tenga permisos para hacerlo
	 */
	public void accept(Identifier id) throws AdministrationException, ForbiddenException;
	
	/**
	 * Punto de entrada para rechazar una solicitud de aprobación de cuenta de un investigador
	 * @param id identificador de la solicitud a rechazar	
	 * @throws AdministrationException 
	 * @throws ForbiddenException, en caso de que el investigador que la acepte no tenga permisos para hacerlo
	 */
	public void reject(Identifier id) throws AdministrationException, ForbiddenException;
	
	/**
	 * Punto de entrada para obtener la lista de solicitudes de aprobación de cuenta en estado PENDIENTE
	 * @return la lista de solictudes pendientes
	 * @throws AdministrationException
	 * @throws ForbiddenException en caso de que la petición sea invocada por un investigador sin permisos
	 */
	public List<RequestDTO> getPendingRequests() throws AdministrationException, ForbiddenException;
	
	/**
	 * Punto de entrada para obtener el detalle de una solicitud de aprobación de cuenta
	 * @param id identificador de la solicitud a rechazar	
	 * @return el detalle de un experimento
	 * @throws AdministrationException
	 */
	public RequestDTO getDetail(Long id) throws AdministrationException;
	
}

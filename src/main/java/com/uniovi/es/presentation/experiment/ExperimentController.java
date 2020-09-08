package com.uniovi.es.presentation.experiment;


import java.util.List;

import com.uniovi.es.business.dto.DeviceDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.utils.Identifier;

public interface ExperimentController {
	
	/**
	 * Recibe una petición para realizar el registro de un experimento
	 * @param dto, datos del experimento a registrar
	 * @throws ExperimentException 
	 * @throws InvestigatorException 
	 */
	public void registerExperiment(ExperimentDTO dto) throws ExperimentException, InvestigatorException;
	
	/**
	 * Recibe una petición para obtener el experimento a partir del id
	 * @param id, paramétro de entrada
	 * @return los datos del experimento
	 * @throws ExperimentException 
	 */
	public ExperimentDTO getExperiment(Long id) throws ExperimentException;
	
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
	public void openExperiment(Identifier id) throws ExperimentException;
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a CERRADO
	 * @param id, identificador del experimento que se desea abrir
	 * @throws ExperimentException 
	 */
	public void closeExperiment(Identifier id) throws ExperimentException;
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a BORRADO
	 * @param id, identificador del experimento que se desea abrir
	 * @throws ExperimentException 
	 */
	public void deleteExperiment(Identifier id) throws ExperimentException;
	
	/**
	 * Recibe una petición para cambiarle el estado del experimento que se pasa
	 * como parámetro. 
	 * El ESTADO del experimento deberá cambiar a ABIERTO
	 * @param id, identificador del experimento que se desea abrir
	 * @throws ExperimentException 
	 */
	public void reOpenExperiment(Identifier id) throws ExperimentException;
	
	/**
	 * Recibe una petición para obtener la lista de investigadores asociados a un experimento
	 * @param id, identificador del experimento del que se quiere obtener los investigadores
	 * @return lista de investigadores asociados al experimento
	 * @throws ExperimentException, en caso de que el identificador no exista en base de datos
	 */
	public List<InvestigatorDTO> getInvestigatorsOfExperiment(Long id) throws ExperimentException; 
	
	/**
	 * Recibe una petición para obtener la lista de experimentos del sistema
	 * @return la lista de experimentos
	 */
	public List<ExperimentDTO> getListExperiments();
	
	/**
	 * Devuelve la lista de dispositivos existentes en la aplicación
	 * @return lista de dispositivos existentes en base de datos
	 */
	public List<DeviceDTO> getListDevices();

	/**
	 * Devuelve la lista de investigadores que no se encuentran asociados a un experimento
	 * y que no tienen solicitud pendiente de contestar
	 * @param id identificador del experimento
	 * @return lista de investigadores no asociados
	 * @throws ExperimentException en caso de que el experimento no exista en el sistema
	 */
	public List<InvestigatorDTO> getInvestigatorsNotAssociatedAnExperiment(Long id) throws ExperimentException;

}

package com.uniovi.es.business.experiment;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.experiment.commands.Close;
import com.uniovi.es.business.experiment.commands.Delete;
import com.uniovi.es.business.experiment.commands.Open;
import com.uniovi.es.business.experiment.commands.ReOpen;
import com.uniovi.es.business.validators.ExperimentValidator;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.persistence.ExperimentDAO;

@Service
public class ExperimentServiceImpl implements ExperimentService{

	private static final Logger logger = LoggerFactory.getLogger(ExperimentServiceImpl.class);
	private ActionManager actionManager = new ActionManager(null);
	private ExperimentValidator experimentValidator = new ExperimentValidator();
	
	@Autowired
	private ExperimentDAO experimentDAO;
	
	@Override
	public void update(ExperimentDTO dto) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- update experiment");

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + dto.id);
		Optional<Experiment> optional = experimentDAO.findById(dto.id);
		Experiment experiment = getExperiment(optional);
		
		experimentValidator.validate(dto);
		DtoAssembler.fillData(experiment, dto);
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- update experiment");
	}
	
	@Override
	public void open(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- open experiment");

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id);
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ABIERTO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new Open());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- open experiment");
	}


	@Override
	public void reOpen(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- reOpen experiment");

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id);
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ABIERTO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new ReOpen());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- reOpen experiment");
	}

	@Override
	public void close(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- close experiment");

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id);
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a CERRADO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new Close());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- close experiment");
	}

	@Override
	public void delete(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- delete experiment");

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id);
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ELIMINADO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new Delete());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- delete experiment");
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


}

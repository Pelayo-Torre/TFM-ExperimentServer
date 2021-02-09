package com.uniovi.es.business.experiment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.DeviceDTO;
import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.experiment.commands.Close;
import com.uniovi.es.business.experiment.commands.Delete;
import com.uniovi.es.business.experiment.commands.Open;
import com.uniovi.es.business.experiment.commands.ReOpen;
import com.uniovi.es.business.validators.ExperimentValidator;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.types.DemographicData;
import com.uniovi.es.model.types.Device;
import com.uniovi.es.model.types.Gender;
import com.uniovi.es.model.types.Laterality;
import com.uniovi.es.model.types.StatusPetition;
import com.uniovi.es.persistence.DeviceDAO;
import com.uniovi.es.persistence.ExperimentDAO;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.utils.Identifier;

@Service
public class ExperimentServiceImpl implements ExperimentService{

	private static final Logger logger = LoggerFactory.getLogger(ExperimentServiceImpl.class);
	
	private ActionManager actionManager = new ActionManager(null);
	
	@Autowired
	private ExperimentValidator experimentValidator;
	
	@Autowired
	private ExperimentDAO experimentDAO;
	
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private DeviceDAO deviceDAO;
	
	//@PostConstruct
	public void init() {
		Device d = new Device("MOUSE");
		Device d1 = new Device("TOUCHPAD");
		
		deviceDAO.save(d);
		deviceDAO.save(d1);
	}
		
	@Override
	public void register(ExperimentDTO dto) throws ExperimentException, InvestigatorException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- register experiment");
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + dto.idInvestigator);
		Optional<Investigator> optional = investigatorDAO.findById(dto.idInvestigator);
		Investigator investigator = getInvestigator(optional);
		
		experimentValidator.validate(dto);
		experimentValidator.validateDemographicData(dto);
		
		Experiment experiment = new Experiment();
		DtoAssembler.fillData(experiment, dto);
		
		//Creación de los datos demográficos
		DemographicData demographicData = new DemographicData(dto.birthDate, Laterality.valueOf(dto.laterality), 
				Gender.valueOf(dto.gender), dto.idDevice);
		
		experiment.setDemographicData(demographicData);
		
		//Creación de la petición
		Petition petition = new Petition(investigator, experiment);
		petition.setAnswerDate(new Date());
		petition.setCreator(true);
		petition.setManager(true);
		petition.accept();
		
		logger.info("\t \t Registrando el experimento en base de datos");
		experimentDAO.save(experiment);
		
		investigatorDAO.save(investigator);
		
		logger.info("\t \t Registrando la petitición en base de datos");
		petitionDAO.save(petition);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- register experiment");
	}

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
	public ExperimentDTO getDetail(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- detail experiment");

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id);
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Obteniendo los datos del creador del experimento con ID: " + id);
		Investigator investigator = investigatorDAO.findCreatorOfExperiment(id);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- detail experiment");
		
		return DtoAssembler.toDTO(experiment, investigator);
	}
	
	@Override
	public void open(Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- open experiment");
		
		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ABIERTO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new Open());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- open experiment");
	}


	@Override
	public void reOpen(Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- reOpen experiment");
		
		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ABIERTO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new ReOpen());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- reOpen experiment");
	}

	@Override
	public void close(Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- close experiment");
		
		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}

		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a CERRADO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new Close());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- close experiment");
	}

	@Override
	public void delete(Identifier id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- delete experiment");

		if(id == null) {
			logger.error("[ERROR - 100] -- El experimento especificado no se encuentra registrado en el sistema");
			throw new ExperimentException("100");
		}
		
		logger.info("\t \t Obteniendo el experimento a partir del ID: " + id);
		Optional<Experiment> optional = experimentDAO.findById(id.getId());
		Experiment experiment = getExperiment(optional);
		
		logger.info("\t \t Cambiando el estado del experimento a ELIMINADO");
		actionManager.setExperiment(experiment);
		actionManager.execute(new Delete());
		
		logger.info("\t \t Actualizando cambios en base de datos");
		experimentDAO.save(experiment);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- delete experiment");
	}
	
	@Override
	public List<InvestigatorDTO> getInvestigatorsOfExperiment(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- getInvestigatorsOfExperiment");
		
		List<Petition> list = petitionDAO.findByIdExperiment(id, StatusPetition.ACCEPTED);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- getInvestigatorsOfExperiment");
		return DtoAssembler.toListInvestigatorsOfPetitions(list);
	}
	
	@Override
	public List<ExperimentDTO> getExperiments() {
		logger.info("[INICIO] EXPERIMENT SERVICE -- all Experiments");
		
		List<Experiment> list = new ArrayList<Experiment>();
		experimentDAO.findAll().forEach(list::add);
				
		logger.info("[FINAL] EXPERIMENT SERVICE -- all Experiments");
		return DtoAssembler.toList(list);
	}
	
	@Override
	public List<DeviceDTO> getAllDevices() {
		logger.info("[INICIO] EXPERIMENT SERVICE -- all Devices");
		
		List<Device> list = new ArrayList<Device>();
		deviceDAO.findAll().forEach(list::add);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- all Devices");
		return DtoAssembler.toListDevices(list);
	}
	
	@Override
	public List<InvestigatorDTO> getInvestigatorsNotAssociatedAnExperiment(Long id) throws ExperimentException {
		logger.info("[INICIO] EXPERIMENT SERVICE -- investigators not associated an experiment");
		
		List<Investigator> list = experimentDAO.findInvestigatorsNotAssociatedAnExperiment(id);
		
		logger.info("[FINAL] EXPERIMENT SERVICE -- investigators not associated an experiment");
		return DtoAssembler.toListInvestigators(list);
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
	
	/**
	 * Devuelve el investigador a partir del optional que se pasa como parámetro
	 * @param optional, parámetro de entrada
	 * @return investigador encontrado
	 * @throws InvestigatorException, en caso de que el investigador no exista en base de datos.
	 */
	private Investigator getInvestigator(Optional<Investigator> optional) throws InvestigatorException{
		Investigator investigator = null;
		if(optional.isPresent()) {
			investigator = optional.get();
		}
		else {
			logger.error("[ERROR - 200] -- El investigador especificado no se encuentra registrado en el sistema");
			throw new InvestigatorException("200");
		}
		return investigator;
	}

}

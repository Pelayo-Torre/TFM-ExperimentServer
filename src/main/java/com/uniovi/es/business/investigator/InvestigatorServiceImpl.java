package com.uniovi.es.business.investigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.ExperimentDTO;
import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.PetitionDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.validators.InvestigatorValidator;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.model.Petition;
import com.uniovi.es.model.StatusPetition;
import com.uniovi.es.persistence.InvestigatorDAO;

@Service
public class InvestigatorServiceImpl implements InvestigatorService{
	
	private static final Logger logger = LoggerFactory.getLogger(InvestigatorServiceImpl.class);
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	@Autowired
	private InvestigatorValidator investigatorValidator;

	@Override
	public void registerInvestigator(InvestigatorDTO dto) throws InvestigatorException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- register INVESTIGATOR ");
		
		investigatorValidator.validate(dto);
		investigatorValidator.validateExistenceOfMail(dto.email);
		
		Investigator investigator = new Investigator();
		DtoAssembler.fillData(investigator, dto);
		
		logger.info("\t \t Registrando el investigador en base de datos");
		investigatorDAO.save(investigator);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- register INVESTIGATOR ");
	}

	@Override
	public InvestigatorDTO getDetail(Long id) throws InvestigatorException{
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- detail INVESTIGATOR ");
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + id);
		Optional<Investigator> optional = investigatorDAO.findById(id);
		Investigator investigator = getInvestigator(optional);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- detail INVESTIGATOR ");
		return DtoAssembler.toDTO(investigator);
	}

	@Override
	public void updateInvestigator(InvestigatorDTO dto) throws InvestigatorException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- update INVESTIGATOR ");
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + dto.id);
		Optional<Investigator> optional = investigatorDAO.findById(dto.id);
		Investigator investigator = getInvestigator(optional);
		
		investigatorValidator.validate(dto);
		
		logger.info("\t \t Validando existencia del mail: " + dto.email);
		if(!investigator.getMail().equals(dto.email)) {
			Investigator i = investigatorDAO.findByMail(dto.email);
			if(i != null) {
				logger.error("[ERROR - 204] -- El email del investigador ya se encuentra registrado en la aplicación");
				throw new InvestigatorException("204");
			}
		}

		DtoAssembler.fillData(investigator, dto);
		
		logger.info("\t \t Registrando el investigador en base de datos");
		investigatorDAO.save(investigator);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- update INVESTIGATOR ");
	}
	
	@Override
	public List<ExperimentDTO> getExperimentsAcceptedByIdInvestigator(Long idInvestigator) throws InvestigatorException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- getExperimentsAcceptedByIdInvestigator INVESTIGATOR ");
		
		logger.info("\t \t Obteniendo los experimentos del investigador: " + idInvestigator);
		List<Experiment> experiments = investigatorDAO.findExperimentsByIdInvestigator(idInvestigator, StatusPetition.ACCEPTED);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- getExperimentsAcceptedByIdInvestigator INVESTIGATOR ");
		return DtoAssembler.toList(experiments);
	}
	
	@Override
	public List<PetitionDTO> getPetitionsPendingByIdInvestigator(Long idInvestigator) throws InvestigatorException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- getPetitionsPendingByIdInvestigator INVESTIGATOR ");
		
		logger.info("\t \t Obteniendo las peticiones del investigador: " + idInvestigator);
		List<Petition> petitions = investigatorDAO.findPetitionsByIdInvestigator(idInvestigator, StatusPetition.PENDING);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- getPetitionsPendingByIdInvestigator INVESTIGATOR ");
		return DtoAssembler.toListPetitions(petitions);
	}

	@Override
	public List<InvestigatorDTO> getListInvestigators() {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- all investigators ");

		List<Investigator> list = new ArrayList<Investigator>();
		investigatorDAO.findAll().forEach(list::add);;
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- all investigators ");
		return DtoAssembler.toListInvestigators(list);
	}
	
	@Override
	public InvestigatorDTO getInvestigatorByMail(String mail) throws InvestigatorException {
		logger.debug("[INICIO] INVESTIGATOR-SERVICE -- investigator by mail ");
		
		Investigator investigator = investigatorDAO.findByMail(mail);
		
		logger.debug("[FINAL] INVESTIGATOR-SERVICE -- investigator by mail ");
		return DtoAssembler.toDTO(investigator);
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

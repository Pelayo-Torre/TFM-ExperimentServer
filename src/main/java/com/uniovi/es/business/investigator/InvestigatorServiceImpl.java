package com.uniovi.es.business.investigator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.dto.InvestigatorDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.validators.InvestigatorValidator;
import com.uniovi.es.exceptions.InvestigatorException;
import com.uniovi.es.model.Investigator;
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
		logger.debug("[INICIO -- INVESTIGATOR-SERVICE -- REGISTER INVESTIGATOR] ");
		
		investigatorValidator.validate(dto);
		investigatorValidator.validateExistenceOfMail(dto.email);
		
		Investigator investigator = new Investigator();
		DtoAssembler.fillData(investigator, dto);
		
		logger.info("\t \t Registrando el investigador en base de datos");
		investigatorDAO.save(investigator);
		
		logger.debug("[FINAL -- INVESTIGATOR-SERVICE -- REGISTER INVESTIGATOR] ");
	}

	@Override
	public InvestigatorDTO getDetail(Long id) throws InvestigatorException{
		logger.debug("[INICIO -- INVESTIGATOR-SERVICE -- DETAIL INVESTIGATOR] ");
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + id);
		Optional<Investigator> optional = investigatorDAO.findById(id);
		Investigator investigator = getInvestigator(optional);
		
		logger.debug("[FINAL -- INVESTIGATOR-SERVICE -- DETAIL INVESTIGATOR] ");
		return DtoAssembler.toDTO(investigator);
	}

	@Override
	public void updateInvestigator(InvestigatorDTO dto) throws InvestigatorException {
		logger.debug("[INICIO -- INVESTIGATOR-SERVICE -- UPDATE INVESTIGATOR] ");
		
		logger.info("\t \t Obteniendo el investigador a partir del ID: " + dto.id);
		Optional<Investigator> optional = investigatorDAO.findById(dto.id);
		Investigator investigator = getInvestigator(optional);
		
		investigatorValidator.validate(dto);

		DtoAssembler.fillData(investigator, dto);
		
		logger.info("\t \t Registrando el investigador en base de datos");
		investigatorDAO.save(investigator);
		
		logger.debug("[FINAL -- INVESTIGATOR-SERVICE -- UPDATE INVESTIGATOR] ");
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

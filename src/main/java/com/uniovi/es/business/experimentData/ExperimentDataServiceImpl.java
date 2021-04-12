package com.uniovi.es.business.experimentData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniovi.es.business.authentication.UserInSession;
import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.dto.SceneDTO;
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.business.dto.UserDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.experimentData.strategy.StrategyData;
import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.model.User;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.persistence.UserDAO;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;

@Service
public class ExperimentDataServiceImpl implements ExperimentDataService{
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentDataServiceImpl.class);
		
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UserInSession userInSession;

	@Override
	public Map<String, List<StrategyDataDTO>> getData(CalculateDataDTO data) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getData");
		Map<String, List<StrategyDataDTO>> datos = new HashMap<String, List<StrategyDataDTO>>();
		
		//Se valida que el investigador esté asociado al experimento
		if(data.idExperiment == null || 
				petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), data.idExperiment) == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores gestores");
			throw new ForbiddenException("116");
		}

		if(data != null && data.users != null) {
			for(String sessionID : data.users) {
					
				List<StrategyDataDTO> strategysCalculated = new ArrayList<StrategyDataDTO>();
								
				if(data != null && data.sceneID != null && sessionID != null && data.strategys != null) {
					logger.info("\t \t Número total de strategys a ejecutar: " + data.strategys.size());
										
					for(Integer strategy : data.strategys) {
						
						logger.info("\t \t Ejecutando estrategia con identificador: " + strategy);
						StrategyData sd = StrategyDataManager.getInstance().getStrategyData(strategy);
						
						StrategyDataDTO sdata = new StrategyDataDTO();
						sdata.identifier = strategy;
						sdata.sessionId = sessionID;
						sdata.sceneId = data.sceneID;
						
						if(sd != null) {
							logger.info("\t \t Calculando datos estrategia con identificador: " + strategy);
							sdata.name = sd.getPropertyName();
							sdata.result = sd.calculate(data.sceneID, sessionID);
							sdata.abbreviation = sd.getPropertyAbbreviation();
						}
						else {
							logger.info("\t \t Estrategia con identificador: " + strategy + " no existente en el sistema");
						}
						strategysCalculated.add(sdata);
					}
				}
				datos.put(sessionID, strategysCalculated);
			}
		}		
		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- getData");
		return datos;
	}

	@Override
	public List<UserDTO> getUsersByExperiment(Long idExperiment) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getUsersByExperiment");
		
		//Se valida que el investigador esté asociado al experimento
		if(idExperiment == null || 
				petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), idExperiment) == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores gestores");
			throw new ForbiddenException("116");
		}
		
		List<User> users = userDAO.getUsersByExperiment(idExperiment);
		if(users == null)
			users = new ArrayList<User>();
		
		logger.info("\t \t Obtenidos " + users.size() + " usuarios");		
		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- getUsersByExperiment");
		
		return DtoAssembler.toListUserDTO(users);
	}

	@Override
	public List<StrategyDataDTO> getStrategys() {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getStrategys");
		List<StrategyDataDTO> list = new ArrayList<StrategyDataDTO>();
		
		Map<Integer, StrategyData> strategys = StrategyDataManager.getInstance().getStrategys();
		
		for (Map.Entry<Integer, StrategyData> entry : strategys.entrySet()) {
			StrategyDataDTO dto = new StrategyDataDTO();
			dto.abbreviation = entry.getValue().getPropertyAbbreviation();
			dto.name = entry.getValue().getPropertyName();
			dto.identifier = entry.getKey();
		    list.add(dto);
		}
		
		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- getStrategys");
		return list;
	}

	@Override
	public List<SceneDTO> getScenesByExperiment(Long idExperiment) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getScenesByExperiment");
		
		//Se valida que el investigador esté asociado al experimento
		if(idExperiment == null || 
				petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), idExperiment) == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores gestores");
			throw new ForbiddenException("116");
		}
		
		List<String> list = ExperimentDataFactory.getSceneComponentDAO().getScenes(idExperiment);
		
		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- getScenesByExperiment");
		return DtoAssembler.toListSceneDTO(list);
	}

	

}

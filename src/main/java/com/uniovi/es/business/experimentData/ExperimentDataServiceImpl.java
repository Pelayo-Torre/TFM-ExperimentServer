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
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.business.experimentData.strategy.StrategyData;
import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.persistence.PetitionDAO;
import com.uniovi.es.persistence.experimentData.ExperimentDataFactory;

@Service
public class ExperimentDataServiceImpl implements ExperimentDataService{
	
	private static final Logger logger = LoggerFactory.getLogger(ExperimentDataServiceImpl.class);
		
	@Autowired
	private PetitionDAO petitionDAO;
	
	@Autowired
	private UserInSession userInSession;

	@Override
	public Map<String, List<StrategyDataDTO>> getData(CalculateDataDTO data) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getData");
		Map<String, List<StrategyDataDTO>> datos = new HashMap<String, List<StrategyDataDTO>>();

		if(data != null && data.users != null) {
			for(String sessionID : data.users) {
				//Se obtiene el experimento del usuario 
				logger.info("\t \t Se obtiene el experimento del usario: " + sessionID);
				Long idExperiment = ExperimentDataFactory.getUserDAO().getExperimentOfUser(sessionID);
			
				//Se valida que el investigador esté asociado al experimento
				if(idExperiment == null || sessionID == null ||
						petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), idExperiment) == null) {
					logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores gestores");
					throw new ForbiddenException("116");
				}
				
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

	

}

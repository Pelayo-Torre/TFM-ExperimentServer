package com.uniovi.es.business.experimentData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.uniovi.es.business.authentication.UserInSession;
import com.uniovi.es.business.dto.CalculateDataDTO;
import com.uniovi.es.business.dto.DemographicDataDTO;
import com.uniovi.es.business.dto.EventDTO;
import com.uniovi.es.business.dto.FilterDTO;
import com.uniovi.es.business.dto.SceneDTO;
import com.uniovi.es.business.dto.StrategyDataDTO;
import com.uniovi.es.business.dto.UserDTO;
import com.uniovi.es.business.dto.assembler.DtoAssembler;
import com.uniovi.es.business.experimentData.filter.FilterData;
import com.uniovi.es.business.experimentData.filter.FilterDataManager;
import com.uniovi.es.business.experimentData.strategy.StrategyData;
import com.uniovi.es.business.experimentData.strategy.StrategyDataManager;
import com.uniovi.es.exceptions.ExperimentException;
import com.uniovi.es.exceptions.ForbiddenException;
import com.uniovi.es.model.DemographicData;
import com.uniovi.es.model.Event;
import com.uniovi.es.model.Experiment;
import com.uniovi.es.model.User;
import com.uniovi.es.persistence.DemographicDataDAO;
import com.uniovi.es.persistence.ExperimentDAO;
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
	private ExperimentDAO experimentDAO;
	
	@Autowired
	private DemographicDataDAO demographicDataDAO;
	
	@Autowired
	private UserInSession userInSession;
	
	@Value("${app.urlBaseScript}")
	private String urlBase;

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
				boolean valid = true;
				List<StrategyDataDTO> strategysCalculated = new ArrayList<StrategyDataDTO>();
								
				if(data != null && data.sceneID != null && sessionID != null && data.strategys != null) {
					logger.info("\t \t Número total de strategys a ejecutar: " + data.strategys.size());
										
					if(data.filters != null && data.filters.size() > 0) {
						logger.info("\t \t Número de filtros a aplicar: " + data.filters.size());
						for(Integer filter : data.filters) {
							FilterData f = FilterDataManager.getInstance().getFilterData(filter);
							if(f != null) {
								valid = f.isValid(data.sceneID, sessionID);
								logger.info("\t \t Resultado de la validación " + f.getName() + ": " + valid);
								if(!valid)
									break;
							}
							else {
								logger.info("\t \t Filtro con identificador: " + filter + " no existente en el sistema");
							}
						}
					}
					
					if(valid) {
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
					
				}
				if(valid)
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
	public List<FilterDTO> getFilters(){
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getFilters");
		List<FilterDTO> list = new ArrayList<FilterDTO>();
		
		Map<Integer, FilterData> filters = FilterDataManager.getInstance().getFilters();
		
		for (Map.Entry<Integer, FilterData> entry : filters.entrySet()) {
			FilterDTO dto = new FilterDTO();
			dto.name = entry.getValue().getName();
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
	
	@Override
	public List<DemographicDataDTO> getDemographicData(Long idExperiment) throws ForbiddenException {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getDemographicData");
		
		//Se valida que el investigador esté asociado al experimento
		if(idExperiment == null || 
				petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), idExperiment) == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores gestores");
			throw new ForbiddenException("116");
		}
		
		List<DemographicData> list = demographicDataDAO.getDemographicData(idExperiment);

		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- getDemographicData");
		return DtoAssembler.toListDemographicData(list);
	}

	@Override
	public Resource generateScriptJS(Long idExperiment) throws ForbiddenException, ExperimentException {
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- generateScriptJS");
		
		//Se valida que el experimento exista y se encuentre en estado ABIERTO
		Optional<Experiment> optional = experimentDAO.findById(idExperiment);
		Experiment experiment = getExperiment(optional);
		
		if(!experiment.isOpen()) {
			logger.error("[ERROR -- 119] - El script de toma de datos solo puede ser generado cuando el estado del experimento es ABIERTO.");
			throw new ExperimentException("119");
		}
		
		//Se valida que el investigador esté asociado al experimento
		if(idExperiment == null || 
				petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), idExperiment) == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores");
			throw new ForbiddenException("116");
		}
				
		List<DemographicDataDTO> list = getDemographicData(idExperiment);
		Resource resource = null;
		try {
			
			//Comenzamos haciendo una copia de la plantilla del fichero XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        
	        synchronized (this) {
	        	//Obtenemos el fichero plantilla XML
		        File original = new File("script/script.xml");
		        //Creamos una copia
	        	File copy = new File("script/copy.xml");
		        FileUtils.copyFile(original, copy);
		        Document doc = db.parse(copy);
		        doc.getDocumentElement().normalize();
		        
		        //Obtenemos la primera etiqueta
		        Node script = doc.getElementsByTagName("script").item(0);
		        
		        //Añadimos los datos demográficos como XML
		        Element demographicData = doc.createElement("demographicData");
		        for(DemographicDataDTO dd : list) {
		        	Element entry = doc.createElement("entry");
		        	Element key = doc.createElement("key");
		        	Element value = doc.createElement("value");
		        	Element type = doc.createElement("type");
		        	key.appendChild(doc.createTextNode(dd.id.toString()));
		        	value.appendChild(doc.createTextNode(getDemographicDataFunction(dd.name)));
		        	type.appendChild(doc.createTextNode(dd.type));
		        	entry.appendChild(key);
		        	entry.appendChild(value);
		        	entry.appendChild(type);
		        	demographicData.appendChild(entry);
		        }
		        //Añdimos el nodo al xml
		        script.appendChild(demographicData);
		        
		        Transformer tr = TransformerFactory.newInstance().newTransformer();
		        StreamResult copyModified = new StreamResult(copy);
		        tr.transform(new DOMSource(doc), copyModified);
		        
		        //Fichero de salida
		        File js = new File("script/script.js");
				FileWriter writer = new FileWriter(js);
				StreamResult result = new StreamResult(writer);
				
				//Cargamos el fichero plantilla XSLT y el fichero modificado con los datos demográficos
				Source xslt = new StreamSource(new File("script/script.xsl"));
		        Source xml  = new StreamSource(copy);
		        Result out  = result;
				
				TransformerFactory factory = TransformerFactory.newInstance();
				factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

		        //Se pasan los parámetros requeridos y se realiza la transformación
		        Transformer transformer = factory.newTransformer(xslt);
		        transformer.setParameter("experimento", idExperiment);
		        transformer.setParameter("uriBase", urlBase);
		        transformer.transform(xml, out);
		        
		        Path file = Paths.get("script/").resolve("script.js");
		        resource = new UrlResource(file.toUri());
		
		        
			}
	        
		} catch (IOException | TransformerException | ParserConfigurationException | SAXException e) {
			logger.error("\t \t [ERROR] generateScriptJS -> " + e);
		}
		
		if(resource != null) {
			if (resource.exists() || resource.isReadable()) {
				logger.info("\t \t Recurso encontrado, se procede a su descarga.");
	        	return resource;
	        }
		}
		
		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- generateScriptJS");
		return null;
	}
	
	@Override
	public List<EventDTO> getEventsOfUseSceneAndExperiment(String user, String scene, Long idExperiment)throws ForbiddenException{
		logger.info("[INICIO] EXPERIMENT DATA SERVICE -- getEventsOfUseSceneAndExperiment");
		logger.info("\t \t Datos de entrada: user: " + user + ", scene: " + scene + ", experimento: ", idExperiment);
		
		//Se valida que el investigador esté asociado al experimento
		if(idExperiment == null || 
				petitionDAO.isInvestigatorAssociatedExperiment(userInSession.getInvestigator().getId(), idExperiment) == null) {
			logger.error("[ERROR -- 116] - Un experimento solo puede ser gestionado por sus investigadores");
			throw new ForbiddenException("116");
		}
		
		List<Event> events = ExperimentDataFactory.getEventDAO().getEvents(scene, user, null, null, null, null);
		
		if(events == null)
			events = new ArrayList<Event>();
		
		logger.info("\t \t Total de eventos obtenidos: " + events.size());
		
		logger.info("[FINAL] EXPERIMENT DATA SERVICE -- getEventsOfUseSceneAndExperiment");
		return DtoAssembler.toListEvents(events);
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
	
	private String getDemographicDataFunction(String name) {
		String cadenaNormalize = Normalizer.normalize(name, Normalizer.Form.NFD);   
		return cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
	}

}

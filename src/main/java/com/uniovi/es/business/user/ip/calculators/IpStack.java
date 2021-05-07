package com.uniovi.es.business.user.ip.calculators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.uniovi.es.business.dto.ip.IpStackDTO;
import com.uniovi.es.business.user.ip.IpCalculator;
import com.uniovi.es.model.User;
import com.uniovi.es.persistence.UserDAO;

/**
 * Servicio Web de cálculo de datos a partir de una IP: 
 * URL: https://ipstack.com/
 * @author pelayo
 *
 */
@Component
public class IpStack implements IpCalculator{
	
	private static final Logger logger = LoggerFactory.getLogger(IpStack.class);
	
	@Value("${ip.ipstack}")
	private String url;
	
	@Value("${ip.key.ipstack}")
	private String key;
	
	@Autowired
	private UserDAO userDAO;

	@Override
	public boolean calculate(User user) {
		
		try {
			String uri = url + user.getRemoteAddress() + "?access_key=" + key + "&format=1";
			RestTemplate restTemplate = new RestTemplate();
			IpStackDTO result = restTemplate.getForObject(uri, IpStackDTO.class);
		    
		    if(result == null) {
		    	logger.info("\t \t La respuesta del WS IpStack genera un bean nulo");
		    	return false;
		    }
		    
		    user.setCity(result.getCity());
		    user.setContinent(result.getContinent_name());
		    user.setCountry(result.getCountry_name());
		    user.setRegionname(result.getRegion_name());
		    user.setProcessed(true);
		    
		    //Actualizamos los datos del usuario
		    userDAO.save(user);
		    
		    return true;
			
		} catch (Exception e) {
			logger.error("[ERROR] - IpStack - " + e.toString());
			return false;
		}
	}

}

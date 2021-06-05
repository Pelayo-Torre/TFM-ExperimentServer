package com.uniovi.es.business.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.uniovi.es.business.dto.AuthDTO;
import com.uniovi.es.exceptions.AttempsException;
import com.uniovi.es.model.Investigator;
import com.uniovi.es.persistence.InvestigatorDAO;
import com.uniovi.es.security.Response;
import com.uniovi.es.security.TokenGenerator;

@Service
public class AuthenticationService {

	@Autowired
	private TokenGenerator generator; // Generador del token
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private InvestigatorDAO investigatorDAO;
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
	
	/**
	 * Realiza la autenticación del usuario en el sistema
	 * 
	 * @param dto
	 * @return
	 * @throws AttempsException
	 */
	public ResponseEntity<?> authenticateUser(AuthDTO dto)
			throws AttempsException {
		
		if(dto == null || dto.mail == null || dto.password == null) {
			logger.error("[ERROR] Las credenciales son obligatorias: " + dto);
			throw new AttempsException("602");
		}

		if (loginService.blocked(dto.mail)) {
			logger.error("[ERROR] La cuenta del usuario con email: " + dto.mail + " está bloqueada.");
			throw new AttempsException("601");
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.mail,
						dto.password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = generator.generateToken(authentication);
		UserDetails details = (UserDetails) authentication.getPrincipal();

		Investigator investigator = getUserByMail(authentication.getName());

		return ResponseEntity.ok(new Response(investigator.getMail(), token,
					details.getAuthorities()));

	}
	
	/**
	 * Devuelve el usuario a partir de su login
	 * @param username, nombre del usuario
	 * @return el usuario o null si no existe
	 */
	public Investigator getUserByMail(String mail) {
		return investigatorDAO.findByMail(mail.toLowerCase());
	}
	
}

package com.uniovi.es.business.authentication;

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
	
	/**
	 * Realiza la autenticación del usuario en el sistema
	 * 
	 * @param dto
	 * @return
	 * @throws AttempsException
	 */
	public ResponseEntity<?> authenticateUser(AuthDTO dto)
			throws AttempsException {

		if (loginService.blocked(dto.mail)) {
			throw new AttempsException(
					"Usuario bloquedo por sobrepasar el número de intentos");
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(dto.mail,
						dto.password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = generator.generateToken(authentication);
		UserDetails details = (UserDetails) authentication.getPrincipal();

		Investigator investigator = getUserByMail(authentication.getName());

//		if (user.getAssociation() != null) {
//			return ResponseEntity.ok(new Response(user.getLogin(), token,
//					user.getStatus(), user.getAssociation().getStatus(),
//					details.getAuthorities()));
//		} else {
			return ResponseEntity.ok(new Response(investigator.getMail(), token,
					details.getAuthorities()));
		//}

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

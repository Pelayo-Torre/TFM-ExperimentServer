package com.uniovi.es.security;


import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

@Component
public class TokenGenerator {

	// Clave expuesta en el fichero de propiedades
	// Con ella se forma el token que se va a generar
	@Value("${app.key}")
	private String key;

	// Clave expuesta en el fichero de propiedades
	// Indica el tiempo de ex
	@Value("${app.time}")
	private int time;

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(key).parseClaimsJws(token);
			return true;
		} catch (ExpiredJwtException exception) {
			logger.error("El token ha caducado: {}", exception);
		} catch (SignatureException exception) {
			logger.error("Token mal formado: {} ", exception);
		} catch (IllegalArgumentException exception) {
			logger.error("Error Ilegal token: {}", exception);
		} catch (UnsupportedJwtException exception) {
			logger.error("Tolen no soportado: {}", exception);
		} catch (MalformedJwtException exception) {
			logger.error("Token inválido: {}", exception);
		}
		return false;
	}
	
	public String generateToken(Authentication auth) {

		PrincipalUser userPrincipal = (PrincipalUser) auth.getPrincipal();
		return Jwts.builder().setSubject((userPrincipal.getUsername()))   // aquí iba userPrincipal.getUuid()
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + (time * 1000)))
				.signWith(SignatureAlgorithm.HS512, key).compact();
	}

	public String getUserByToken(String token) {
		return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody()
				.getSubject();
	}

	private static final Logger logger = LoggerFactory
			.getLogger(TokenGenerator.class);

}

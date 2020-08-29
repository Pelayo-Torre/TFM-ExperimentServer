package com.uniovi.es.business.authentication;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.stereotype.Service;

@Service
public class LoginService {
	
	private final static int NUM_MAX_ATTEMPS = 3;
	private Map<String, Integer> dictionary;
	private Map<String, Date> dictionaryDates;
	
	private Timer timer;
	private TimerTask timerTask;

	public LoginService() {
		super();
		dictionary = new HashMap<String, Integer>();
		dictionaryDates = new HashMap<String, Date>();
		timer = new Timer();
		time();
	}
	
	/**
	 * Resetea los dos diccionarios cada hora de reloj
	 */
	public void time() {
		timerTask = new TimerTask() {

			@Override
			public void run() {
				dictionary.clear();
				dictionaryDates.clear();
			}
			
		};
		timer.schedule(timerTask, 10, 3600000);
	}
	
	/**
	 * Si se registra un fallo, se guarda en los diccionarios
	 * @param username
	 */
	public void fail(String username) {
		if (dictionary.get(username) == null) {
			dictionary.put(username, 1);
		} else {
			int intentos = dictionary.get(username);
			dictionary.remove(username);
			dictionary.put(username, (intentos + 1));
		}
	}

	/**
	 * Si el usuario es correcto, se borra de los diccionarios
	 * @param username
	 */
	public void correct(String username) {
		if (dictionary.containsKey(username)) {
			dictionary.remove(username);
		}
		if (dictionaryDates.containsKey(username)) {
			dictionaryDates.remove(username);
		}
	}

	/**
	 * Devuelve si el usuario está o no bloquedao
	 * @param username
	 * @return true si está bloqueado, false en caso contrario.
	 */
	public boolean blocked(String username) {
		Date now = new Date();
		// Se comprueba si el usuario ya había fallado el login
		if (dictionary.containsKey(username)) {
			// Si lo había fallado más de 3 veces
			if (dictionary.get(username) >= NUM_MAX_ATTEMPS) {
				if (dictionaryDates.containsKey(username)) {
					// Si superó los 3 intentos y la fecha de expiración es
					// mayor que ahora, está bloqueadp
					if (now.before(dictionaryDates.get(username)))
						return true;
					// Si la fecha de expiración ya pasó, no está bloqueado
					else {
						dictionary.remove(username);
						dictionaryDates.remove(username);
						return false;
					}
				} else {
					dictionaryDates.put(username, expireAccount(30));
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * Fecha de expiración
	 * @param min
	 * @return
	 */
	private Date expireAccount(int min) {
		Date expirationDate;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, min);
		expirationDate = calendar.getTime();
		return expirationDate;
	}

}

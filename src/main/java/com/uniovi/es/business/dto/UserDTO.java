package com.uniovi.es.business.dto;


public class UserDTO {

	public String sessionId;
	
	public Boolean browserOnline, javaEnabled, dataCookiesEnabled;

	public String timeOpened, pageon, referrer, browserName, browserEngine, browserVersion1a, browserVersion1b,
			browserLanguage, browserPlatform, dataCookies1, dataCookies2, dataStorage;
	
	public String remoteHost;
	
	public String locale;
	
	public String remoteAddress;
	
	public Integer previousSites, sizeScreenW, sizeScreenH, sizeDocW, sizeDocH, sizeInW, sizeInH, sizeAvailW, sizeAvailH,
			scrColorDepth, scrPixelDepth;
	
	public Integer remotePort;
	
	public Integer timezone;
	
	public Long timeStamp;
	
	public Long idExperiment;
	
}

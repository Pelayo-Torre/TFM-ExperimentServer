package com.uniovi.es.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "userdata")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	private String sessionId;
		
	private Boolean browserOnline, javaEnabled, dataCookiesEnabled;

	private String timeOpened, pageon, referrer, browserName, browserEngine, browserVersion1a, browserVersion1b,
			browserLanguage, browserPlatform, dataCookies1, dataCookies2, dataStorage;
	
	@Column(nullable = false)
	private String remoteHost;
	
	@Column(nullable = false)
	private String locale;
	
	@Column(nullable = false)
	private String remoteAddress;
	
	private Integer previousSites, sizeScreenW, sizeScreenH, sizeDocW, sizeDocH, sizeInW, sizeInH, sizeAvailW, sizeAvailH,
			scrColorDepth, scrPixelDepth;
	
	@Column(nullable = false)
	private Integer remotePort;
	
	@Column(nullable = false)
	private Integer timezone;
	
	@Column(nullable = false)
	private Long timeStamp;
	
	@ManyToOne
	private Experiment experiment;
	
	@OneToMany(mappedBy = "user")
	private Set<Event> events = new HashSet<Event>();
	
	@OneToMany(mappedBy = "user")
	private Set<ComponentData> components = new HashSet<ComponentData>();
	
	User() {}
	
	public User(String sessionID, Experiment experiment, Integer remotePort, 
			Integer timezone, Long timeStamp, String remoteHost, String locale, String remoteAddress) {
		this.sessionId = sessionID;
		this.remotePort = remotePort;
		this.timezone = timezone;
		this.timeStamp = timeStamp;
		this.remoteHost = remoteHost;
		this.locale = locale;
		this.remoteAddress = remoteAddress;
		Associations.UserExperiment.link(this, experiment);
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public boolean isBrowserOnline() {
		return browserOnline;
	}

	public void setBrowserOnline(boolean browserOnline) {
		this.browserOnline = browserOnline;
	}

	public boolean isJavaEnabled() {
		return javaEnabled;
	}

	public void setJavaEnabled(boolean javaEnabled) {
		this.javaEnabled = javaEnabled;
	}

	public boolean isDataCookiesEnabled() {
		return dataCookiesEnabled;
	}

	public void setDataCookiesEnabled(boolean dataCookiesEnabled) {
		this.dataCookiesEnabled = dataCookiesEnabled;
	}

	public String getTimeOpened() {
		return timeOpened;
	}

	public void setTimeOpened(String timeOpened) {
		this.timeOpened = timeOpened;
	}

	public String getPageon() {
		return pageon;
	}

	public void setPageon(String pageon) {
		this.pageon = pageon;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getBrowserName() {
		return browserName;
	}

	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}

	public String getBrowserEngine() {
		return browserEngine;
	}

	public void setBrowserEngine(String browserEngine) {
		this.browserEngine = browserEngine;
	}

	public String getBrowserVersion1a() {
		return browserVersion1a;
	}

	public void setBrowserVersion1a(String browserVersion1a) {
		this.browserVersion1a = browserVersion1a;
	}

	public String getBrowserVersion1b() {
		return browserVersion1b;
	}

	public void setBrowserVersion1b(String browserVersion1b) {
		this.browserVersion1b = browserVersion1b;
	}

	public String getBrowserLanguage() {
		return browserLanguage;
	}

	public void setBrowserLanguage(String browserLanguage) {
		this.browserLanguage = browserLanguage;
	}

	public String getBrowserPlatform() {
		return browserPlatform;
	}

	public void setBrowserPlatform(String browserPlatform) {
		this.browserPlatform = browserPlatform;
	}

	public String getDataCookies1() {
		return dataCookies1;
	}

	public void setDataCookies1(String dataCookies1) {
		this.dataCookies1 = dataCookies1;
	}

	public String getDataCookies2() {
		return dataCookies2;
	}

	public void setDataCookies2(String dataCookies2) {
		this.dataCookies2 = dataCookies2;
	}

	public String getDataStorage() {
		return dataStorage;
	}

	public void setDataStorage(String dataStorage) {
		this.dataStorage = dataStorage;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public Integer getPreviousSites() {
		return previousSites;
	}

	public void setPreviousSites(Integer previousSites) {
		this.previousSites = previousSites;
	}

	public Integer getSizeScreenW() {
		return sizeScreenW;
	}

	public void setSizeScreenW(Integer sizeScreenW) {
		this.sizeScreenW = sizeScreenW;
	}

	public Integer getSizeScreenH() {
		return sizeScreenH;
	}

	public void setSizeScreenH(Integer sizeScreenH) {
		this.sizeScreenH = sizeScreenH;
	}

	public Integer getSizeDocW() {
		return sizeDocW;
	}

	public void setSizeDocW(Integer sizeDocW) {
		this.sizeDocW = sizeDocW;
	}

	public Integer getSizeDocH() {
		return sizeDocH;
	}

	public void setSizeDocH(Integer sizeDocH) {
		this.sizeDocH = sizeDocH;
	}

	public Integer getSizeInW() {
		return sizeInW;
	}

	public void setSizeInW(Integer sizeInW) {
		this.sizeInW = sizeInW;
	}

	public Integer getSizeInH() {
		return sizeInH;
	}

	public void setSizeInH(Integer sizeInH) {
		this.sizeInH = sizeInH;
	}

	public Integer getSizeAvailW() {
		return sizeAvailW;
	}

	public void setSizeAvailW(Integer sizeAvailW) {
		this.sizeAvailW = sizeAvailW;
	}

	public Integer getSizeAvailH() {
		return sizeAvailH;
	}

	public void setSizeAvailH(Integer sizeAvailH) {
		this.sizeAvailH = sizeAvailH;
	}

	public Integer getScrColorDepth() {
		return scrColorDepth;
	}

	public void setScrColorDepth(Integer scrColorDepth) {
		this.scrColorDepth = scrColorDepth;
	}

	public Integer getScrPixelDepth() {
		return scrPixelDepth;
	}

	public void setScrPixelDepth(Integer scrPixelDepth) {
		this.scrPixelDepth = scrPixelDepth;
	}

	public Integer getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(Integer remotePort) {
		this.remotePort = remotePort;
	}

	public Integer getTimezone() {
		return timezone;
	}

	public void setTimezone(Integer timezone) {
		this.timezone = timezone;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}

	public Set<Event> getEvents() {
		return events;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	public Set<ComponentData> getComponents() {
		return components;
	}

	public void setComponents(Set<ComponentData> components) {
		this.components = components;
	}

	public Boolean getBrowserOnline() {
		return browserOnline;
	}

	public void setBrowserOnline(Boolean browserOnline) {
		this.browserOnline = browserOnline;
	}

	public Boolean getJavaEnabled() {
		return javaEnabled;
	}

	public void setJavaEnabled(Boolean javaEnabled) {
		this.javaEnabled = javaEnabled;
	}

	public Boolean getDataCookiesEnabled() {
		return dataCookiesEnabled;
	}

	public void setDataCookiesEnabled(Boolean dataCookiesEnabled) {
		this.dataCookiesEnabled = dataCookiesEnabled;
	}

	@Override
	public String toString() {
		return "User [sessionId=" + sessionId + ", browserOnline=" + browserOnline + ", javaEnabled=" + javaEnabled
				+ ", dataCookiesEnabled=" + dataCookiesEnabled + ", timeOpened=" + timeOpened + ", pageon=" + pageon
				+ ", referrer=" + referrer + ", browserName=" + browserName + ", browserEngine=" + browserEngine
				+ ", browserVersion1a=" + browserVersion1a + ", browserVersion1b=" + browserVersion1b
				+ ", browserLanguage=" + browserLanguage + ", browserPlatform=" + browserPlatform + ", dataCookies1="
				+ dataCookies1 + ", dataCookies2=" + dataCookies2 + ", dataStorage=" + dataStorage + ", remoteHost="
				+ remoteHost + ", locale=" + locale + ", remoteAddress=" + remoteAddress + ", previousSites="
				+ previousSites + ", sizeScreenW=" + sizeScreenW + ", sizeScreenH=" + sizeScreenH + ", sizeDocW="
				+ sizeDocW + ", sizeDocH=" + sizeDocH + ", sizeInW=" + sizeInW + ", sizeInH=" + sizeInH
				+ ", sizeAvailW=" + sizeAvailW + ", sizeAvailH=" + sizeAvailH + ", scrColorDepth=" + scrColorDepth
				+ ", scrPixelDepth=" + scrPixelDepth + ", remotePort=" + remotePort + ", timezone=" + timezone
				+ ", timeStamp=" + timeStamp + ", experiment=" + experiment + ", events=" + events + ", components="
				+ components + "]";
	}
	
	
	
}

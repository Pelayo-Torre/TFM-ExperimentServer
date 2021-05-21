package com.uniovi.es.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "event")
public class Event implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String sceneId;
	
	@Column(nullable = false)
	private Integer eventType;
	
	@Column(nullable = false)
	private String elementId;
	
	private Long timeStamp;
	
	@Column(nullable = false)
	private Integer x;
	
	@Column(nullable = false)
	private Integer y;
	
	private String keyValueEvent;
	
	private Integer keyCodeEvent;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private User user;
		
	public Event() {}
		
	public Event(String sceneId, Integer eventType, String elementId, Long timeStamp, Integer x, Integer y,
			String keyValueEvent, Integer keyCodeEvent, User user) {
		super();
		this.sceneId = sceneId;
		this.eventType = eventType;
		this.elementId = elementId;
		this.timeStamp = timeStamp;
		this.x = x;
		this.y = y;
		this.keyValueEvent = keyValueEvent;
		this.keyCodeEvent = keyCodeEvent;
		Associations.UserEvent.link(user, this);
	}



	public String getSceneId() {
		return sceneId;
	}

	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}

	public Integer getEventType() {
		return eventType;
	}

	public void setEventType(Integer eventType) {
		this.eventType = eventType;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getKeyValueEvent() {
		return keyValueEvent;
	}

	public void setKeyValueEvent(String keyValueEvent) {
		this.keyValueEvent = keyValueEvent;
	}

	public Integer getKeyCodeEvent() {
		return keyCodeEvent;
	}

	public void setKeyCodeEvent(Integer keyCodeEvent) {
		this.keyCodeEvent = keyCodeEvent;
	}

	@Override
	public String toString() {
		return "Event [id=" + id + ", sceneId=" + sceneId + ", eventType=" + eventType + ", elementId=" + elementId
				+ ", timeStamp=" + timeStamp + ", x=" + x + ", y=" + y + ", keyValueEvent=" + keyValueEvent
				+ ", keyCodeEvent=" + keyCodeEvent + "]";
	}
	
}

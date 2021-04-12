package com.uniovi.es.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "scene_component")
public class ComponentData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(nullable = false)
	private Integer x,y,xF,yF;
	
	@Column(nullable = false)
	private Long timeStamp;
	
	@Id
	@Column(nullable = false)
	private String sceneId;
	
	@Id
	@ManyToOne
	private User user;
	
	@Id
	private String componentId;
	
	private Integer typeId;
	
	private String componentAssociated;
	
	public ComponentData() {}

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

	public Integer getxF() {
		return xF;
	}

	public void setxF(Integer xF) {
		this.xF = xF;
	}

	public Integer getyF() {
		return yF;
	}

	public void setyF(Integer yF) {
		this.yF = yF;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getSceneId() {
		return sceneId;
	}

	public void setSceneId(String sceneId) {
		this.sceneId = sceneId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getComponentAssociated() {
		return componentAssociated;
	}

	public void setComponentAssociated(String componentAssociated) {
		this.componentAssociated = componentAssociated;
	}
	
}

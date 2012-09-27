package com.ourlittlegame.entities;

import java.io.Serializable;

import org.json.JSONObject;

import com.ourlittlegame.utilities.JsonUtils;

public class User implements Serializable  {
	private static final long serialVersionUID = 6145139656740415145L;
	private JSONObject jsonObject;

	public User(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public static User parse(JSONObject jsonObject) {
		return new User(jsonObject);
	}

	public int getID() {
		return JsonUtils.getIntProperty(jsonObject,"id",0);
	}
	
	public String getRelation() {
		return JsonUtils.getStringProperty(jsonObject,"relation","self");
	}
	
	public String getName() {
		return JsonUtils.getStringProperty(jsonObject,"name",null);
	}
	
	public String getPicture() {
		return JsonUtils.getStringProperty(jsonObject,"picture",null);
	}
	
	public String getGender() {
		return JsonUtils.getStringProperty(jsonObject,"gender",null);
	}
	
	public String getFacebookUID() {
		return JsonUtils.getStringProperty(jsonObject,"facebook_uid",null);
	}
}

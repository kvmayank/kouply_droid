package com.ourlittlegame.entities;

import java.io.Serializable;

import org.json.JSONObject;

import com.ourlittlegame.utilities.JsonUtils;

public class Place implements Serializable  {
	private static final long serialVersionUID = 7775459732296690651L;
	private JSONObject jsonObject;

	public Place(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public static Place parse(JSONObject jsonObject) {
		return new Place(jsonObject);
	}

	public int getID() {
		return JsonUtils.getIntProperty(jsonObject,"id",0);
	}
	
	public String getName() {
		return JsonUtils.getStringProperty(jsonObject,"name",null);
	}
	
	public String getPicture() {
		return JsonUtils.getStringProperty(jsonObject,"picture",null);
	}	
}

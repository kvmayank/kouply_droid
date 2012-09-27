package com.ourlittlegame.entities;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.ourlittlegame.utilities.JsonUtils;

public class Comment implements Serializable  {
	private static final long serialVersionUID = 5669612303668355151L;
	private JSONObject jsonObject;

	public Comment(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public static Comment parse(JSONObject jsonObject) {
		return new Comment(jsonObject);
	}

	public JSONObject getJson() {
		return jsonObject;
	}
	
	public int getID() {
		return JsonUtils.getIntProperty(jsonObject,"id",0);
	}
	
	public String getMessage() {
		return JsonUtils.getStringProperty(jsonObject,"message",null);
	}
	
	public User getUser() {
		try {
			if (jsonObject.has("user"))
				return User.parse(jsonObject.getJSONObject("user"));
			else 
				return null;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int getUserId() {
		try {
			if (jsonObject.has("user_id"))
				return jsonObject.getInt("user_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getCreatedAt() {
		return JsonUtils.getStringProperty(jsonObject,"created_at","");
	}
}

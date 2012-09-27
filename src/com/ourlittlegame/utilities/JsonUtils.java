package com.ourlittlegame.utilities;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {

	public static int getIntProperty(JSONObject jsonObject, String prop, int default_val) {
		try {
			if (jsonObject.has(prop))
				return jsonObject.getInt(prop);
			else
				return default_val;			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return default_val;
	}

	public static String getStringProperty(JSONObject jsonObject,
			String prop, String default_val) {
		try {
			if (jsonObject.has(prop))
				return jsonObject.getString(prop);
			else
				return default_val;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return default_val;
	}

	public static boolean getBooleanProperty(JSONObject jsonObject,
			String prop, boolean default_val) {
		try {
			if (jsonObject.has(prop))
				return jsonObject.getBoolean(prop);
			else
				return default_val;							
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return default_val;
	}

}

package com.ourlittlegame.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ourlittlegame.utilities.JsonUtils;
import com.ourlittlegame.utilities.MiscUtils;

public class Couple implements Serializable {
	private static final long serialVersionUID = -3498865743085291493L;
	private JSONObject jsonObject;
	private List<User> users = new ArrayList<User>();
	private List<Activity> activityList = new ArrayList<Activity>();
	
	public Couple(JSONObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public static Couple parse(JSONObject jsonObject) {		
		return new Couple(jsonObject);
	}

	public int getScore() {
		return JsonUtils.getIntProperty(jsonObject,"score",0);		
	}
	
	public int getUnreadNotifications() {
		return JsonUtils.getIntProperty(jsonObject,"unread_notifications",0);
	}
	
	public int getID() {
		return JsonUtils.getIntProperty(jsonObject,"id",0);
	}
	
	public String getAnniversary() {
		return JsonUtils.getStringProperty(jsonObject,"anniversary",null);
	}
	
	public String getRelation() {
		return JsonUtils.getStringProperty(jsonObject,"relation","self");
	}
	
	public String getCurrentCover() {
		return JsonUtils.getStringProperty(jsonObject,"current_cover",null);
	}
	
	public boolean isConfirmed() {
		return JsonUtils.getBooleanProperty(jsonObject,"confirmed",false);
	}
	
	public List<User> getUsers() {
		if (users.size() > 0)
			return users;
		
		if (jsonObject.has("users")) {
			try {
				JSONArray jsUsers = jsonObject.getJSONArray("users");
				for (int i=0;i<jsUsers.length();i++) {
					JSONObject jso = jsUsers.getJSONObject(i);
					if (jso.has("user")) {
						User u = User.parse(jso.getJSONObject("user"));
						if (u != null)
							users.add(u);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return users;
	}
	
	public List<Activity> getActivities() {
		if (activityList.size() > 0)
			return activityList;
		
		if (jsonObject.has("activities")) {
			try {
				JSONArray jsUsers = jsonObject.getJSONArray("activities");
				for (int i=0;i<jsUsers.length();i++) {
					JSONObject jso = jsUsers.getJSONObject(i);
					if (jso.has("activity")) {
						Activity u = Activity.parse(jso.getJSONObject("activity"));
						if (u != null)
							activityList.add(u);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			MiscUtils.println("Activities object not found");
			System.out.println(jsonObject.toString());
		}
		
		return activityList;
	}


	public void addActivity(com.ourlittlegame.entities.Activity a) {
		if (jsonObject.has("activities")) {
			try {
				JSONArray jsActivities = jsonObject.getJSONArray("activities");
				JSONObject js = new JSONObject();
				js.put("activity", a.getJson());
				jsActivities.put(0,js);
				this.activityList.clear();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public User getCurrentUser() {
		List<User> l = getUsers();
		for (Iterator<User> it = l.iterator(); it.hasNext();) {
			User u = it.next();
			if (MiscUtils.removeNull(u.getRelation()).toLowerCase().equals("self")) {
				return u;
			}
		}
		return null;
	}
	
	public User getPartner() {
		List<User> l = getUsers();
		for (Iterator<User> it = l.iterator(); it.hasNext();) {
			User u = it.next();
			if (! MiscUtils.removeNull(u.getRelation()).toLowerCase().equals("self")) {
				return u;
			}
		}
		return null;
	}
}

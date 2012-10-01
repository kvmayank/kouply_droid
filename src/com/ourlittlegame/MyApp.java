package com.ourlittlegame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;

import com.ourlittlegame.adaptors.MenuListAdaptor;
import com.ourlittlegame.adaptors.MenuListAdaptor.MenuItem;
import com.ourlittlegame.entities.Activity;
import com.ourlittlegame.entities.Comment;
import com.ourlittlegame.entities.Couple;
import com.ourlittlegame.entities.Notification;
import com.ourlittlegame.entities.Suggestion;
import com.ourlittlegame.entities.User;

public class MyApp extends Application {

	private String fb_access_token;
	private String app_token;
	private Couple couple;
	private List<Notification> notifications = new ArrayList<Notification>();
	private List<Suggestion> suggestions = new ArrayList<Suggestion>();

	public void setFacebookToken(String access_token) {
		this.fb_access_token = access_token;
	}

	public String getFacebookToken() {
		return fb_access_token;
	}

	public void setAppToken(String appToken) {
		this.app_token = appToken;
	}

	public String getAppToken() {
		return app_token;
	}

	public void setCouple(Couple c) {
		this.couple = c;		
	}

	public Couple getCouple() {
		return couple;
	}
	
	public String getExternalStorageFolder() {
		return getFilesDir().getAbsolutePath()+"/assets/";
		//String appFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+"/data/"+getPackageName()+"/files/";
		//return appFolder;
	}

	public User findUserById(int userId) {
		List<User> users = getCouple().getUsers();
		for (Iterator<User> it = users.iterator();it.hasNext();) {
			User u = it.next();
			if (u.getID() == userId)
				return u;
		}
		return null;
	}

	public void addCommentForActivity(int id, Comment c) {
		Activity a = findActivityById(id);
		if (a != null)
			a.addComment(c);
	}

	public Activity findActivityById(int activityID) {
		List<Activity> as = getCouple().getActivities();
		for (Iterator<Activity> it = as.iterator(); it.hasNext();) {
			Activity a = it.next();
			if (a.getID() == activityID)
				return a;
		}
		return null;
	}

	public List<MenuItem> getMenuItems() {
		List<MenuListAdaptor.MenuItem> items = new ArrayList<MenuListAdaptor.MenuItem>();
		items.add(new MenuItem(R.drawable.nav_feed,"Feed"));
		items.add(new MenuItem(R.drawable.nav_friends,"Friends"));
		items.add(new MenuItem(R.drawable.nav_ideas,"Ideas"));
		items.add(new MenuItem(R.drawable.nav_settings,"Settings"));
		items.add(new MenuItem(R.drawable.nav_how,"How it Works"));
		items.add(new MenuItem(0,"Sign Out"));
		return items;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications  = notifications;
	}
	
	public List<Notification> getNotifications() {
		return this.notifications;
	}

	public void setSuggestions(List<Suggestion> suggestions) {
		this.suggestions  = suggestions;
	}
	
	public List<Suggestion> getSuggestions() {
		return this.suggestions;
	}

	public Suggestion getSuggestionById(int id) {
		for (Iterator<Suggestion> it = this.suggestions.iterator(); it.hasNext();) {
			Suggestion a = it.next();
			if (a.getID() == id)
				return a;
		}
		return null;
	}
}
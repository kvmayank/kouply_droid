package com.ourlittlegame.entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

import com.ourlittlegame.utilities.JsonUtils;

public class Activity implements Parcelable {
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(jsonObject.toString());
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Activity> CREATOR = new Parcelable.Creator<Activity>() {
		public Activity createFromParcel(Parcel in) {
			String json = in.readString();
			try {
				Object obj = new JSONTokener(json).nextValue();
				if (obj instanceof JSONObject) {
					return Activity.parse((JSONObject) obj);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public Activity[] newArray(int size) {
			return new Activity[size];
		}
	};

	private JSONObject jsonObject;
	private List<Comment> comments;

	public Activity(JSONObject jsa) {
		this.jsonObject = jsa;
	}

	public static Activity parse(JSONObject jsa) {
		return new Activity(jsa);
	}

	public int getID() {
		return JsonUtils.getIntProperty(jsonObject, "id", 0);
	}

	public String getKind() {
		return JsonUtils.getStringProperty(jsonObject, "kind", "");
	}

	public String getCaption() {
		return JsonUtils.getStringProperty(jsonObject, "caption", "");
	}

	public String getCreatedAt() {
		return JsonUtils.getStringProperty(jsonObject, "created_at", "");
	}

	public int getPoints() {
		return JsonUtils.getIntProperty(jsonObject, "points", 0);
	}

	public int getCoupleId() {
		return JsonUtils.getIntProperty(jsonObject, "couple_id", 0);
	}

	public int getFacebookPostId() {
		return JsonUtils.getIntProperty(jsonObject, "facebook_post_id", 0);
	}

	public String getRatio() {
		return JsonUtils.getStringProperty(jsonObject, "ratio", null);
	}

	public String getPrivacy() {
		return JsonUtils.getStringProperty(jsonObject, "privacy", "");
	}

	public String getThumbnail() {
		return JsonUtils.getStringProperty(jsonObject, "thumbnail", null);
	}

	public String getAsset() {
		return JsonUtils.getStringProperty(jsonObject, "asset", null);
	}

	public User getUser() {
		try {
			return User.parse(jsonObject.getJSONObject("user"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Place getPlace() {
		try {
			return Place.parse(jsonObject.getJSONObject("place"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Comment> getComments() {
		if (this.comments != null)
			return this.comments;

		this.comments = new ArrayList<Comment>();
		if (jsonObject.has("comments")) {
			try {
				JSONArray jsComments = jsonObject.getJSONArray("comments");
				for (int i = 0; i < jsComments.length(); i++) {
					JSONObject jsc = jsComments.getJSONObject(i);
					if (jsc.has("comment")) {
						Comment c = Comment.parse(jsc.getJSONObject("comment"));
						if (c != null)
							this.comments.add(c);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return this.comments;
	}

	public void addComment(Comment c) {
		if (jsonObject.has("comments")) {
			try {
				JSONArray jsComments = jsonObject.getJSONArray("comments");
				JSONObject js = new JSONObject();
				js.put("comment", c.getJson());
				jsComments.put(js);
				this.comments = null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

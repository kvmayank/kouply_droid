package com.ourlittlegame.entities;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

import com.ourlittlegame.utilities.JsonUtils;

public class Notification implements Parcelable {
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(jsonObject.toString());
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Notification> CREATOR = new Parcelable.Creator<Notification>() {
		public Notification createFromParcel(Parcel in) {
			String json = in.readString();
			try {
				Object obj = new JSONTokener(json).nextValue();
				if (obj instanceof JSONObject) {
					return Notification.parse((JSONObject) obj);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public Notification[] newArray(int size) {
			return new Notification[size];
		}
	};

	private JSONObject jsonObject;
	private List<Comment> comments;

	public Notification(JSONObject jsa) {
		this.jsonObject = jsa;
	}

	public static Notification parse(JSONObject jsa) {
		return new Notification(jsa);
	}

	public int getID() {
		return JsonUtils.getIntProperty(jsonObject, "id", 0);
	}

	public String getKind() {
		return JsonUtils.getStringProperty(jsonObject, "kind", "");
	}

	public String getDescription() {
		return JsonUtils.getStringProperty(jsonObject, "description", "");
	}

	public String getCreatedAt() {
		return JsonUtils.getStringProperty(jsonObject, "created_at", "");
	}

	public String getLinkObject() {
		return JsonUtils.getStringProperty(jsonObject, "link_object", "");
	}

	public String getLink() {
		return JsonUtils.getStringProperty(jsonObject, "link", null);
	}

	public boolean isRead() {
		return JsonUtils.getBooleanProperty(jsonObject, "read", true);
	}
}

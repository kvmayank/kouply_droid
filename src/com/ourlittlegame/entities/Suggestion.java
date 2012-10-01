package com.ourlittlegame.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

import com.ourlittlegame.utilities.JsonUtils;

public class Suggestion  implements Parcelable {
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(jsonObject.toString());
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<Suggestion> CREATOR = new Parcelable.Creator<Suggestion>() {
		public Suggestion createFromParcel(Parcel in) {
			String json = in.readString();
			try {
				Object obj = new JSONTokener(json).nextValue();
				if (obj instanceof JSONObject) {
					return Suggestion.parse((JSONObject) obj);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public Suggestion[] newArray(int size) {
			return new Suggestion[size];
		}
	};

	private JSONObject jsonObject;

	public Suggestion(JSONObject jsa) {
		this.jsonObject = jsa;
	}

	public static Suggestion parse(JSONObject jsa) {
		return new Suggestion(jsa);
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
}

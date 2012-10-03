package com.ourlittlegame.responses;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.ourlittlegame.entities.Activity;

import android.os.Parcel;
import android.os.Parcelable;

public class CreateActivityResponse implements Parcelable {

	JSONObject jsonResponse;
	int responseCode;

	public static CreateActivityResponse parseResponse(int responseCode,
			String responseText) {
		CreateActivityResponse r = new CreateActivityResponse();
		r.responseCode = responseCode;

		if (responseText != null && responseText.length() > 0) {
			try {
				Object obj = new JSONTokener(responseText).nextValue();
				if (obj instanceof JSONObject) {
					r.jsonResponse = (JSONObject) obj;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return r;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(responseCode);
		dest.writeString(jsonResponse.toString());
	}

	public static final Parcelable.Creator<CreateActivityResponse> CREATOR = new Parcelable.Creator<CreateActivityResponse>() {
		public CreateActivityResponse createFromParcel(Parcel in) {
			int rcode = in.readInt();
			String json = in.readString();
			return CreateActivityResponse.parseResponse(rcode, json);
		}

		public CreateActivityResponse[] newArray(int size) {
			return new CreateActivityResponse[size];
		}
	};

	public boolean isValid() {
		return (responseCode == 200);
	}
	
	public Activity getActivity() {
		if (jsonResponse.has("activity")) {
			try {
				return Activity.parse(jsonResponse.getJSONObject("activity"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
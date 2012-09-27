package com.ourlittlegame.responses;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

import com.ourlittlegame.entities.Couple;
import com.ourlittlegame.utilities.MiscUtils;

public class UsResponse implements Parcelable {

	JSONObject jsonResponse;
	int responseCode;

	public static UsResponse parseResponse(int responseCode,
			String responseText) {
		UsResponse r = new UsResponse();
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

	public static final Parcelable.Creator<UsResponse> CREATOR = new Parcelable.Creator<UsResponse>() {
		public UsResponse createFromParcel(Parcel in) {
			int rcode = in.readInt();
			String json = in.readString();
			return UsResponse.parseResponse(rcode, json);
		}

		public UsResponse[] newArray(int size) {
			return new UsResponse[size];
		}
	};

	public boolean isValid() {
		return (responseCode == 200);
	}

	public Couple getCouple() {
		if (jsonResponse.has("couple")) {
			try {
				return Couple.parse(jsonResponse.getJSONObject("couple"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			MiscUtils.println("No couple object found");
		}
		return null;
	}
}

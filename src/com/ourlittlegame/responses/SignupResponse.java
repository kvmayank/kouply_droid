package com.ourlittlegame.responses;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Sample sign-up response:
 * Response: {"couple":{"anniversary":null,"confirmed":false,"created_at":"2012-09-13T12:00:26+01:00",
 * 				"current_cover_id":null,"decoupled":false,"id":44,"score":0,"share_to_facebook":false,
 * 				"suggest_friends":false,"updated_at":"2012-09-13T12:00:26+01:00",
 * 				"user_1_email":null,"user_2_email":null},
 * 			  "location":"/","token":"ekpFMTzj2FbPDFwupLWv"
 * 			}
 */
public class SignupResponse implements Parcelable {

	JSONObject jsonResponse;
	int responseCode;

	public static SignupResponse parseResponse(int responseCode,
			String responseText) {
		SignupResponse r = new SignupResponse();
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

	public boolean loginSuccess() {
		return (responseCode == 200 && jsonResponse.has("token"));
	}

	public boolean isNewUser() {
		return (responseCode == 302);
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(responseCode);
		dest.writeString(jsonResponse.toString());
	}

	public static final Parcelable.Creator<SignupResponse> CREATOR = new Parcelable.Creator<SignupResponse>() {
		public SignupResponse createFromParcel(Parcel in) {
			int rcode = in.readInt();
			String json = in.readString();
			return SignupResponse.parseResponse(rcode, json);
		}

		public SignupResponse[] newArray(int size) {
			return new SignupResponse[size];
		}
	};

	public Object getFacebookUserId() {
		// TODO Auto-generated method stub
		return null;
	}

}

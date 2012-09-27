package com.ourlittlegame.responses;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

/*
 * Sample response on failed sign-in through FB
 * {"location":"https://sharp-lightning-3317.herokuapp.com/users/sign_up",
 * 		"user":{"name":"Mayank Kumar Varshney","email":"kvmayank@gmail.com","gender":"Male"},
 * 		"facebook":{"uid":"540592202"}}
 * 
 * Sample response on successful login
 * {"token":"ekpFMTzj2FbPDFwupLWv","location":"https://sharp-lightning-3317.herokuapp.com/us.json",
 * 		"partnerless":false}
 */
public class SigninResponse implements Parcelable {

	JSONObject jsonResponse;
	int responseCode;

	public static SigninResponse parseResponse(int responseCode,
			String responseText) {
		SigninResponse r = new SigninResponse();
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

	public static final Parcelable.Creator<SigninResponse> CREATOR = new Parcelable.Creator<SigninResponse>() {
		public SigninResponse createFromParcel(Parcel in) {
			int rcode = in.readInt();
			String json = in.readString();
			return SigninResponse.parseResponse(rcode, json);
		}

		public SigninResponse[] newArray(int size) {
			return new SigninResponse[size];
		}
	};

	public String getFacebookUserId() {
		if (jsonResponse.has("facebook")) {
			try {
				JSONObject jsonFb = (JSONObject)jsonResponse.get("facebook");
				if (jsonFb.has("uid")) {
					return jsonFb.getString("uid");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public String getUserName() {
		if (jsonResponse.has("user")) {
			try {
				JSONObject jsonFb = (JSONObject)jsonResponse.get("user");
				if (jsonFb.has("name")) {
					return jsonFb.getString("name");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String getUserEmail() {
		if (jsonResponse.has("user")) {
			try {
				JSONObject jsonFb = (JSONObject)jsonResponse.get("user");
				if (jsonFb.has("email")) {
					return jsonFb.getString("email");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String getUserGender() {
		if (jsonResponse.has("user")) {
			try {
				JSONObject jsonFb = (JSONObject)jsonResponse.get("user");
				if (jsonFb.has("gender")) {
					return jsonFb.getString("gender");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}
	
	public String getAppToken() {
		if (jsonResponse.has("token")) {
			try {
				return jsonResponse.getString("token");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";
	}

	public boolean isPartnerLess() {
		if (jsonResponse.has("partnerless")) {
			try {
				return jsonResponse.getBoolean("partnerless");				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
}

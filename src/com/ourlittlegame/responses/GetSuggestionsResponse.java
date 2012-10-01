package com.ourlittlegame.responses;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

import com.ourlittlegame.entities.Suggestion;
import com.ourlittlegame.entities.Notification;
import com.ourlittlegame.utilities.MiscUtils;

public class GetSuggestionsResponse  implements Parcelable {

	JSONObject jsonResponse;
	int responseCode;

	List<Suggestion> ideas = new ArrayList<Suggestion>();;
	
	public static GetSuggestionsResponse parseResponse(int responseCode,
			String responseText) {
		GetSuggestionsResponse r = new GetSuggestionsResponse();
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

	public static final Parcelable.Creator<GetSuggestionsResponse> CREATOR = new Parcelable.Creator<GetSuggestionsResponse>() {
		public GetSuggestionsResponse createFromParcel(Parcel in) {
			int rcode = in.readInt();
			String json = in.readString();
			return GetSuggestionsResponse.parseResponse(rcode, json);
		}

		public GetSuggestionsResponse[] newArray(int size) {
			return new GetSuggestionsResponse[size];
		}
	};

	public boolean isValid() {
		return (responseCode == 200);
	}
	
	public List<Suggestion> getSuggestions() {
		if (ideas.size() > 0)
			return ideas;
		
		if (jsonResponse.has("suggestions")) {
			try {
				JSONArray jsUsers = jsonResponse.getJSONArray("suggestions");
				for (int i=0;i<jsUsers.length();i++) {
					JSONObject jso = jsUsers.getJSONObject(i);
					if (jso.has("suggestion")) {
						Suggestion u = Suggestion.parse(jso.getJSONObject("suggestion"));
						if (u != null)
							ideas.add(u);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			MiscUtils.println("Suggestions object not found");
			System.out.println(jsonResponse.toString());
		}
		
		return ideas;
	}
}
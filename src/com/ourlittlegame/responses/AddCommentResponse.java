package com.ourlittlegame.responses;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

import com.ourlittlegame.entities.Comment;

public class AddCommentResponse implements Parcelable {

	JSONObject jsonResponse;
	int responseCode;

	public static AddCommentResponse parseResponse(int responseCode,
			String responseText) {
		AddCommentResponse r = new AddCommentResponse();
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

	public static final Parcelable.Creator<AddCommentResponse> CREATOR = new Parcelable.Creator<AddCommentResponse>() {
		public AddCommentResponse createFromParcel(Parcel in) {
			int rcode = in.readInt();
			String json = in.readString();
			return AddCommentResponse.parseResponse(rcode, json);
		}

		public AddCommentResponse[] newArray(int size) {
			return new AddCommentResponse[size];
		}
	};

	public boolean isValid() {
		return (responseCode == 201);
	}

	public Comment getComment() {
		return Comment.parse(jsonResponse);
	}
}
package com.ourlittlegame.responses;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.Parcel;
import android.os.Parcelable;

import com.ourlittlegame.entities.Couple;
import com.ourlittlegame.entities.Notification;
import com.ourlittlegame.utilities.MiscUtils;

/*
 * Response: {"notifications":[{"notification":{"description":"Tushar uploaded a new photo!","created_at":"2012-09-22T21:40:20Z","link_object":null,"link":null,"read":false,"kind":"new_cover_picture"}},{"notification":{"description":"Tushar uploaded a new photo!","created_at":"2012-09-20T08:15:33Z","link_object":null,"link":null,"read":false,"kind":"new_cover_picture"}},{"notification":{"description":"Tushar acknowledged something you did!","created_at":"2012-09-14T07:07:07Z","link_object":"Activity","link":"313","read":false,"kind":"new_activity"}},{"notification":{"description":"Tushar acknowledged something you did!","created_at":"2012-09-14T06:46:56Z","link_object":"Activity","link":"312","read":false,"kind":"new_activity"}},{"notification":{"description":"Tushar acknowledged something you did!","created_at":"2012-09-10T03:13:13Z","link_object":"Activity","link":"307","read":false,"kind":"new_activity"}},{"notification":{"description":"Tushar complimented you!","created_at":"2012-09-08T20:31:14Z","link_object":"Activity","link":"305","read":false,"kind":"new_activity"}},{"notification":{"description":"vini and Vinit accepted your friend request!","created_at":"2012-08-14T06:58:09Z","link_object":"Couple","link":"20","read":false,"kind":"friend_request_accepted"}},{"notification":{"description":"Your friends vini and Vinit just joined Kouply!","created_at":"2012-08-14T01:18:02Z","link_object":"Couple","link":"20","read":false,"kind":"friend_suggested"}},{"notification":{"description":"Your friends vini and Vinit just joined Kouply!","created_at":"2012-08-14T00:37:34Z","link_object":"Couple","link":"20","read":false,"kind":"friend_suggested"}},{"notification":{"description":"Your friends vini and Vinit just joined Kouply!","created_at":"2012-08-14T00:35:28Z","link_object":"Couple","link":"20","read":false,"kind":"friend_suggested"}},{"notification":{"description":"Tushar uploaded a new photo!","created_at":"2012-08-13T01:07:23Z","link_object":"Activity","link":"95","read":false,"kind":"new_activity"}},{"notification":{"description":"Your friends vini and Vinit just joined Kouply!","created_at":"2012-08-10T07:19:01Z","link_object":"Couple","link":"20","read":false,"kind":"friend_suggested"}},{"notification":{"description":"Your friends Tush and Tushar just joined Kouply!","created_at":"2012-08-10T01:45:23Z","link_object":"Couple","link":"24","read":false,"kind":"friend_suggested"}},{"notification":{"description":"Tushar uploaded a new photo!","created_at":"2012-08-08T16:19:12Z","link_object":null,"link":null,"read":false,"kind":"new_cover_picture"}},{"notification":{"description":"Tushar uploaded a new photo!","created_at":"2012-08-06T19:48:27Z","link_object":"Activity","link":"51","read":false,"kind":"new_activity"}},{"notification":{"description":"Tushar complimented you!","created_at":"2012-08-06T19:43:54Z","link_object":"Activity","link":"50","read":false,"kind":"new_activity"}},{"notification":{"description":"testing30_user and testing30_user sent you a friend request!","created_at":"2012-08-04T18:45:18Z","link_object":"Couple","link":"41","read":false,"kind":"friend_requested"}},{"notification":{"description":"testing27_partner and testing27_partner sent you a friend request!","created_at":"2012-08-04T18:38:22Z","link_object":"Couple","link":"22","read":false,"kind":"friend_requested"}},{"notification":{"description":"testing30_user and testing30_user sent you a friend request!","created_at":"2012-08-04T18:26:21Z","link_object":"Couple","link":"41","read":false,"kind":"friend_requested"}},{"notification":{"description":"Tushar acknowledged something you did!","created_at":"2012-08-03T21:28:22Z","link_object":"Activity","link":"47","read":false,"kind":"new_activity"}}]}
 */
public class GetNotificationResponse  implements Parcelable {

	JSONObject jsonResponse;
	int responseCode;

	List<Notification> notifications = new ArrayList<Notification>();;
	
	public static GetNotificationResponse parseResponse(int responseCode,
			String responseText) {
		GetNotificationResponse r = new GetNotificationResponse();
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

	public static final Parcelable.Creator<GetNotificationResponse> CREATOR = new Parcelable.Creator<GetNotificationResponse>() {
		public GetNotificationResponse createFromParcel(Parcel in) {
			int rcode = in.readInt();
			String json = in.readString();
			return GetNotificationResponse.parseResponse(rcode, json);
		}

		public GetNotificationResponse[] newArray(int size) {
			return new GetNotificationResponse[size];
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
	
	public List<Notification> getNotifications() {
		if (notifications.size() > 0)
			return notifications;
		
		if (jsonResponse.has("notifications")) {
			try {
				JSONArray jsUsers = jsonResponse.getJSONArray("notifications");
				for (int i=0;i<jsUsers.length();i++) {
					JSONObject jso = jsUsers.getJSONObject(i);
					if (jso.has("notification")) {
						Notification u = Notification.parse(jso.getJSONObject("notification"));
						if (u != null)
							notifications.add(u);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} else {
			MiscUtils.println("Activities object not found");
			System.out.println(jsonResponse.toString());
		}
		
		return notifications;
	}
}

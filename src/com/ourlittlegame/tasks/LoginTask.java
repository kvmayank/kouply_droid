package com.ourlittlegame.tasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.responses.SigninResponse;
import com.ourlittlegame.utilities.Configuration;
import com.ourlittlegame.utilities.HttpUtils;
import com.ourlittlegame.utilities.MiscUtils;

public class LoginTask extends AsyncTask {
	WeakReference<IListener> listener;
	MyApp myapp;
	
	public interface IListener {
		public void beforeLogin();
		public void onLogin(Object result);
	}
	
	public LoginTask(IListener listener, MyApp app) {
		this.listener = new WeakReference<IListener>(listener);
		this.myapp = app;
	}
	
	@Override
	protected void onPreExecute() {
		IListener l = listener.get();
		if (l != null)
			l.beforeLogin();           	
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		if (params.length == 1) {
			String access_token = (String)params[0];
			myapp.setFacebookToken(access_token);
			nameValuePairs.add(new BasicNameValuePair("facebook[token]", access_token));
		} else if (params.length == 2) {
			String email = (String)params[0];
			String password = (String)params[1];
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
		} 		 
		
		HttpUtils h = new HttpUtils();
		h.doPost(Configuration.getServerApiPath()+"/api/v1/tokens.json",nameValuePairs);
		SigninResponse sr = SigninResponse.parseResponse(h.getResponseCode(),h.getResponseText());

		return sr;
	}
	
	@Override
     protected void onProgressUpdate(Object... progress) {
         //setProgressPercent(progress[0]);
     }

	@Override
     protected void onPostExecute(Object result) {
		IListener l = listener.get();
		if (l != null)
			l.onLogin(result);
     }		
}

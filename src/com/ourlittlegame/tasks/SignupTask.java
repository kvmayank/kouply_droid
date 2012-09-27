package com.ourlittlegame.tasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.responses.SignupResponse;
import com.ourlittlegame.utilities.Configuration;
import com.ourlittlegame.utilities.HttpUtils;

public class SignupTask extends AsyncTask {
	WeakReference<IListener> listener;
	MyApp myapp;
	
	public interface IListener {
		public void beforeSignup();
		public void onSignup(Object result);
	}
	
	public SignupTask(IListener listener, MyApp app) {
		this.listener = new WeakReference<IListener>(listener);
		this.myapp = app;
	}
	
	@Override
	protected void onPreExecute() {
		IListener l = listener.get();
		if (l != null)
			l.beforeSignup();           	
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		SignupResponse sr = null;
		HashMap fields = (HashMap)params[0];

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(); 
		nameValuePairs.add(new BasicNameValuePair("facebook[token]", (String)fields.get("facebook[token]")));
		nameValuePairs.add(new BasicNameValuePair("facebook[uid]", (String)fields.get("facebook[uid]")));
		
		nameValuePairs.add(new BasicNameValuePair("user[email]", (String)fields.get("email")));
		nameValuePairs.add(new BasicNameValuePair("user[name]", (String)fields.get("name")));
		nameValuePairs.add(new BasicNameValuePair("user[gender]", (String)fields.get("gender")));
		nameValuePairs.add(new BasicNameValuePair("user[partner_email]", (String)fields.get("partneremail")));
		nameValuePairs.add(new BasicNameValuePair("user[time_zone_offset]", (String)fields.get("time_zone_offset")));
		
		HttpUtils h = new HttpUtils();
		h.doPost(Configuration.getServerApiPath()+"/users.json",nameValuePairs);
		sr = SignupResponse.parseResponse(h.getResponseCode(),h.getResponseText());

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
			l.onSignup(result);
     }		
}

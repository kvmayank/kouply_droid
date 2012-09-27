package com.ourlittlegame.tasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.responses.UsResponse;
import com.ourlittlegame.utilities.Configuration;
import com.ourlittlegame.utilities.HttpUtils;

public class GetActivitiesTask extends AsyncTask {
	WeakReference<IListener> listener;
	MyApp myapp;
	
	public interface IListener {
		public void beforeActivitiesLoad();
		public void onActivitiesLoad(Object result);
	}
	
	public GetActivitiesTask(IListener listener, MyApp app) {
		this.listener = new WeakReference<IListener>(listener);
		this.myapp = app;
	}
	
	@Override
	protected void onPreExecute() {
		IListener l = listener.get();
		if (l != null)
			l.beforeActivitiesLoad();           	
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("auth_token", myapp.getAppToken()));
		nameValuePairs.add(new BasicNameValuePair("timestamp", ""+(new Date().getTime())));
		
		HttpUtils h = new HttpUtils();
		//h.doPost(Configuration.getServerApiPath()+"/us.json",nameValuePairs);
		h.doGet(Configuration.getServerApiPath()+"/us.json?auth_token="+myapp.getAppToken()+"&ts="+(new Date().getTime()));
		UsResponse sr = UsResponse.parseResponse(h.getResponseCode(),h.getResponseText());

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
			l.onActivitiesLoad(result);
     }		
}
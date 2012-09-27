package com.ourlittlegame.tasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.responses.AddCommentResponse;
import com.ourlittlegame.utilities.Configuration;
import com.ourlittlegame.utilities.HttpUtils;

public class AddCommentTask extends AsyncTask {
	WeakReference<IListener> listener;
	MyApp myapp;
	int activityID;
	
	public interface IListener {
		public void beforeCommentAdded();
		public void onCommentAdded(Object result);
	}
	
	public AddCommentTask(IListener listener, MyApp app, int activityID) {
		this.listener = new WeakReference<IListener>(listener);
		this.myapp = app;
		this.activityID = activityID;
	}
	
	@Override
	protected void onPreExecute() {
		IListener l = listener.get();
		if (l != null)
			l.beforeCommentAdded();           	
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		String msg = (String)params[0];
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("auth_token", myapp.getAppToken()));
		nameValuePairs.add(new BasicNameValuePair("comment[message]", msg));
		
		HttpUtils h = new HttpUtils();
		h.doPost(Configuration.getServerApiPath()+"/activities/"+this.activityID+"/comments.json",nameValuePairs);
		AddCommentResponse sr = AddCommentResponse.parseResponse(h.getResponseCode(),h.getResponseText());
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
			l.onCommentAdded(result);
     }		
}
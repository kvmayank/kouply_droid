package com.ourlittlegame.tasks;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.responses.CreateActivityResponse;
import com.ourlittlegame.utilities.Configuration;
import com.ourlittlegame.utilities.HttpUtils;

public class CreateActivityTask  extends AsyncTask {
	WeakReference<IListener> listener;
	MyApp myapp;
	List<NameValuePair> nameValuePairs;
	Map<String,File> assets;
	
	public interface IListener {
		public void beforeActivityCreated();
		public void onActivityCreated(Object result);
	}
	
	public CreateActivityTask(IListener listener, MyApp app, List<NameValuePair> nameValuePairs, Map<String,File> assets) {
		this.listener = new WeakReference<IListener>(listener);
		this.myapp = app;
		this.nameValuePairs = nameValuePairs;
		this.assets = assets;
	}
	
	@Override
	protected void onPreExecute() {
		IListener l = listener.get();
		if (l != null)
			l.beforeActivityCreated();           	
	}
	
	@Override
	protected Object doInBackground(Object... params) {
		HttpUtils h = new HttpUtils();
		nameValuePairs.add(new BasicNameValuePair("auth_token", myapp.getAppToken()));
		h.doPostMultipart(Configuration.getServerApiPath()+"/activities.json",this.nameValuePairs,this.assets);
		CreateActivityResponse sr = CreateActivityResponse.parseResponse(h.getResponseCode(),h.getResponseText());
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
			l.onActivityCreated(result);
     }		
}
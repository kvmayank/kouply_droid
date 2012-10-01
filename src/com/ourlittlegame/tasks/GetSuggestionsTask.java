package com.ourlittlegame.tasks;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.responses.GetSuggestionsResponse;
import com.ourlittlegame.utilities.Configuration;
import com.ourlittlegame.utilities.HttpUtils;

public class GetSuggestionsTask extends AsyncTask {
	WeakReference<IListener> listener;
	MyApp myapp;

	public interface IListener {
		public void beforeIdeasLoad();

		public void onIdeasLoad(Object result);
	}

	public GetSuggestionsTask(IListener listener, MyApp app) {
		this.listener = new WeakReference<IListener>(listener);
		this.myapp = app;
	}

	@Override
	protected void onPreExecute() {
		IListener l = listener.get();
		if (l != null)
			l.beforeIdeasLoad();
	}

	@Override
	protected Object doInBackground(Object... params) {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("auth_token", myapp
				.getAppToken()));

		HttpUtils h = new HttpUtils();
		h.doGet(Configuration.getServerApiPath()
				+ "/ideas/1/suggestions.json?auth_token=" + myapp.getAppToken()
				+ "&ts=" + (new Date().getTime()));
		GetSuggestionsResponse sr = GetSuggestionsResponse.parseResponse(
				h.getResponseCode(), h.getResponseText());

		return sr;
	}

	@Override
	protected void onProgressUpdate(Object... progress) {
		// setProgressPercent(progress[0]);
	}

	@Override
	protected void onPostExecute(Object result) {
		IListener l = listener.get();
		if (l != null)
			l.onIdeasLoad(result);
	}
}
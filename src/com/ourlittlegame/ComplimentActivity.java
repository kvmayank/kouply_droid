package com.ourlittlegame;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;

import com.ourlittlegame.adaptors.SuggestionListAdaptor;
import com.ourlittlegame.entities.Suggestion;
import com.ourlittlegame.responses.GetSuggestionsResponse;
import com.ourlittlegame.tasks.GetSuggestionsTask;
import com.ourlittlegame.utilities.MiscUtils;

public class ComplimentActivity extends Activity 
	implements GetSuggestionsTask.IListener, SuggestionListAdaptor.IListener {

	EditText txt;
	ListView suggestionList;
	SuggestionListAdaptor suggestionAdaptor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compliment);

		// link controls
		txt = (EditText) findViewById(R.id.txt);

		/*
		 * final Button button = (Button) findViewById(R.id.button);
		 * button.setOnClickListener(new View.OnClickListener() { public void
		 * onClick(View v) { doLogin(v); } });
		 */
		MyApp myapp = (MyApp) getApplicationContext();
		this.suggestionAdaptor = new SuggestionListAdaptor(this, R.layout.suggestionslistitem, 
				myapp.getSuggestions(), myapp, this);
		suggestionList = (ListView)findViewById(android.R.id.list);	
		suggestionList.setAdapter(this.suggestionAdaptor);
		
		GetSuggestionsTask lt = new GetSuggestionsTask(ComplimentActivity.this, myapp);
		lt.execute();
	}

	public void beforeIdeasLoad() {
	}

	public void onIdeasLoad(Object result) {
		GetSuggestionsResponse ur = (GetSuggestionsResponse)result;
		MyApp myapp = (MyApp) getApplicationContext();
		if (ur.isValid()) {
			List<Suggestion> suggestions = ur.getSuggestions();		
			if (suggestions != null) {
				MiscUtils.println("Updating suggestions object in the app");
				myapp.setSuggestions(suggestions);
			} 
		} else {
			MiscUtils.println("Invalid getNotifications response...");
		}		
		suggestionAdaptor.set(myapp.getSuggestions());
	}
}

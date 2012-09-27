package com.ourlittlegame;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ourlittlegame.adaptors.FeedListAdaptor;
import com.ourlittlegame.entities.Activity;
import com.ourlittlegame.entities.Couple;
import com.ourlittlegame.entities.User;
import com.ourlittlegame.responses.UsResponse;
import com.ourlittlegame.tasks.GetActivitiesTask;
import com.ourlittlegame.utilities.ImageManager;
import com.ourlittlegame.utilities.MiscUtils;

public class FeedActivity extends ListActivity implements
		FeedListAdaptor.IListener, GetActivitiesTask.IListener {
	FeedListAdaptor m_adapter;
	ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed);

		MyApp myapp = (MyApp) getApplicationContext();
		
		if (myapp.getCouple() != null)
			this.m_adapter = new FeedListAdaptor(this, R.layout.feedlistitem,
					myapp.getCouple().getActivities(), myapp, this);
		else
			this.m_adapter = new FeedListAdaptor(this, R.layout.feedlistitem,
					new ArrayList<Activity>(), myapp, this);
		setListAdapter(this.m_adapter);

		ListView lv = (ListView)findViewById(android.R.id.list);
		lv.setOnScrollListener(new OnScrollListener() {
			
			private int currentScrollState;
			private int currentFirstVisibleItem;
			private int currentVisibleItemCount;

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				this.currentScrollState = scrollState;
				//if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
				/*if (true) {
			        *//*** In this way I detect if there's been a scroll which has completed ***//*
			        *//*** do the work! ***//*
					if (this.currentFirstVisibleItem > 3)
						findViewById(R.id.feedbannercontainer).setVisibility(View.GONE);
					else
						findViewById(R.id.feedbannercontainer).setVisibility(View.VISIBLE);
					
					System.out.println("Requesting redraw...");
					findViewById(R.id.feedbannercontainer).invalidate();
			    }*/
				
			}
			
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				/*if(this.currentFirstVisibleItem > firstVisibleItem){
			        //scrolling to top of list
			    }else if (this.currentFirstVisibleItem < firstVisibleItem){
			        //scrolling to bottom of list
			    }

			    this.currentFirstVisibleItem = firstVisibleItem;
			    this.currentVisibleItemCount = visibleItemCount; 
			    
			    if (this.currentFirstVisibleItem > 1) {
					findViewById(R.id.feedbannercontainer).setVisibility(View.GONE);
					ListView lv = (ListView)findViewById(android.R.id.list);
			    } else {
					findViewById(R.id.feedbannercontainer).setVisibility(View.VISIBLE);
			    }
			    
				System.out.println("Requesting redraw...");
				findViewById(R.id.feedbannercontainer).invalidate();*/
			}
		});
		
		// Post the information
		GetActivitiesTask lt = new GetActivitiesTask(FeedActivity.this, myapp);
		lt.execute();
	}

	public void beforeActivitiesLoad() {
		TextView tv = (TextView) findViewById(android.R.id.empty);
		tv.setText("Loading ...");
	}

	public void onActivitiesLoad(Object result) {
		TextView tv = (TextView) findViewById(android.R.id.empty);
		tv.setText("No recent activities ...");
		
		UsResponse ur = (UsResponse)result;
		MyApp myapp = (MyApp) getApplicationContext();
		if (ur.isValid()) {
			Couple c = ur.getCouple();			
			if (c != null) {
				MiscUtils.println("Updating couple object in the app");
				myapp.setCouple(c);
			} 
		} else {
			MiscUtils.println("Invalid getUs response...");
		}		
		updateDisplay();
	}

	private void updateDisplay() {
		MyApp myapp = (MyApp) getApplicationContext();
		if (myapp.getCouple() != null) {
			Couple c = myapp.getCouple();
			User u = c.getCurrentUser();
			/*View v = findViewById(R.id.feedbanner);
			if (u != null) {
				ImageView iv = (ImageView) v.findViewById(R.id.userpic);			   
				ImageManager.showPicture(u.getID(), 
						u.getPicture(), iv, myapp.getExternalStorageFolder());
			}
			User p = c.getPartner();
			if (p != null) {
				ImageView iv = (ImageView) v.findViewById(R.id.partnerpic);			   
				ImageManager.showPicture(p.getID(), 
						p.getPicture(), iv, myapp.getExternalStorageFolder());
			}
			((TextView) v.findViewById(R.id.totalscore)).setText(""+c.getScore());*/
			
			MiscUtils.println("Updating list display");
			m_adapter.set(myapp.getCouple().getActivities());
		}
	}

	final int RESPONSE_CODE_ADDCOMMENT = 1;
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESPONSE_CODE_ADDCOMMENT) {
			updateDisplay();
		}
	}
	
	public void onAddComment(int activityID) {
		Intent myIntent = new Intent(this, ActivityDetailActivity.class);
		myIntent.putExtra("activityID", activityID);
		startActivityForResult(myIntent,RESPONSE_CODE_ADDCOMMENT);
	}
}
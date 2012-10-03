package com.ourlittlegame;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ourlittlegame.adaptors.FeedListAdaptor;
import com.ourlittlegame.adaptors.MenuListAdaptor;
import com.ourlittlegame.adaptors.NotificationListAdaptor;
import com.ourlittlegame.entities.Activity;
import com.ourlittlegame.entities.Couple;
import com.ourlittlegame.entities.Notification;
import com.ourlittlegame.entities.User;
import com.ourlittlegame.responses.GetNotificationResponse;
import com.ourlittlegame.responses.UsResponse;
import com.ourlittlegame.tasks.GetActivitiesTask;
import com.ourlittlegame.tasks.GetNotificationsTask;
import com.ourlittlegame.utilities.ImageManager;
import com.ourlittlegame.utilities.MiscUtils;
import com.ourlittlegame.utilities.MyHorizontalScrollView;
import com.ourlittlegame.utilities.MyHorizontalScrollView.SizeCallback;

public class SlidingFeedActivity extends android.app.Activity implements
		FeedListAdaptor.IListener, MenuListAdaptor.IListener, 
		GetActivitiesTask.IListener, GetNotificationsTask.IListener, NotificationListAdaptor.IListener {
    
	MyHorizontalScrollView scrollView;
    View menu;
    View app;
    View notifications;
    
    ImageView btnMenu;
    ImageView btnNotifications;
    
    boolean menuOut = false;
    Handler handler = new Handler();
    int btnWidth;

	FeedListAdaptor m_adapter;
	MenuListAdaptor m_menuAdapter;
	NotificationListAdaptor m_NotificationsAdapter;
	ProgressDialog pd;

	ListView feedList;
	ListView menuList;
	ListView notificationsList;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LayoutInflater inflater = LayoutInflater.from(this);
        scrollView = (MyHorizontalScrollView) inflater.inflate(R.layout.scrolling_app_canvas, null);
        setContentView(scrollView);

        menu = inflater.inflate(R.layout.appmenu, null);
        app = inflater.inflate(R.layout.feed, null);
        notifications = inflater.inflate(R.layout.appnotifications, null);
        
		MyApp myapp = (MyApp) getApplicationContext();
		
		if (myapp.getCouple() != null)
			this.m_adapter = new FeedListAdaptor(this, R.layout.feedlistitem,
					myapp.getCouple().getActivities(), myapp, this);
		else
			this.m_adapter = new FeedListAdaptor(this, R.layout.feedlistitem,
					new ArrayList<Activity>(), myapp, this);
		this.m_menuAdapter = new MenuListAdaptor(this, R.layout.menuitemrow, 
					myapp.getMenuItems(), myapp, this);
		this.m_NotificationsAdapter = new NotificationListAdaptor(this, R.layout.notificationitemrow, 
				myapp.getNotifications(), myapp, this);
		
		feedList = (ListView)app.findViewById(android.R.id.list);				
		ViewGroup feedheader = (ViewGroup)inflater.inflate(R.layout.banner, feedList, false);
		feedList.addHeaderView(feedheader, null, true);		
		feedList.setAdapter(this.m_adapter);
		
		menuList = (ListView) menu.findViewById(R.id.list);
        menuList.setAdapter(m_menuAdapter);

        notificationsList = (ListView)notifications.findViewById(R.id.list);
        notificationsList.setAdapter(m_NotificationsAdapter);
        
        btnMenu = (ImageView) app.findViewById(R.id.bannerlayout).findViewById(R.id.feed_menu);
        btnMenu.setOnClickListener(new ClickListenerForScrolling(scrollView, app, menu, ClickListenerForScrolling.DIRECTION_LEFT));

        btnNotifications = (ImageView) app.findViewById(R.id.bannerlayout).findViewById(R.id.feed_notifications);
        btnNotifications.setOnClickListener(new ClickListenerForScrolling(scrollView, app, notifications, ClickListenerForScrolling.DIRECTION_RIGHT));
        
        final View[] children = new View[] { menu, app, notifications };

        // Scroll to app (view[1]) when layout finished.
        int scrollToViewIdx = 1;
        scrollView.initViews(children, scrollToViewIdx, new SizeCallbackForMenu());

		// Post the information
		(new GetActivitiesTask(SlidingFeedActivity.this, myapp)).execute();
		(new GetNotificationsTask(SlidingFeedActivity.this, myapp)).execute();
	}

	public void beforeActivitiesLoad() {
		TextView tv = (TextView) app.findViewById(android.R.id.empty);
		tv.setText("Loading ...");
	}

	public void onActivitiesLoad(Object result) {
		TextView tv = (TextView) app.findViewById(android.R.id.empty);
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
			View v = app.findViewById(R.id.bannerlayout);
			
			if (u != null) {
				View appmenu = findViewById(R.id.appmenuheader);
				ImageView iv = (ImageView) v.findViewById(R.id.userpic);			   
				ImageManager.showPicture(u.getID(), 
						u.getPicture(), iv, myapp.getExternalStorageFolder());
				
				iv = (ImageView) appmenu.findViewById(R.id.userimg);
				ImageManager.showPicture(u.getID(), 
						u.getPicture(), iv, myapp.getExternalStorageFolder());
				
				((TextView) appmenu.findViewById(R.id.username)).setText(u.getName());
			}
			User p = c.getPartner();
			if (p != null) {
				ImageView iv = (ImageView) v.findViewById(R.id.partnerpic);			   
				ImageManager.showPicture(p.getID(), 
						p.getPicture(), iv, myapp.getExternalStorageFolder());
			}
			((TextView) v.findViewById(R.id.totalscore)).setText(""+c.getScore());
			
			MiscUtils.println("Updating list display");
			m_adapter.set(myapp.getCouple().getActivities());
		}
		m_NotificationsAdapter.set(myapp.getNotifications());
	}

	final int RESPONSE_CODE_ADDCOMMENT = 1;
	final int RESPONSE_CODE_CREATEACTIVITY = 2;
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESPONSE_CODE_ADDCOMMENT || requestCode == RESPONSE_CODE_CREATEACTIVITY) {
			updateDisplay();
		}
	}
	
	public void onAddComment(int activityID) {
		Intent myIntent = new Intent(this, ActivityDetailActivity.class);
		myIntent.putExtra("activityID", activityID);
		startActivityForResult(myIntent,RESPONSE_CODE_ADDCOMMENT);
	}
	
    /**
     * Helper for examples with a HSV that should be scrolled by a menu View's width.
     */
    static class ClickListenerForScrolling implements OnClickListener {
        public static final int DIRECTION_LEFT = 0;
        public static final int DIRECTION_RIGHT = 1;
        
		HorizontalScrollView scrollView;
        View menu;
        View center;
        int direction;
        /**
         * Menu must NOT be out/shown to start with.
         */
        boolean menuOut = false;

        public ClickListenerForScrolling(HorizontalScrollView scrollView, View center, View menu, int direction) {
            super();
            this.scrollView = scrollView;
            this.menu = menu;
            this.center = center;
            this.direction = direction;
        }

        @Override
        public void onClick(View v) {
            int menuWidth = menu.getMeasuredWidth();
            int centerWidth = center.getMeasuredWidth();
            
            // Ensure menu is visible
            menu.setVisibility(View.VISIBLE);

            if (!menuOut) {
                // Scroll to 0 to reveal menu
            	if (this.direction == DIRECTION_LEFT) {
            		scrollView.smoothScrollTo(0, 0);
            	} else {
            		//scrollView.smoothScrollTo(menuWidth+centerWidth, 0);
            		scrollView.smoothScrollTo(menuWidth*2, 0);
            	}                
            } else {
                // Scroll to menuWidth so menu isn't on screen.
            	if (this.direction == DIRECTION_LEFT) {
            		scrollView.smoothScrollTo(menuWidth, 0);
            	} else {
            		scrollView.smoothScrollTo(menuWidth, 0);
            	}                                
            }
            menuOut = !menuOut;
        }
    }

    /**
     * Helper that remembers the width of the 'slide' button, so that the 'slide' button remains in view, even when the menu is
     * showing.
     */
    static class SizeCallbackForMenu implements SizeCallback {
        int btnWidth;

        public SizeCallbackForMenu() {
            super();
        }

        @Override
        public void onGlobalLayout() {
            btnWidth = 80;
        }

        @Override
        public void getViewSize(int idx, int w, int h, int[] dims) {
            dims[0] = w;
            dims[1] = h;
            if (idx != 1) {
                dims[0] = w - btnWidth;
            }
        }
    }

	@Override
	public void beforeNotificationsLoad() {
		TextView tv = (TextView) notifications.findViewById(android.R.id.empty);
		tv.setText("Loading ...");
	}

	@Override
	public void onNotificationsLoad(Object result) {
		TextView tv = (TextView) notifications.findViewById(android.R.id.empty);
		tv.setText("No recent activities ...");
		
		GetNotificationResponse ur = (GetNotificationResponse)result;
		MyApp myapp = (MyApp) getApplicationContext();
		if (ur.isValid()) {
			List<Notification> notifications = ur.getNotifications();		
			if (notifications != null) {
				MiscUtils.println("Updating notifications object in the app");
				myapp.setNotifications(notifications);
			} 
		} else {
			MiscUtils.println("Invalid getNotifications response...");
		}		
		updateDisplay();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionpad, menu);
		return true;
	}		
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		MyApp myapp = ((MyApp)getApplicationContext());
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.acknowledge:
		    {
		    	Intent myIntent = new Intent(getApplicationContext(), ComplimentActivity.class);
		    	myIntent.putExtra("kind", "Acknowledge");
	            startActivityForResult(myIntent, RESPONSE_CODE_CREATEACTIVITY);
		    	
		    }
	        return true;
	    case R.id.compliment:
		    {
		    	Intent myIntent = new Intent(getApplicationContext(), ComplimentActivity.class);
		    	myIntent.putExtra("kind", "Compliment");
	            startActivityForResult(myIntent, RESPONSE_CODE_CREATEACTIVITY);	    	
		    }
	        return true;
	    case R.id.message:
	    	return true;	    	
	    case R.id.photo:
		    {
		    	Intent myIntent = new Intent(getApplicationContext(), ComplimentActivity.class);
		    	myIntent.putExtra("kind", "Photo");
	            startActivityForResult(myIntent, RESPONSE_CODE_CREATEACTIVITY);
		    	
		    }
	        return true;   	
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

}
package com.ourlittlegame.adaptors;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.R;
import com.ourlittlegame.entities.Activity;
import com.ourlittlegame.entities.Comment;
import com.ourlittlegame.entities.User;
import com.ourlittlegame.utilities.DateUtils;
import com.ourlittlegame.utilities.ImageManager;
import com.ourlittlegame.utilities.MiscUtils;


public class FeedListAdaptor extends ArrayAdapter<Activity> {
	WeakReference<IListener> listenerReference;
	List<Activity> items;
	MyApp myapp;
	Context context;
	
	public interface IListener {
		void onAddComment(int activityID);
	}
	
	public FeedListAdaptor(Context context, int textViewResourceId,
			List<Activity> items, MyApp app, IListener listener) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.myapp = app;
		this.context = context;
		this.listenerReference = new WeakReference<IListener>(listener);
	}

	public int getCount() {
		return items.size();
	}
	public Activity getItem(int position) {		
		return items.get(position);
	}
	public void set(List<Activity> feeds) {
		items = feeds;
		notifyDataSetChanged();
	}
	
	public void add(List<Activity> feeds) {
		items.addAll(feeds);
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null)
			v = vi.inflate(R.layout.feedlistitem, null);
		
		final Activity a = items.get(position);
		if (a != null) {
			ImageView iv = (ImageView) v.findViewById(R.id.userimg);			   
			ImageManager.showPicture(a.getUser().getID(), 
					a.getUser().getPicture(), iv, myapp.getExternalStorageFolder());
			((TextView) v.findViewById(R.id.toptext)).setText(a.getCaption());
			((TextView) v.findViewById(R.id.middletext)).setText(DateUtils.relativeDate(a.getCreatedAt()));
			((TextView) v.findViewById(R.id.scoretext)).setText(a.getPoints()+"");
			
			ImageView av = (ImageView) v.findViewById(R.id.activitypicture);
			av.setImageBitmap(null);
			if (MiscUtils.removeNull(a.getAsset()).length() > 0) {
				float ratio = 0;
				try {
					ratio = Float.parseFloat(a.getRatio());
				} catch (Exception e) {
					
				}								
				av.setVisibility(View.VISIBLE);
				if (ratio == 0)
					ImageManager.showPicture(a.getAsset(), av, myapp.getExternalStorageFolder());
				else
					ImageManager.showPicture(a.getAsset(), av, myapp.getExternalStorageFolder(), ratio);
			} else {
				av.setVisibility(View.GONE);				
			}
			
			LinearLayout viewCommentsView = (LinearLayout)v.findViewById(R.id.viewcommentsview);
			viewCommentsView.removeAllViewsInLayout();
			List<Comment> comments = a.getComments();
			if (comments.size() > 0) {
				//v.findViewById(R.id.nocommentsview).setVisibility(View.GONE);
				v.findViewById(R.id.viewcommentsview).setVisibility(View.VISIBLE);
								
				for (Iterator<Comment> it = comments.iterator();it.hasNext();) {
					Comment c = it.next();
					User cuser = c.getUser();
					if (cuser == null) {
						int userId = c.getUserId();
						if (userId > 0) 
							cuser = myapp.findUserById(userId);
					}
					
					View cv = vi.inflate(R.layout.commentlistitem, null);
					((TextView) cv.findViewById(R.id.toptext)).setText(c.getMessage());
					((TextView) cv.findViewById(R.id.middletext)).setText(DateUtils.relativeDate(c.getCreatedAt()));
					
					if (cuser != null) {
						ImageView cuv = (ImageView) cv.findViewById(R.id.userimg);
						ImageManager.showPicture(cuser.getID(),cuser.getPicture(), cuv, myapp.getExternalStorageFolder());						
					}
										
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
			        	     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			        layoutParams.setMargins(0,3,0,0);
					viewCommentsView.addView(cv,layoutParams);				
				}
			} else {
				//v.findViewById(R.id.nocommentsview).setVisibility(View.VISIBLE);
				v.findViewById(R.id.viewcommentsview).setVisibility(View.GONE);
			}
			
			ImageView addCommentImg = (ImageView) v.findViewById(R.id.addcommentimg);
			TextView addCommentTxt = (TextView) v.findViewById(R.id.addcomment);
			android.view.View.OnClickListener l = new android.view.View.OnClickListener() {				
				public void onClick(View v) {
					IListener l = listenerReference.get();
					if (l != null) {
						System.out.println("Add comment clicked...");
						l.onAddComment(a.getID());
					}
				}
			}; 
			addCommentTxt.setOnClickListener(l);
			addCommentImg.setOnClickListener(l);
		}
		return v;
	}
}

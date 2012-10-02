package com.ourlittlegame.adaptors;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.R;
import com.ourlittlegame.entities.Notification;
import com.ourlittlegame.entities.User;
import com.ourlittlegame.utilities.ImageManager;

public class NotificationListAdaptor extends ArrayAdapter<Notification> {
	WeakReference<IListener> listenerReference;
	List<Notification> items;
	MyApp myapp;
	Context context;

	public interface IListener {
	}

	public NotificationListAdaptor(Context context, int textViewResourceId,
			List<Notification> items, MyApp app, IListener listener) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.myapp = app;
		this.context = context;
		this.listenerReference = new WeakReference<IListener>(listener);
	}

	public int getCount() {
		return items.size();
	}

	public Notification getItem(int position) {
		return items.get(position);
	}

	public void set(List<Notification> feeds) {
		items = feeds;
		notifyDataSetChanged();
	}

	public void add(List<Notification> feeds) {
		items.addAll(feeds);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null)
			v = vi.inflate(R.layout.notificationitemrow, null);

		final Notification a = items.get(position);
		if (a != null) {
			ImageView iv = (ImageView) v.findViewById(R.id.icon);
			if ("partner_joined".equals(a.getKind())) {
				User u = myapp.getCouple().getCurrentUser();
				ImageManager.showPicture(u.getID(),u.getPicture(), iv, myapp.getExternalStorageFolder());
			} else if ("new_acknowledgement".equals(a.getKind()) || "new_friend_acknowledgement".equals(a.getKind())) {
				iv.setImageResource(R.drawable.notifications_ack);
			} else if ("new_compliment".equals(a.getKind()) || "new_friend_compliment".equals(a.getKind())) {
				iv.setImageResource(R.drawable.notifications_compliment);
			} else if ("new_photo".equals(a.getKind()) || "new_friend_photo".equals(a.getKind())) {
				iv.setImageResource(R.drawable.notifications_photo);
			} else if ("new_cover_picture".equals(a.getKind())) {
				iv.setImageResource(R.drawable.notifications_photo);
			} else if ("new_video".equals(a.getKind()) || "new_friend_video".equals(a.getKind())) {
				iv.setImageResource(R.drawable.notifications_video);
			} else if ("leaderboard_change".equals(a.getKind())) {
				iv.setImageResource(R.drawable.notification_trend);
			} else if ("friend_requested".equals(a.getKind()) || "friend_suggested".equals(a.getKind()) 
					|| "friend_request_accepted".equals(a.getKind())) {
				iv.setImageResource(R.drawable.couple);
			}
			((TextView) v.findViewById(R.id.text)).setText(a
					.getDescription());
		}
		return v;
	}
}
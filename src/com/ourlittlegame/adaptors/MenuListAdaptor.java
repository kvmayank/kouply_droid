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

public class MenuListAdaptor extends ArrayAdapter<MenuListAdaptor.MenuItem> {
	public static class MenuItem {
		public int iconResouceId;
		public String text;
		public MenuItem(int iconId, String text) {
			this.iconResouceId = iconId;
			this.text = text;
		}
	}
	
	WeakReference<IListener> listenerReference;
	List<MenuListAdaptor.MenuItem> items;
	MyApp myapp;
	Context context;
	
	public interface IListener {
		void onAddComment(int activityID);
	}
	
	public MenuListAdaptor(Context context, int textViewResourceId,
			List<MenuListAdaptor.MenuItem> items, MyApp app, IListener listener) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.myapp = app;
		this.context = context;
		this.listenerReference = new WeakReference<IListener>(listener);
	}

	public int getCount() {
		return items.size();
	}
	public MenuListAdaptor.MenuItem getItem(int position) {		
		return items.get(position);
	}
	public void set(List<MenuListAdaptor.MenuItem> feeds) {
		items = feeds;
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null)
			v = vi.inflate(R.layout.menuitemrow, null);
		
		final MenuListAdaptor.MenuItem a = items.get(position);
		if (a != null) {
			ImageView iv = (ImageView) v.findViewById(R.id.icon);	
			if (a.iconResouceId > 0) {
				iv.setImageResource(a.iconResouceId);
				iv.setVisibility(View.VISIBLE);
			} else 
				iv.setVisibility(View.INVISIBLE);
			
			((TextView) v.findViewById(R.id.text)).setText(a.text);
		}
		return v;
	}
}
package com.ourlittlegame.adaptors;

import java.lang.ref.WeakReference;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ourlittlegame.MyApp;
import com.ourlittlegame.R;
import com.ourlittlegame.entities.Suggestion;

public class SuggestionListAdaptor  extends ArrayAdapter<Suggestion> {
	WeakReference<IListener> listenerReference;
	List<Suggestion> items;
	MyApp myapp;
	Context context;
	
	public interface IListener {
	}
	
	public SuggestionListAdaptor(Context context, int textViewResourceId,
			List<Suggestion> items, MyApp app, IListener listener) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.myapp = app;
		this.context = context;
		this.listenerReference = new WeakReference<IListener>(listener);
	}

	public int getCount() {
		return items.size();
	}
	public Suggestion getItem(int position) {		
		return items.get(position);
	}
	public void set(List<Suggestion> feeds) {
		items = feeds;
		notifyDataSetChanged();
	}
	
	public void add(List<Suggestion> feeds) {
		items.addAll(feeds);
		notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = convertView;
		if (v == null)
			v = vi.inflate(R.layout.suggestionslistitem, null);
		
		final Suggestion a = items.get(position);
		if (a != null) {
/*			ImageView iv = (ImageView) v.findViewById(R.id.userimg);			   
			ImageManager.showPicture(a.getUser().getID(), 
					a.getUser().getPicture(), iv, myapp.getExternalStorageFolder());
*/			((TextView) v.findViewById(R.id.text)).setText(a.getDescription());
		}
		return v;
	}
}
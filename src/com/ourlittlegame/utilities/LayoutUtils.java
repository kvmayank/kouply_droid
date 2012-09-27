package com.ourlittlegame.utilities;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class LayoutUtils {
	public interface ITextClickListener {
		public void handleClick(String actionString);
	}

	public static class MyClickableSpan extends ClickableSpan {
		String actionString;
		WeakReference<ITextClickListener> listener;

		public MyClickableSpan(ITextClickListener listener) {
			this.listener = new WeakReference<ITextClickListener>(listener);
		}

		public void setActionString(String s) {
			this.actionString = s;
		}

		@Override
		public void onClick(View widget) {
			ITextClickListener l = listener.get();
			if (l != null)
				l.handleClick(this.actionString);
		}

	}

	/**
	 * Given either a Spannable String or a regular String and a token, apply
	 * the given CharacterStyle to the span between the tokens, and also remove
	 * tokens.
	 * <p>
	 * For example, {@code setSpanBetweenTokens("Hello ##world##!", "##",
	 * new ForegroundColorSpan(0xFFFF0000));} will return a CharSequence
	 * {@code "Hello world!"} with {@code world} in red.
	 * 
	 * @param text
	 *            The text, with the tokens, to adjust.
	 * @param token
	 *            The token string; there should be at least two instances of
	 *            token in text.
	 * @param cs
	 *            The style to apply to the CharSequence. WARNING: You cannot
	 *            send the same two instances of this parameter, otherwise the
	 *            second call will remove the original span.
	 * @return A Spannable CharSequence with the new style applied.
	 * 
	 * @see http
	 *      ://developer.android.com/reference/android/text/style/CharacterStyle
	 *      .html
	 */
	public static CharSequence setSpanBetweenTokens(String text, String token,
			StyleFactory sf) {

		String ftext = text.replaceAll(token, "");
		System.out.println(ftext);

		SpannableStringBuilder ssb = new SpannableStringBuilder(ftext);
		int trunc_length = 0;
		int num_tokens_found = 0;
		while (text.length() > 0 && text.indexOf(token) != -1) {
			int idx = text.indexOf(token);
			text = text.substring(idx + token.length());

			int obegin = (trunc_length - num_tokens_found * token.length())
					+ idx;
			trunc_length += idx + token.length();
			num_tokens_found++;

			if (text.indexOf(token) != -1) {
				int nidx = text.indexOf(token);
				text = text.substring(nidx + token.length());

				int oend = (trunc_length - num_tokens_found * token.length())
						+ nidx;
				trunc_length += nidx + token.length();
				num_tokens_found++;

				CharacterStyle[] cs = sf.getStyles();
				for (CharacterStyle c : cs) {
					System.out.println("Setting span for "
							+ ftext.substring(obegin, oend));
					if (c instanceof LayoutUtils.MyClickableSpan)
						((LayoutUtils.MyClickableSpan) c).setActionString(ftext
								.substring(obegin, oend));
					ssb.setSpan(c, obegin, oend, 0);
				}
			}
		}

		return ssb;
	}

	public static void setViewWidths(View view, View[] views) {
		int w = view.getWidth();
		int h = view.getHeight();
		for (int i = 0; i < views.length; i++) {
			View v = views[i];
			v.layout((i + 1) * w, 0, (i + 2) * w, h);
			printView("view[" + i + "]", v);
		}
	}

	public static void printView(String msg, View v) {
		System.out.println(msg + "=" + v);
		if (null == v) {
			return;
		}
		System.out.print("[" + v.getLeft());
		System.out.print(", " + v.getTop());
		System.out.print(", w=" + v.getWidth());
		System.out.println(", h=" + v.getHeight() + "]");
		System.out.println("mw=" + v.getMeasuredWidth() + ", mh="
				+ v.getMeasuredHeight());
		System.out.println("scroll [" + v.getScrollX() + "," + v.getScrollY()
				+ "]");
	}

	public static void initListView(Context context, ListView listView,
			String prefix, int numItems, int layout) {
		// By using setAdpater method in listview we an add string array in
		// list.
		String[] arr = new String[numItems];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = prefix + (i + 1);
		}
		listView.setAdapter(new ArrayAdapter<String>(context, layout, arr));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Context context = view.getContext();
				String msg = "item[" + position + "]="
						+ parent.getItemAtPosition(position);
				Toast.makeText(context, msg, 1000).show();
				System.out.println(msg);
			}
		});
	}
}

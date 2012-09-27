package com.ourlittlegame;

import java.util.Iterator;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ourlittlegame.entities.Activity;
import com.ourlittlegame.entities.Comment;
import com.ourlittlegame.entities.User;
import com.ourlittlegame.responses.AddCommentResponse;
import com.ourlittlegame.tasks.AddCommentTask;
import com.ourlittlegame.utilities.DateUtils;
import com.ourlittlegame.utilities.ImageManager;
import com.ourlittlegame.utilities.MiscUtils;

public class ActivityDetailActivity extends android.app.Activity implements AddCommentTask.IListener {
	int activityID;
	ProgressDialog pd;
	EditText cText;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitydetail);

		Bundle b = getIntent().getExtras();
		this.activityID = b.getInt("activityID");
		updateDisplay();
	}

	private void updateDisplay() {
		MyApp myapp = (MyApp) getApplicationContext();
		Activity a = myapp.findActivityById(activityID);
		
		View v = findViewById(R.id.activitydesc);
		ImageView iv = (ImageView) v.findViewById(R.id.userimg);
		ImageManager.showPicture(a.getUser().getID(), a.getUser().getPicture(),
				iv, myapp.getExternalStorageFolder());
		((TextView) v.findViewById(R.id.toptext)).setText(a.getCaption());
		((TextView) v.findViewById(R.id.middletext)).setText(DateUtils
				.relativeDate(a.getCreatedAt()));
		((TextView) v.findViewById(R.id.scoretext)).setText(a.getPoints() + "");

		ImageView av = (ImageView) v.findViewById(R.id.activitypicture);
		av.setImageBitmap(null);
		if (MiscUtils.removeNull(a.getAsset()).length() > 0) {
			av.setVisibility(View.VISIBLE);
			ImageManager.showPicture(a.getAsset(), av,
					myapp.getExternalStorageFolder());
		} else {
			av.setVisibility(View.GONE);
		}

		v.findViewById(R.id.nocommentsview).setVisibility(View.GONE);

		LayoutInflater vi = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout viewCommentsView = (LinearLayout) findViewById(R.id.activitycomments);
		viewCommentsView.removeAllViewsInLayout();
		List<Comment> comments = a.getComments();
		if (comments.size() > 0) {
			for (Iterator<Comment> it = comments.iterator(); it.hasNext();) {
				Comment c = it.next();
				User cuser = c.getUser();
				if (cuser == null) {
					int userId = c.getUserId();
					if (userId > 0) 
						cuser = myapp.findUserById(userId);
				}
				
				View cv = vi.inflate(R.layout.commentlistitem, null);
				((TextView) cv.findViewById(R.id.toptext)).setText(c
						.getMessage());
				((TextView) cv.findViewById(R.id.middletext)).setText(DateUtils
						.relativeDate(c.getCreatedAt()));
				
				if (cuser != null) {
					ImageView cuv = (ImageView) cv.findViewById(R.id.userimg);
					ImageManager.showPicture(cuser.getID(),cuser.getPicture(), cuv, myapp.getExternalStorageFolder());						
				}
				
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(0, 3, 0, 0);
				viewCommentsView.addView(cv, layoutParams);
			}
		}

		cText = (EditText) findViewById(R.id.comment);

		final Button button = (Button) findViewById(R.id.sendcomment);
		final int activityID = a.getID(); 
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MyApp myapp = (MyApp) getApplicationContext();
				AddCommentTask act = new AddCommentTask(ActivityDetailActivity.this, myapp,activityID);
				act.execute(cText.getText().toString());
			}
		});

		final Button cbutton = (Button) findViewById(R.id.clearcomment);
		cbutton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cText.setText("");
			}
		});
	}

	public void beforeCommentAdded() {
		cText.setEnabled(false);
	}

	public void onCommentAdded(Object result) {
		MyApp myapp = (MyApp) getApplicationContext();
		AddCommentResponse response = (AddCommentResponse)result;
		if (response.isValid()) {
			System.out.println("Comment added successfully.. updating the list..");
			Comment c = response.getComment();
			myapp.addCommentForActivity(activityID,c);
			updateDisplay();
		}
		cText.setText("");
		cText.setEnabled(true);
	}
}
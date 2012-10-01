package com.ourlittlegame;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ourlittlegame.adaptors.SuggestionListAdaptor;
import com.ourlittlegame.entities.Suggestion;
import com.ourlittlegame.entities.User;
import com.ourlittlegame.responses.GetSuggestionsResponse;
import com.ourlittlegame.tasks.GetSuggestionsTask;
import com.ourlittlegame.utilities.ImageManager;
import com.ourlittlegame.utilities.MiscUtils;

public class ComplimentActivity extends Activity implements
		GetSuggestionsTask.IListener, SuggestionListAdaptor.IListener {

	EditText txt;
	ListView suggestionList;
	SuggestionListAdaptor suggestionAdaptor;
	ImageView activityPicture;

	private static final int CAMERA_REQUEST = 1888;
	private static final int PICK_IMAGE = 1890;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compliment);

		MyApp myapp = (MyApp) getApplicationContext();
		// link controls
		txt = (EditText) findViewById(R.id.txt);
		ImageView iv = (ImageView) findViewById(R.id.userimg);
		activityPicture = (ImageView) findViewById(R.id.activitypicture);
		activityPicture.setVisibility(View.GONE);
		
		User u = myapp.getCouple().getCurrentUser();
		ImageManager.showPicture(u.getID(), u.getPicture(), iv,
				myapp.getExternalStorageFolder());

		((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//doLogin(v);
			}
		});
		((Button) findViewById(R.id.backbutton)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		((Button) findViewById(R.id.addphotobtn)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				final CharSequence[] items = {"Choose from Gallery", "Click new picture"};
				AlertDialog.Builder builder = new AlertDialog.Builder(ComplimentActivity.this);
				//builder.setTitle("Pick a color");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				        if (item == 0) {
				        	Intent intent = new Intent();
				        	intent.setType("image/*");
				        	intent.setAction(Intent.ACTION_GET_CONTENT);
				        	startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
				        } else {
				        	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
			                startActivityForResult(cameraIntent, CAMERA_REQUEST);
				        }
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();								 
			}
		});

		this.suggestionAdaptor = new SuggestionListAdaptor(this,
				R.layout.suggestionslistitem, myapp.getSuggestions(), myapp,
				this);
		suggestionList = (ListView) findViewById(android.R.id.list);
		suggestionList.setAdapter(this.suggestionAdaptor);

		GetSuggestionsTask lt = new GetSuggestionsTask(ComplimentActivity.this,
				myapp);
		lt.execute();
	}

	public void beforeIdeasLoad() {
		TextView tv = (TextView) findViewById(android.R.id.empty);
		tv.setText("Loading suggestions ...");
	}

	public void onIdeasLoad(Object result) {
		TextView tv = (TextView) findViewById(android.R.id.empty);
		tv.setVisibility(View.GONE);

		GetSuggestionsResponse ur = (GetSuggestionsResponse) result;
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

	@Override
	public void onUseSuggestion(int id) {
		MyApp myapp = (MyApp) getApplicationContext();
		Suggestion s = myapp.getSuggestionById(id);
		if (s != null) {
			txt.setText(s.getTextCaption());
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (requestCode == CAMERA_REQUEST) {  
            Bitmap photo = (Bitmap) data.getExtras().get("data"); 
            activityPicture.setImageBitmap(photo);
            activityPicture.setVisibility(View.VISIBLE);
        } else if(requestCode == PICK_IMAGE && data != null && data.getData() != null){
            Uri _uri = data.getData();

            if (_uri != null) {
                //User had pick an image.
                Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
                cursor.moveToFirst();

                //Link to the image
                final String imageFilePath = cursor.getString(0);
                System.out.println(imageFilePath);
                cursor.close();
            }
        }
    } 
}

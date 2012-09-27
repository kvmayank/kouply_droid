package com.ourlittlegame;

import java.util.HashMap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.ourlittlegame.responses.SigninResponse;
import com.ourlittlegame.tasks.SignupTask;
import com.ourlittlegame.utilities.MiscUtils;

public class SignupActivity extends Activity implements
		SignupTask.IListener {
	SigninResponse signinResponse;

	EditText name;
	EditText email;
	EditText partneremail;
	EditText password;
	Switch gender;

	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);

		// link controls
		name = (EditText) findViewById(R.id.name);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		partneremail = (EditText) findViewById(R.id.partneremail);
		gender = (Switch) findViewById(R.id.gender);

		Bundle b = getIntent().getExtras();
		signinResponse = b.getParcelable("response");
		if (signinResponse != null) {
			if (signinResponse.getUserName().length() > 0)
				name.setText(signinResponse.getUserName());
			if (signinResponse.getUserEmail().length() > 0)
				email.setText(signinResponse.getUserEmail());
			if (signinResponse.getUserGender().length() > 0) {
				if ("Male".equals(signinResponse.getUserGender())) 
					gender.setChecked(true);
				else
					gender.setChecked(false);
			}
			partneremail.setText("kvmayank@outlook.com");
			
			if (MiscUtils.removeNull(signinResponse.getFacebookUserId()).length() > 0) {
				password.setVisibility(View.GONE);
			}
		} else {
			String mode = b.getString("mode");
			if ("fullsignup".equals(mode)) {
				
			}
		}

		final Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doSignup(v);
			}
		});
	}

	private void doSignup(View v) {
		// Validate the form
		if (name.getText().length() == 0 || email.getText().length() == 0
				|| partneremail.getText().length() == 0) {
			Toast.makeText(getApplicationContext(),
					"Please fill out the fields", Toast.LENGTH_LONG).show();
			return;
		}
		if ((MiscUtils.removeNull(signinResponse.getFacebookUserId()).length() == 0) 
				&& password.getText().length() == 0) {
			Toast.makeText(getApplicationContext(),
					"Please fill out the password", Toast.LENGTH_LONG).show();
			return;
		}
		
		MyApp myapp = (MyApp)getApplicationContext();
		HashMap<String, String> fields = new HashMap<String, String>();
		fields.put("name", name.getText().toString());
		fields.put("email", email.getText().toString());
		fields.put("partneremail", partneremail.getText().toString());
		fields.put("gender", gender.isChecked()?"Male":"Female");
		fields.put("time_zone_offset", "0");
		if (MiscUtils.removeNull(signinResponse.getFacebookUserId()).length() > 0) {
			fields.put("facebook[uid]", signinResponse.getFacebookUserId());
			fields.put("facebook[token]", myapp.getFacebookToken());
		} else {
			fields.put("password", password.getText().toString());
		}
		// Post the information
		SignupTask lt = new SignupTask(SignupActivity.this, myapp);		
		lt.execute(fields);
	}

	public void beforeSignup() {
		pd = ProgressDialog.show(this, "", "Sending request...", true, false);
	}

	public void onSignup(Object result) {
		pd.dismiss();
	}
}

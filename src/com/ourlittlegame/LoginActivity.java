package com.ourlittlegame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ourlittlegame.responses.SigninResponse;
import com.ourlittlegame.tasks.LoginTask;

public class LoginActivity extends Activity implements
		LoginTask.IListener {

	EditText email;
	EditText password;
	
	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// link controls
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);

		final Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doLogin(v);
			}
		});
		
		final ImageView back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				MyApp myapp = (MyApp)getApplicationContext();
				Intent myIntent = new Intent(myapp, OurLittleGameActivity.class);
				startActivity(myIntent);
				finish();
			}
		});
	}

	private void doLogin(View v) {
		// Validate the form
		if (email.getText().length() == 0
				|| password.getText().length() == 0) {
			Toast.makeText(getApplicationContext(),
					"Please fill out the fields", Toast.LENGTH_LONG).show();
			return;
		}
		
		MyApp myapp = (MyApp)getApplicationContext();

		// Post the information
		LoginTask lt = new LoginTask(LoginActivity.this, myapp);		
		lt.execute(email.getText().toString(),password.getText().toString());
	}

	public void beforeLogin() {
		pd = ProgressDialog.show(this, "", "Sending request...", true, false);
	}

	public void onLogin(Object result) {
		pd.dismiss();
		MyApp myapp = (MyApp) getApplicationContext();
		SigninResponse response = (SigninResponse) result;
		if (response != null) {
			if (response.loginSuccess()) {
				myapp.setAppToken(response.getAppToken());
				// Proceed to app home
				Intent myIntent = new Intent(myapp, SlidingFeedActivity.class);
				startActivity(myIntent);
				finish();
			} else if (response.isNewUser()) {
				// Go to Sign Up				
				Intent myIntent = new Intent(myapp, SignupActivity.class);
				myIntent.putExtra("response", response);
				startActivity(myIntent);
				finish();
			} else {
				loginFailed(response);
			}
		} else {
			loginFailed(response);
		}
	}
	
	private void loginFailed(SigninResponse response) {
		// just show login failed
		TextView status = (TextView) findViewById(R.id.status);
		status.setText("Login failed");
		status.setVisibility(View.VISIBLE);
	}
}

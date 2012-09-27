package com.ourlittlegame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.ourlittlegame.responses.SigninResponse;
import com.ourlittlegame.tasks.LoginTask;
import com.ourlittlegame.utilities.LayoutUtils;
import com.ourlittlegame.utilities.MiscUtils;
import com.ourlittlegame.utilities.StyleFactory;

public class OurLittleGameActivity extends Activity implements
		LoginTask.IListener, LayoutUtils.ITextClickListener {
	/** Called when the activity is first created. */
	Facebook facebook = new Facebook("151127164967977");
	private ProgressDialog pd;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Button button = (Button) findViewById(R.id.button);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				login(v);
			}
		});

		TextView tv = (TextView) findViewById(R.id.loginorsignup);
		String txt = tv.getText().toString();
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		tv.setText(
				LayoutUtils.setSpanBetweenTokens(txt, "~~", new StyleFactory() {
					public CharacterStyle[] getStyles() {
						// TODO Auto-generated method stub
						CharacterStyle[] cs = {
								new StyleSpan(android.graphics.Typeface.BOLD),
								new LayoutUtils.MyClickableSpan(OurLittleGameActivity.this) };
						return cs;
					}
				}), BufferType.SPANNABLE);
		Spannable stext = (Spannable) tv.getText();
		stext.setSpan(
				new ForegroundColorSpan(getResources().getColor(
						android.R.color.white)), 0, txt.replaceAll("~~", "")
						.length(), 0);
	}

	public void login(final View view) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String access_token = prefs.getString("access_token", null);
		Long expires = prefs.getLong("access_expires", -1);
		if (access_token != null && expires != -1) {
			facebook.setAccessToken(access_token);
			facebook.setAccessExpires(expires);
		}
		if (!facebook.isSessionValid() || access_token == null) {
			/*
			 * facebook.authorize(this, new String[] {
			 * "email","publish_stream","offline_access" },-1, new
			 * DialogListener() {
			 */
			facebook.authorize(this, new String[] { "email", "user_birthday",
					"offline_access", "user_relationships" },
					new DialogListener() {
						public void onComplete(Bundle values) {
							MiscUtils.println("onComplete");
							/*
							 * for (Iterator it=values.keySet().iterator();
							 * it.hasNext();) { String key = (String)it.next();
							 * String value = (String)values.get(key);
							 * 
							 * MiscUtils.println(key + " => " + value); }
							 */
							String token = facebook.getAccessToken();
							long token_expires = facebook.getAccessExpires();

							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(OurLittleGameActivity.this);
							prefs.edit()
									.putLong("access_expires", token_expires)
									.commit();
							prefs.edit().putString("access_token", token)
									.commit();

							// String access_token =
							// (String)values.get("access_token");
							MyApp myapp = (MyApp) getApplicationContext();
							LoginTask lt = new LoginTask(
									OurLittleGameActivity.this, myapp);
							lt.execute(token);
						}

						public void onFacebookError(FacebookError error) {
							MiscUtils.println("onFacebookError");
							MiscUtils.println(error.getMessage());
							TextView status = (TextView) findViewById(R.id.status);
							status.setText(error.getMessage());
							status.setVisibility(View.VISIBLE);
						}

						public void onError(DialogError e) {
							MiscUtils.println("onError");
							MiscUtils.println(e.getMessage());
							TextView status = (TextView) findViewById(R.id.status);
							status.setText(e.getMessage());
							status.setVisibility(View.VISIBLE);
						}

						public void onCancel() {
							MiscUtils.println("onCancel");
						}
					});
		} else {
			MyApp myapp = (MyApp) getApplicationContext();
			LoginTask lt = new LoginTask(OurLittleGameActivity.this, myapp);
			lt.execute(access_token);
		}
	}

	public void onLogin(Object result) {
		pd.dismiss();
		MyApp myapp = (MyApp) getApplicationContext();
		SigninResponse response = (SigninResponse) result;
		if (response != null) {
			if (response.loginSuccess()) {
				myapp.setAppToken(response.getAppToken());
				// Proceed to app home
				Intent myIntent = new Intent(myapp, FeedActivity.class);
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

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(OurLittleGameActivity.this);
		prefs.edit().remove("access_expires").commit();
		prefs.edit().remove("access_token").commit();

	}

	public void beforeLogin() {
		pd = ProgressDialog.show(this, "", "Logging In...", true, false);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}

	public void handleClick(String actionString) {
		if ("sign-up".equals(actionString)) {
			MyApp myapp = (MyApp) getApplicationContext();
			Intent myIntent = new Intent(myapp, SignupActivity.class);
			myIntent.putExtra("mode", "fullsignup");
			startActivity(myIntent);
			finish();
		} else if ("login".equals(actionString)) {
			MyApp myapp = (MyApp) getApplicationContext();
			Intent myIntent = new Intent(myapp, LoginActivity.class);
			startActivity(myIntent);
			finish();
		}
	}
}
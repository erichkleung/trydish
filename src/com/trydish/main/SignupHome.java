package com.trydish.main;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SignupHome extends Activity {
	private String user, pass;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup_home);

		ActionBar actionBar = getActionBar();
		actionBar.hide();

		Intent i = getIntent();
		((EditText)(findViewById(R.id.signup_username))).setText(i.getStringExtra("user"));
		((EditText)(findViewById(R.id.signup_password))).setText(i.getStringExtra("pass"));
	}

	public void signupNext(View view) {
		EditText userText = (EditText)(findViewById(R.id.signup_username));
		EditText passText = (EditText)(findViewById(R.id.signup_password));
		EditText confText = (EditText)(findViewById(R.id.signup_password_confirm));

		user = userText.getText().toString();
		pass = passText.getText().toString();
		String conf = confText.getText().toString();

		if (user.equals("")) {
			Toast toast = Toast.makeText(this, "Please enter a username.", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 400);
			toast.show();
			return;
		} else if (pass.equals("")) {
			Toast toast = Toast.makeText(this, "Please enter a password.", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 400);
			toast.show();
			return;
		} else if (conf.equals("")) {
			Toast toast = Toast.makeText(this, "Please confirm your password.", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 400);
			toast.show();
			return;
		} else if (!(conf.equals(pass))) {
			Toast toast = Toast.makeText(this, "Mismatched passwords, try again.", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 400);
			toast.show();
			passText.setText("");
			confText.setText("");
			return;
		}

		ProgressBar progress = (ProgressBar)findViewById(R.id.login_progressbar);
		progress.setVisibility(View.VISIBLE);
		
		

		class CheckUsername extends AsyncTask<String, Void, JSONObject>{
			@Override
			protected JSONObject doInBackground(String... params) {
				String url = "http://trydish.pythonanywhere.com/is_username_taken/" + params[0];
				HttpResponse response;
				JSONObject result;

				HttpClient httpclient = new DefaultHttpClient();

				try {
					response = httpclient.execute(new HttpGet(url));
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						response.getEntity().writeTo(out);

						final String response_value = out.toString();
						out.close(); 
						JSONObject a = new JSONObject(response_value);
						return a;
					} else {
						response.getEntity().getContent().close();
						System.out.println("status: " + response.getStatusLine().getStatusCode());
						return null;
					}
				} catch (Exception e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(JSONObject result) {
				checkName(result);
			}
		}
		
		CheckUsername newUser = new CheckUsername();
		newUser.execute(user, pass);
	}



	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
	}

	private void checkName(JSONObject check) {
		ProgressBar progress = (ProgressBar)findViewById(R.id.login_progressbar);
		progress.setVisibility(View.INVISIBLE);

		try {
			global.userID = check.getInt("id");
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("broken  :(");
		}

		if (global.userID == -1) {
			global.username = ((EditText)findViewById(R.id.signup_username)).getText().toString();
			Intent intent = new Intent(this, SignupAllergies.class);
			intent.putExtra("user", user);
			intent.putExtra("pass", global.hash_pw(pass));
			startActivity(intent);
			overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left);
		} else {
			Toast toast = Toast.makeText(this, "That username is taken.", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 400);
			toast.show();
		}
	}
}
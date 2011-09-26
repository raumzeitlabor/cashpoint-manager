package org.raumzeitlabor.cashpoint.activities;

import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.R.id;
import org.raumzeitlabor.cashpoint.R.layout;
import org.raumzeitlabor.cashpoint.R.string;
import org.raumzeitlabor.cashpoint.client.AsyncTaskCompleteListener;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.tasks.AuthenticationTask;
import org.raumzeitlabor.cashpoint.client.entities.Session;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private final int DIALOG_AUTH_WAIT = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		final Button button = (Button) findViewById(R.id.loginBtn);
		final CheckBox autologin = (CheckBox) findViewById(R.id.autoLogin);
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final EditText username = (EditText) findViewById(R.id.username);
				final EditText password = (EditText) findViewById(R.id.password);
				
				// FOR DEBUGGING
				username.setText("foobar");
				password.setText("foobar");

		        // hide virtual keyboard
		        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

				if (username.getText().length() == 0
						|| password.getText().length() == 0) {
					Toast.makeText(LoginActivity.this, getString(R.string.form_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}

				AuthenticationTask task = new AuthenticationTask(new AsyncTaskCompleteListener() {
					private final CheckBox saveOnSuccess = (CheckBox) findViewById(R.id.autoLogin);
					
					@Override
					public void onTaskStart() {
						LoginActivity.this.showDialog(DIALOG_AUTH_WAIT);
					}

					@Override
					public void onTaskError(Exception error) {
						LoginActivity.this.removeDialog(DIALOG_AUTH_WAIT);
						
						String msg = error.getLocalizedMessage();
						if (error instanceof HttpStatusException) {
							if (((HttpStatusException) error).getStatus() == 401) {
								msg = LoginActivity.this.getString(R.string.auth_refused);
							} else if (((HttpStatusException) error).getStatus() == 403) {
								msg = LoginActivity.this.getString(R.string.auth_blocked);
							}
						}
						
						Toast.makeText(LoginActivity.this,
								LoginActivity.this.getString(R.string.auth_fail)+": "
								+msg, Toast.LENGTH_LONG).show();
					}

					@Override
					public <Session> void onTaskComplete(Session session) {
						LoginActivity.this.removeDialog(DIALOG_AUTH_WAIT);
						
						SharedPreferences settings = LoginActivity.this.getPreferences(LoginActivity.this.MODE_PRIVATE);
						Editor e = settings.edit();
						
						if (session != null) {
							// W. Z. F. §%(/§)$)="="=!=!!!!
							if (((org.raumzeitlabor.cashpoint.client.entities.Session) session).getRole().equals("admin"))
								LoginActivity.this.startActivity(new Intent(LoginActivity.this, ManagerActivity.class));
							else
								LoginActivity.this.startActivity(new Intent(LoginActivity.this, UserActivity.class));

							Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.auth_success),
									Toast.LENGTH_SHORT).show();

							if (saveOnSuccess.isChecked()) {
								e.putString("username", username.getText().toString());
								e.putString("password", password.getText().toString());
								e.putBoolean("autologin", true);
								e.commit();
								return;
							}
						}
						
						e.remove("username");
						e.remove("password");
						e.remove("autologin");
						e.commit();
					}
					
				});
				
				task.execute(username.getText().toString(), password.getText().toString());
			}
		});
		
		final float scale = this.getResources().getDisplayMetrics().density;
		autologin.setPadding(autologin.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
		        autologin.getPaddingTop(),
		        autologin.getPaddingRight(),
		        autologin.getPaddingBottom());
		
		password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
	                button.performClick();
	                return true;
	            }
	            return false;
			}
	    });
		
		// autologin
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		
		// check if we have been sent here because we signed off
		boolean signedoff = false;
		if (getIntent().getExtras() != null) {
			if (getIntent().getExtras().containsKey("signedoff"))
				signedoff = true;
		}
		
		if (settings.getBoolean("autologin", false)) {
			String user = settings.getString("username", null);
			String passwd = settings.getString("password", null);
			
			if (user != null && passwd != null) {
				username.setText(user);
				password.setText(passwd);
				autologin.setChecked(true);
				
				if (!signedoff)
					button.performClick();
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		switch(id) {
		case DIALOG_AUTH_WAIT:
			dialog = ProgressDialog.show(this, "", getString(R.string.auth_wait), true);
			break;
		}
		
		return dialog;
	}
}
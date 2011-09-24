package org.raumzeitlabor.cashpoint;

import org.raumzeitlabor.cashpoint.client.tasks.AuthenticationTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

public class LoginActivity extends Activity implements OnClickListener {
	private AuthenticationTask task;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		final Button button = (Button) findViewById(R.id.loginBtn);
		button.setOnClickListener(this);
		
		final CheckBox autologin = (CheckBox) findViewById(R.id.autoLogin);
		final float scale = this.getResources().getDisplayMetrics().density;
		autologin.setPadding(autologin.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
		        autologin.getPaddingTop(),
		        autologin.getPaddingRight(),
		        autologin.getPaddingBottom());
		
		final EditText username = (EditText) findViewById(R.id.username);
		
		final EditText password = (EditText) findViewById(R.id.password);
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
		SharedPreferences settings = getPreferences(this.MODE_PRIVATE);
		if (settings.getBoolean("autologin", false)) {
			String user = settings.getString("username", null);
			String passwd = settings.getString("password", null);
			
			if (user != null && passwd != null) {
				username.setText(user);
				password.setText(passwd);
				button.performClick();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		final EditText password = (EditText) findViewById(R.id.password);
		password.setText("");
	}

	@Override
	public void onClick(View v) {
		final EditText username = (EditText) findViewById(R.id.username);
		final EditText password = (EditText) findViewById(R.id.password);
		final CheckBox saveOnSuccess = (CheckBox) findViewById(R.id.autoLogin);
		
		// FOR DEBUGGING
		username.setText("foobar");
		password.setText("foobar");

        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(password.getWindowToken(), 0);

		if (username.getText().length() == 0
				|| password.getText().length() == 0) {
			Toast.makeText(this, getString(R.string.login_empty), Toast.LENGTH_SHORT).show();
			return;
		}

		task = new AuthenticationTask(this, username.getText() + "",
				password.getText() + "", saveOnSuccess.isChecked());
		task.execute();
	}

//	@Override
//	public void onBackPressed() {
//		Toast.makeText(this, "BACK", Toast.LENGTH_SHORT).show();
//		if (task != null) {
//			task.cancel(true);
//		}
//	}
}
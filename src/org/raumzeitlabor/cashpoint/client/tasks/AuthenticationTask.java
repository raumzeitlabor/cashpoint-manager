package org.raumzeitlabor.cashpoint.client.tasks;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.raumzeitlabor.cashpoint.ManagerActivity;
import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.UserActivity;
import org.raumzeitlabor.cashpoint.client.Cashpoint;
import org.raumzeitlabor.cashpoint.client.JSONResponseHandler;
import org.raumzeitlabor.cashpoint.client.Session;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AuthenticationTask extends AsyncTask<String,Void,Session> {
	private Exception error;
	private final Activity context;
	private final String username;
	private final String password;
	private final boolean saveOnSuccess;
	private Dialog dialog;
	
	public AuthenticationTask(Activity context, String username, String password, boolean saveOnSuccess) {
		this.context = context;
		this.username = username;
		this.password = password;
		this.saveOnSuccess = saveOnSuccess;
	}
	
	@Override
	protected void onCancelled() {
		dialog.dismiss();
//		Builder builder = new AlertDialog.Builder(context);
//		builder.setMessage(context.getString(R.string.auth_canceled));
//		dialog = builder.create();
//		dialog.show();
		Toast.makeText(context, "foo", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "", context.getString(R.string.auth_wait), true);
	}
	
	@Override
	protected void onPostExecute(Session s) {
		dialog.dismiss();

		SharedPreferences settings = context.getPreferences(context.MODE_PRIVATE);
		Editor e = settings.edit();
		
		if (error != null) {
			Toast.makeText(context, context.getString(R.string.auth_fail)+": "
					+error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		} else {
			if (s != null) {
				if (s.getRole().equals("admin"))
					context.startActivity(new Intent(context, ManagerActivity.class));
				else
					context.startActivity(new Intent(context, UserActivity.class));

				Toast.makeText(context, context.getString(R.string.auth_success),
						Toast.LENGTH_SHORT).show();
				
				if (saveOnSuccess) {
					e.putString("username", username);
					e.putString("password", password);
					e.putBoolean("autologin", true);
					e.commit();
					return;
				}
			}
		}
		
		e.remove("username");
		e.remove("password");
		e.remove("autologin");
		e.commit();
	}
	
	@Override
	protected Session doInBackground(String... params) {
		HttpParams httpParameters = new BasicHttpParams();
		
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		
		final DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost request = new HttpPost(Cashpoint.ENDPOINT+"/auth");
		request.addHeader("Content-Type", "application/json");

		Session s = null;
		try {
			JSONObject json = new JSONObject();
			json.put("username", username);
			json.put("passwd", password);
			
			request.setEntity(new ByteArrayEntity(json.toString().getBytes("utf-8")));
			JSONObject response = (JSONObject) client.execute(request, new JSONResponseHandler());
			s = new Session(response);
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return s;
	}

}

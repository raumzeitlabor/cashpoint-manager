package org.raumzeitlabor.cashpoint.client.tasks;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.HttpResponse;
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
import org.raumzeitlabor.cashpoint.client.AsyncTaskCompleteListener;
import org.raumzeitlabor.cashpoint.client.Cashpoint;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.JSONResponseHandler;
import org.raumzeitlabor.cashpoint.client.entities.Session;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AuthenticationTask extends AsyncTask<Object,Void,Session> {
	private Exception error;
	private final AsyncTaskCompleteListener callback;
	
	public AuthenticationTask(AsyncTaskCompleteListener t) {
		this.callback = t;
	}
	
	@Override
	protected void onPreExecute() {
		callback.onTaskStart();
	}

	@Override
	protected void onPostExecute(Session s) {
		if (error != null) {
			callback.onTaskError(error);
		} else {
			callback.onTaskComplete(s);
		}
	}
	
	@Override
	protected Session doInBackground(Object... params) {
		if (params.length != 2) {
			return null;
		}
		
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
			json.put("username", params[0]);
			json.put("passwd", params[1]);
			
			request.setEntity(new ByteArrayEntity(json.toString().getBytes("utf-8")));
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != 200)
				throw new HttpStatusException(statusCode);
			
			JSONObject jsonresp = (JSONObject) new JSONResponseHandler().handleResponse(response);
			s = new Session(jsonresp);
			
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (ParseException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (HttpStatusException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return s;
	}

}

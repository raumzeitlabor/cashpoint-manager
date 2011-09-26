package org.raumzeitlabor.cashpoint.client.tasks;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.activities.LoginActivity;
import org.raumzeitlabor.cashpoint.client.Cashpoint;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.entities.Session;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class CreateProductTask extends AsyncTask<String,Void,Boolean> {
	private Exception error;
	private final Activity context;
	private Dialog dialog;
	private Session session;
	
	public CreateProductTask(Activity context, Session s) {
		this.context = context;
		this.session = s;
	}
	
	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "",
				context.getString(R.string.product_create_wait), true);
	}
	
	@Override
	protected void onPostExecute(Boolean success) {
		dialog.dismiss();
		
		if (error != null) {
			String msg = error.getLocalizedMessage();
			
			if (error instanceof HttpStatusException) {
				if (((HttpStatusException) error).getStatus() == 401) {
					Session.getInstance().destroy();
					Intent intent = new Intent(context, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(intent);
					msg = context.getString(R.string.auth_fail);
				}
			}
			
			Toast.makeText(context, context.getString(R.string.product_create_fail)+": "
					+msg, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(context, "Product created.", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		if (params.length != 3)
			throw new IllegalArgumentException();
		
		HttpPost request = new HttpPost(Cashpoint.ENDPOINT+"/products?auth_token="
				+session.getAuthtoken());
		request.setHeader("Content-Type", "application/json");
		
		try {
			JSONObject json = new JSONObject();
			json.put("ean", params[0]);
			json.put("name", params[1]);			
			json.put("threshold", params[2]);
			
			request.setEntity(new StringEntity(json.toString(), "UTF-8"));
			HttpResponse response = Cashpoint.getHttpClient().execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != 201)
				throw new HttpStatusException(statusCode);
			
			return true;
			
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (HttpStatusException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		}
		
		return false;
	}

}

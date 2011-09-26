package org.raumzeitlabor.cashpoint.client.tasks;

import java.io.IOException;
import java.util.Comparator;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.activities.LoginActivity;
import org.raumzeitlabor.cashpoint.client.Cashpoint;
import org.raumzeitlabor.cashpoint.client.GroupArrayAdapter;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.JSONResponseHandler;
import org.raumzeitlabor.cashpoint.client.entities.Group;
import org.raumzeitlabor.cashpoint.client.entities.Session;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class CreateGroupTask extends AsyncTask<String,Void,Group> {
	private Exception error;
	private final Activity context;
	private Dialog dialog;
	private Session session;
	
	public CreateGroupTask(Activity context, Session s) {
		this.context = context;
		this.session = s;
	}
	
	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "",
				context.getString(R.string.group_add_wait), true);
	}
	
	@Override
	protected void onPostExecute(Group newGroup) {
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
			
			Toast.makeText(context, context.getString(R.string.group_add_fail)+": "
					+msg, Toast.LENGTH_LONG).show();
		} else {
			final ListView list = (ListView) context.findViewById(R.id.groupList);
			((GroupArrayAdapter) list.getAdapter()).add(newGroup);
			((GroupArrayAdapter) list.getAdapter()).sort(new Comparator<Group>() {

				@Override
				public int compare(Group object1, Group object2) {
					return object1.getName().compareTo(object2.getName());
				}
				
			});
			((GroupArrayAdapter) list.getAdapter()).notifyDataSetChanged();
		}
	}
	
	@Override
	protected Group doInBackground(String... params) {
		if (params.length != 1)
			return null;
		
		HttpPost request = new HttpPost(Cashpoint.ENDPOINT+"/groups?auth_token="
				+session.getAuthtoken());
		request.setHeader("Content-Type", "application/json");
		
		try {
			JSONObject json = new JSONObject();
			json.put("name", params[0]);
			request.setEntity(new StringEntity(json.toString(), "UTF-8"));
			HttpResponse response = Cashpoint.getHttpClient().execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != 201)
				throw new HttpStatusException(statusCode);
			
			JSONObject object = (JSONObject) new JSONResponseHandler().handleResponse(response);
			return new Group(object.getInt("id"), params[0], 0);
			
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
		
		return null;
	}

}

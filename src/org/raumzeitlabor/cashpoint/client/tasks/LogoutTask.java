package org.raumzeitlabor.cashpoint.client.tasks;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.raumzeitlabor.cashpoint.LoginActivity;
import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.client.Cashpoint;
import org.raumzeitlabor.cashpoint.client.entities.Session;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LogoutTask extends AsyncTask<String,Void,Integer> {
	private Exception error;
	private final Activity context;
	private Dialog dialog;
	private Session session;
	
	public LogoutTask(Activity context, Session s) {
		this.context = context;
		this.session = s;
	}
	
	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "", context.getString(R.string.logout_wait), true);
	}
	
	@Override
	protected void onPostExecute(Integer status) {
		dialog.dismiss();
		
		if (status == 200) {
			Toast.makeText(context, context.getString(R.string.logout_success),
					Toast.LENGTH_SHORT).show();
			context.finish();
		} else if (status == 401) {
			Session.getInstance().destroy();
			
			// see http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-lo
			Intent intent = new Intent(context, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(intent);
			
		} else {
			Toast.makeText(context, context.getString(R.string.logout_fail),
					Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		HttpParams httpParameters = new BasicHttpParams();
		
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		
		final DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		HttpDelete request = new HttpDelete(Cashpoint.ENDPOINT+"/auth?auth_token="
				+session.getAuthtoken());

		try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			
//			if (statusCode != 200) {
//				Log.e(this.getClass().getSimpleName(),
//						(new BasicResponseHandler().handleResponse(response)));
//			}
			
			return statusCode;
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return 0;
	}

}

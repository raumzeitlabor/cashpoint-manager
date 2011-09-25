package org.raumzeitlabor.cashpoint.client.tasks;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.raumzeitlabor.cashpoint.client.AsyncTaskCompleteListener;
import org.raumzeitlabor.cashpoint.client.Cashpoint;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.JSONResponseHandler;
import org.raumzeitlabor.cashpoint.client.entities.Product;
import org.raumzeitlabor.cashpoint.client.entities.Session;

import android.os.AsyncTask;
import android.util.Log;

public class ShowOrCreateProductTask extends AsyncTask<String,Void,Product> {
	private Exception error;
	private final AsyncTaskCompleteListener callback;
	
	public ShowOrCreateProductTask(AsyncTaskCompleteListener t) {
		this.callback = t;
	}
	
	@Override
	protected void onPreExecute() {
		callback.onTaskStart();
	}

	@Override
	protected void onPostExecute(Product product) {
		if (error != null) {
			callback.onTaskError(error);
		} else {
			callback.onTaskComplete(product);
		}
	}
	
	@Override
	protected Product doInBackground(String... params) {
		if (params.length != 1)
			return null;
		
		HttpParams httpParameters = new BasicHttpParams();
		
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		
		final DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		HttpGet request = new HttpGet(Cashpoint.ENDPOINT+"/products/"+params[0]+"?auth_token="
				+Session.getInstance().getAuthtoken());
		
		try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			

			if (statusCode == 404) // 404 => product not found
				return null;
			else if (statusCode != 200)
				throw new HttpStatusException(statusCode);
			
			JSONObject object = (JSONObject) new JSONResponseHandler().handleResponse(response);
			return new Product(object);
			
		} catch (IOException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (HttpStatusException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} catch (ParseException e) {
			Log.e(this.getClass().getSimpleName(), e.toString());
			error = e;
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return null;
	}

}

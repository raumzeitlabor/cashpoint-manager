package org.raumzeitlabor.cashpoint.client.tasks;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
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

public class GetProductsTask extends AsyncTask<String,Void,ArrayList<Product>> {
	private Exception error;
	private AsyncTaskCompleteListener callback;
	
	public GetProductsTask(AsyncTaskCompleteListener t) {
		this.callback = t;
	}
	
	@Override
	protected void onPreExecute() {
		callback.onTaskStart();
	}
	
	@Override
	protected void onPostExecute(ArrayList<Product> productList) {
		if (error != null) {
			callback.onTaskError(error);
		} else {
			callback.onTaskComplete(productList);
		}
	}
	
	@Override
	protected ArrayList<Product> doInBackground(String... params) {
		HttpGet request = new HttpGet(Cashpoint.ENDPOINT+"/products?auth_token="
				+Session.getInstance().getAuthtoken());

		final ArrayList<Product> productList = new ArrayList<Product>();
		
		try {
			HttpResponse response = Cashpoint.getHttpClient().execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			
			if (statusCode != 200)
				throw new HttpStatusException(statusCode);
			
			JSONArray object = (JSONArray) new JSONResponseHandler().handleResponse(response);
			for (int i = 0; i < object.length(); i++) {
				JSONObject json = (JSONObject) object.get(i);
				productList.add(new Product(json));
			}
			
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
		}
		
		return productList;
	}

}

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
import org.raumzeitlabor.cashpoint.CreateProductActivity;
import org.raumzeitlabor.cashpoint.LoginActivity;
import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.client.Cashpoint;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.JSONResponseHandler;
import org.raumzeitlabor.cashpoint.client.entities.Product;
import org.raumzeitlabor.cashpoint.client.entities.Session;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class ShowOrCreateProductTask extends AsyncTask<String,Void,Product> {
	private Exception error;
	private final Activity context;
	private Dialog dialog;
	private Session session;
	private String ean;
	
	public ShowOrCreateProductTask(Activity context, Session s) {
		this.context = context;
		this.session = s;
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
		dialog = ProgressDialog.show(context, "", context.getString(R.string.product_lookup_wait), true);
	}
	
	@Override
	protected void onPostExecute(Product product) {
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
			
			Toast.makeText(context, context.getString(R.string.product_lookup_fail)+": "
					+msg, Toast.LENGTH_LONG).show();
		} else {
			if (product == null) {
				Intent intent = new Intent(context, CreateProductActivity.class);
				intent.putExtra("ean", ean);
				context.startActivity(intent);
 			} else {
 				Toast.makeText(context, "product found", Toast.LENGTH_LONG).show();
 			}
		}
	}
	
	@Override
	protected Product doInBackground(String... params) {
		if (params.length != 1)
			return null;
		
		// save it for calling the create product activity
		ean = params[0];
		
		HttpParams httpParameters = new BasicHttpParams();
		
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		
		final DefaultHttpClient client = new DefaultHttpClient(httpParameters);
		HttpGet request = new HttpGet(Cashpoint.ENDPOINT+"/products/"+params[0]+"?auth_token="
				+session.getAuthtoken());
		
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

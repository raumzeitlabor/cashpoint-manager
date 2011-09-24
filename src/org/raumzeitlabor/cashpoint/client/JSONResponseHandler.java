package org.raumzeitlabor.cashpoint.client;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONResponseHandler implements ResponseHandler<Object> {

	@Override
	public Object handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		String json = new BasicResponseHandler().handleResponse(response);

		try {
			return new JSONTokener(json).nextValue();
		} catch (JSONException e) {
			throw new IOException(e.toString());
		}
	}
}

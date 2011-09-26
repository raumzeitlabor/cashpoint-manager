package org.raumzeitlabor.cashpoint.client;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public final class Cashpoint {
//	public static final String ENDPOINT = "http://172.22.36.21:3000";
	public static final String ENDPOINT = "http://192.168.1.10:3000";
	private final static DefaultHttpClient httpClient;
	
	static {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 3000);
		HttpConnectionParams.setSoTimeout(httpParameters, 5000);
		httpClient = new DefaultHttpClient(httpParameters);
	}

	public static DefaultHttpClient getHttpClient() {
		return httpClient;
	}
}

package org.raumzeitlabor.cashpoint.client.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Session {
	private static final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static Session instance;
	private final String username;
	private final int userid;
	private final String authtoken;
	private final String role;
	private Date validUntil;
	
	public Session(JSONObject response) throws JSONException, ParseException {
		authtoken = response.getString("auth_token");
		role = response.getString("role");
		username = response.getJSONObject("user").getString("name");
		userid = response.getJSONObject("user").getInt("id");
		validUntil = parser.parse(response.getString("valid_until"));
		
		this.instance = this;
	}

	public Date getValidUntil() {
		return validUntil;
	}

	public void setValidUntil(Date validUntil) {
		this.validUntil = validUntil;
	}

	public String getUsername() {
		return username;
	}

	public int getUserid() {
		return userid;
	}

	public String getAuthtoken() {
		return authtoken;
	}

	public String getRole() {
		return role;
	}

	public static Session getInstance() {
		return instance;
	}

	public void destroy() {
		instance = null;
	}

}

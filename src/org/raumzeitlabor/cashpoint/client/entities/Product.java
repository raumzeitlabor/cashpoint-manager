package org.raumzeitlabor.cashpoint.client.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
	private static final SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private final String name;
	private final String ean;
	private final int stock;
	private final Date addedOn;
	
	public Product(String name, String ean, int stock, String addedOn) throws ParseException {
		this.name = name;
		this.ean = ean;
		this.stock = stock;
		this.addedOn = parser.parse(addedOn);
	}
	
	public Product(JSONObject json) throws ParseException, JSONException {
		this.name = json.getString("name");
		this.ean = json.getString("ean");
		this.stock = json.getInt("stock");
		this.addedOn = parser.parse(json.getString("added_on"));
	}

	public String getName() {
		return name;
	}

	public String getEan() {
		return ean;
	}

	public int getStock() {
		return stock;
	}

	public Date getAddedOn() {
		return addedOn;
	}
}

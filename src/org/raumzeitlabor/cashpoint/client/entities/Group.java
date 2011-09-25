package org.raumzeitlabor.cashpoint.client.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class Group {
	private final String name;
	private final int id;
	private final int count;
	
	public Group(int id, String name, int count) {
		super();
		this.name = name;
		this.id = id;
		this.count = count;
	}
	
	public Group(JSONObject json) throws JSONException {
		this.name = json.getString("name");
		this.id = json.getInt("id");
		this.count = json.getInt("members");
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public int getCount() {
		return count;
	}
}

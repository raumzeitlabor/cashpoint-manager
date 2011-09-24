package org.raumzeitlabor.cashpoint.client;

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

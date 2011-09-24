package org.raumzeitlabor.cashpoint.menu;


public class MenuEntry {
	final String entryName;
	final String entryDescription;
	final Class<?> targetActivity;
	
	public MenuEntry(String entryName, String entryDescription) {
		this(entryName, entryDescription, null);
	}
	
	public MenuEntry(String entryName, String entryDescription,	Class<?> targetActivity) {
		super();
		this.entryName = entryName;
		this.entryDescription = entryDescription;
		this.targetActivity = targetActivity;
	}
	
	public String getEntryName() {
		return entryName;
	}
	
	public String getEntryDescription() {
		return entryDescription;
	}
	
	public Class<?> getTargetActivity() {
		return targetActivity;
	}	
}

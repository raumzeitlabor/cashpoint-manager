package org.raumzeitlabor.cashpoint.menu;

import org.raumzeitlabor.cashpoint.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MenuArrayAdapter extends ArrayAdapter<MenuEntry> {
	private final Activity context;
	private final MenuEntry[] entries;
	
	public MenuArrayAdapter(Activity context, MenuEntry[] entries) {
		super(context, R.layout.menu_item, entries);
		this.context = context;
		this.entries = entries;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.menu_item, null, true);
		TextView name = (TextView) rowView.findViewById(R.id.entryName);
		TextView descr = (TextView) rowView.findViewById(R.id.entryDescription);
		name.setText(entries[position].getEntryName());
		descr.setText(entries[position].getEntryDescription());
		return rowView;
	}
}

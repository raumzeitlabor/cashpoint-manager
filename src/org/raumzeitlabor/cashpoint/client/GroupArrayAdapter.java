package org.raumzeitlabor.cashpoint.client;

import org.raumzeitlabor.cashpoint.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GroupArrayAdapter extends ArrayAdapter<Group> {
	private final Activity context;
	private final Group[] groupList;
	
	public GroupArrayAdapter(Activity context, Group[] groupList) {
		super(context, R.layout.group_item, groupList);
		this.context = context;
		this.groupList = groupList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.group_item, null, true);
		TextView name = (TextView) rowView.findViewById(R.id.groupName);
		TextView count = (TextView) rowView.findViewById(R.id.groupCount);
		name.setText(groupList[position].getName());
		count.setText(""+groupList[position].getCount());
		return rowView;
	}
}

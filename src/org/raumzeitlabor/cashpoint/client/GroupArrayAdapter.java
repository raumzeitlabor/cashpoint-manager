package org.raumzeitlabor.cashpoint.client;

import java.util.ArrayList;

import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.client.entities.Group;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GroupArrayAdapter extends ArrayAdapter<Group> {
	private final Activity context;
	private final ArrayList<Group> groupList;
	
	public GroupArrayAdapter(Activity context, ArrayList<Group> groupList2) {
		super(context, R.layout.group_item, groupList2);
		this.context = context;
		this.groupList = groupList2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.group_item, null, true);
		TextView name = (TextView) rowView.findViewById(R.id.groupName);
		TextView count = (TextView) rowView.findViewById(R.id.groupCount);
		name.setText(groupList.get(position).getName());
		count.setText(""+groupList.get(position).getCount());
		return rowView;
	}
}

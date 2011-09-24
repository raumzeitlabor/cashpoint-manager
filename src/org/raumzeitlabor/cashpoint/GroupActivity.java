package org.raumzeitlabor.cashpoint;

import org.raumzeitlabor.cashpoint.client.Session;
import org.raumzeitlabor.cashpoint.client.tasks.GetGroupsTask;

import android.app.Activity;
import android.os.Bundle;

public class GroupActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		
		new GetGroupsTask(this, Session.getInstance()).execute();
	}

}

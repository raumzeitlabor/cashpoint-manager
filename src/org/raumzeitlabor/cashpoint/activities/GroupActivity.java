package org.raumzeitlabor.cashpoint.activities;

import java.util.ArrayList;

import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.R.drawable;
import org.raumzeitlabor.cashpoint.R.id;
import org.raumzeitlabor.cashpoint.R.layout;
import org.raumzeitlabor.cashpoint.R.string;
import org.raumzeitlabor.cashpoint.client.AsyncTaskCompleteListener;
import org.raumzeitlabor.cashpoint.client.GroupArrayAdapter;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.entities.Group;
import org.raumzeitlabor.cashpoint.client.entities.Session;
import org.raumzeitlabor.cashpoint.client.tasks.CreateGroupTask;
import org.raumzeitlabor.cashpoint.client.tasks.GetGroupsTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
		
		final ImageButton addbtn = (ImageButton) findViewById(R.id.addBtn);
		addbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog.Builder alert = new AlertDialog.Builder(GroupActivity.this);

				alert.setTitle(getString(R.string.group_add_title));
				alert.setMessage(getString(R.string.group_add_msg));

				final LinearLayout layout = new LinearLayout(GroupActivity.this);
				final float scale = GroupActivity.this.getResources().getDisplayMetrics().density;
				layout.setPadding(layout.getPaddingLeft() + (int)(15.0f * scale + 0.5f),
						layout.getPaddingTop(),
						layout.getPaddingRight() + (int)(15.0f * scale + 0.5f),
						layout.getPaddingBottom());
				
				final EditText input = new EditText(GroupActivity.this);
				input.setHint(""+getString(R.string.group_name_descr));
				input.setSingleLine();
				
				// set max length
				InputFilter[] FilterArray = new InputFilter[1];
				FilterArray[0] = new InputFilter.LengthFilter(30);
				input.setFilters(FilterArray);
				
				input.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
				layout.addView(input);
				alert.setView(layout);
				
				alert.setPositiveButton(getString(R.string.group_add_btn_ok),
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String name = ""+input.getText();
						if (name.length() > 0) {
							new CreateGroupTask(GroupActivity.this,
									Session.getInstance()).execute(name);
						}
					}
				});

				alert.setNegativeButton(getString(R.string.group_add_btn_cancel),
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {}
				});
				
				alert.create().show();
			}
			
		});
		
		final ImageButton refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshGroups();
			}
			
		});
		
		refreshGroups();
	}
	
	public void refreshGroups() {
		new GetGroupsTask(new AsyncTaskCompleteListener() {
			
			final ImageButton refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
			
			@Override
			public void onTaskStart() {
				refreshBtn.startAnimation(AnimationUtils.loadAnimation(GroupActivity.this, R.drawable.rotator));
			}
			
			@Override
			public void onTaskError(Exception error) {
				refreshBtn.clearAnimation();
				String msg = error.getLocalizedMessage();
				
				if (error instanceof HttpStatusException) {
					if (((HttpStatusException) error).getStatus() == 401) {
						Session.getInstance().destroy();
						Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						GroupActivity.this.startActivity(intent);
						msg = GroupActivity.this.getString(R.string.auth_fail);
					}
				}
				
				Toast.makeText(GroupActivity.this, 
						GroupActivity.this.getString(R.string.group_fetch_fail)+": "
						+msg, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public <GroupList> void onTaskComplete(GroupList groupList) {
				refreshBtn.clearAnimation();
				final ListView list = (ListView) GroupActivity.this.findViewById(R.id.groupList);
				list.setAdapter(new GroupArrayAdapter(GroupActivity.this, (ArrayList<Group>) groupList));

				TextView emptyView = new TextView(GroupActivity.this);
				emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
				emptyView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				emptyView.setText(GroupActivity.this.getString(R.string.group_fetch_empty));
				emptyView.setVisibility(View.GONE);
				((ViewGroup)list.getParent()).addView(emptyView);
				list.setEmptyView(emptyView);
			}
		}).execute();
	}

}

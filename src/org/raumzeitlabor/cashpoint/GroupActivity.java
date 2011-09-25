package org.raumzeitlabor.cashpoint;

import org.raumzeitlabor.cashpoint.client.entities.Session;
import org.raumzeitlabor.cashpoint.client.tasks.CreateGroupTask;
import org.raumzeitlabor.cashpoint.client.tasks.GetGroupsTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class GroupActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.group);
			
		ImageButton addbtn = (ImageButton) findViewById(R.id.addBtn);
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
		
		new GetGroupsTask(this, Session.getInstance()).execute();
	}

}

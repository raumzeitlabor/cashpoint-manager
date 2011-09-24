package org.raumzeitlabor.cashpoint;

import org.raumzeitlabor.cashpoint.client.Session;
import org.raumzeitlabor.cashpoint.client.tasks.LogoutTask;
import org.raumzeitlabor.cashpoint.menu.MenuArrayAdapter;
import org.raumzeitlabor.cashpoint.menu.MenuEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ManagerActivity extends Activity {	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager);
		
		final MenuEntry[] entries = {
			new MenuEntry("Groups", "Create new groups or add users to existing.", GroupActivity.class),
			new MenuEntry("Products", "Create new or manage existing products."),
			new MenuEntry("Cashcards", "Activate new or manage existing cashcards."),
			new MenuEntry("Statistics", "Get detailed information on cashpoint entities."),
		};
		
		final ListView menu = (ListView) findViewById(R.id.menulist);
		menu.setAdapter(new MenuArrayAdapter(this, entries));
		menu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				@SuppressWarnings("unchecked")
				final Class<Activity> target = (Class<Activity>)entries[position].getTargetActivity();
				
				if (target != null) {
					try {
						startActivity(new Intent(getApplicationContext(), entries[position].getTargetActivity()));
					} catch (ActivityNotFoundException e) {
						Log.w(this.getClass().getSimpleName(), "Associated activity could not be found");
					}
				} else {
					Toast.makeText(getApplicationContext(), "not yet implemented", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		final ImageButton scanBtn = (ImageButton) findViewById(R.id.scanBtn);
		scanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegrator.initiateScan(ManagerActivity.this);
			}
		});
		
		final ImageButton logoutBtn = (ImageButton) findViewById(R.id.logoutBtn);
		logoutBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new LogoutTask(ManagerActivity.this, Session.getInstance()).execute();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
			AlertDialog.Builder info = new AlertDialog.Builder(ManagerActivity.this);
			info.setMessage("TYPE: "+scanResult.getFormatName()+"\nCODE: "+scanResult.getContents());
			info.show();
		} else {
			CharSequence cancelmsg = "Scan has been canceled.";
			Toast.makeText(ManagerActivity.this, cancelmsg, Toast.LENGTH_SHORT).show();
		}
	}
}
package org.raumzeitlabor.cashpoint.activities;

import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.R.id;
import org.raumzeitlabor.cashpoint.R.layout;
import org.raumzeitlabor.cashpoint.R.string;
import org.raumzeitlabor.cashpoint.client.AsyncTaskCompleteListener;
import org.raumzeitlabor.cashpoint.client.entities.Session;
import org.raumzeitlabor.cashpoint.client.tasks.LogoutTask;
import org.raumzeitlabor.cashpoint.client.tasks.LookupProductTask;
import org.raumzeitlabor.cashpoint.menu.MenuArrayAdapter;
import org.raumzeitlabor.cashpoint.menu.MenuEntry;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
	private final int DIALOG_LOOKUP_WAIT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manager);
		
		final MenuEntry[] entries = {
			new MenuEntry("Groups", "Create new groups or add users to existing.", GroupActivity.class),
			new MenuEntry("Products", "Create new or manage existing products.", ProductActivity.class),
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
		final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		
		if (resultCode != RESULT_CANCELED) {
			if (scanResult.getFormatName().equals("EAN_8") || scanResult.getFormatName().equals("EAN_13")) {
				LookupProductTask task = new LookupProductTask(new AsyncTaskCompleteListener() {
					
					@Override
					public void onTaskError(Exception error) {
						ManagerActivity.this.dismissDialog(DIALOG_LOOKUP_WAIT);
					}
					
					@Override
					public <Product> void onTaskComplete(Product product) {
						ManagerActivity.this.removeDialog(DIALOG_LOOKUP_WAIT);
						
						if (product == null) {
							Intent intent = new Intent(ManagerActivity.this, CreateProductActivity.class);
							intent.putExtra("ean", scanResult.getContents());
							ManagerActivity.this.startActivity(intent);
						} else {
							Toast.makeText(ManagerActivity.this, "product found", Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onTaskStart() {
						ManagerActivity.this.removeDialog(DIALOG_LOOKUP_WAIT);
					}
				});
				task.execute(scanResult.getContents());
			} else if (scanResult.getFormatName().equals("CODE_128")) {
				Toast.makeText(ManagerActivity.this, "Cashcard scan", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(ManagerActivity.this,
					getString(R.string.scan_canceled), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		
		switch (id) {
		case DIALOG_LOOKUP_WAIT:
			dialog = ProgressDialog.show(this, "", getString(R.string.product_lookup_wait), true);
			break;
		}
		
		return dialog;
	}
}

package org.raumzeitlabor.cashpoint;

import org.raumzeitlabor.cashpoint.client.entities.Session;
import org.raumzeitlabor.cashpoint.client.tasks.CreateProductTask;
import org.raumzeitlabor.cashpoint.client.tasks.ShowOrCreateProductTask;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CreateProductActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_create);
		
		final EditText eanInput = (EditText) findViewById(R.id.productEAN);		
		final EditText nameInput = (EditText) findViewById(R.id.productName);
		final EditText thresholdInput = (EditText) findViewById(R.id.productThreshold);
		
		final ImageButton scanBtn = (ImageButton) findViewById(R.id.productCreateScanBtn);
		scanBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IntentIntegrator.initiateScan(CreateProductActivity.this, "EAN_8,EAN_13");
			}
		});
		
		// read out ean, if started after scan
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String ean = (String) extras.get("ean");
			if (ean != null) {
				eanInput.setText(ean);
				eanInput.setEnabled(false);
				eanInput.setFocusable(false);
			}
		}
		
		final Button button = (Button) findViewById(R.id.productCreateBtn);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (eanInput.getText().length() < 8
						|| nameInput.getText().length() == 0
						|| thresholdInput.getText().length() == 0) {
					Toast.makeText(CreateProductActivity.this, getString(R.string.form_empty),
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				CreateProductTask task = new CreateProductTask(CreateProductActivity.this,
						Session.getInstance());
				task.execute(eanInput.getText().toString(), nameInput.getText()
						.toString(), thresholdInput.getText().toString());
			}
		});
		
		
		// default value for threshold
		thresholdInput.setText(""+10);
		
		// set action to go for threshold
		thresholdInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_GO) {
	                button.performClick();
	                return true;
	            }
	            return false;
			}
	    });
		
		// validate input
		thresholdInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().startsWith("0")) {
					s.replace(0, 1, "");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
			
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {
//			ShowOrCreateProductTask task = new ShowOrCreateProductTask(this, Session.getInstance());
//			task.execute(scanResult.getContents());
			
			final EditText eanInput = (EditText) findViewById(R.id.productEAN);
			eanInput.setText(scanResult.getContents());
			eanInput.setFocusable(false);
			eanInput.setEnabled(false);
		} else {
			Toast.makeText(this, getString(R.string.scan_canceled), Toast.LENGTH_SHORT).show();
		}
	}
}

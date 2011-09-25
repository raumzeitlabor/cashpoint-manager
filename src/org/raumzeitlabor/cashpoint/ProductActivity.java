package org.raumzeitlabor.cashpoint;

import org.raumzeitlabor.cashpoint.client.entities.Session;
import org.raumzeitlabor.cashpoint.client.tasks.GetProductsTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ProductActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
		final ImageButton addbtn = (ImageButton) findViewById(R.id.addBtn);
		addbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProductActivity.this, CreateProductActivity.class);
				startActivity(intent);
			}
			
		});
	
		GetProductsTask task = new GetProductsTask(this, Session.getInstance());
		task.execute();
	}

}

package org.raumzeitlabor.cashpoint.activities;

import java.util.ArrayList;

import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.R.drawable;
import org.raumzeitlabor.cashpoint.R.id;
import org.raumzeitlabor.cashpoint.R.layout;
import org.raumzeitlabor.cashpoint.R.string;
import org.raumzeitlabor.cashpoint.client.AsyncTaskCompleteListener;
import org.raumzeitlabor.cashpoint.client.HttpStatusException;
import org.raumzeitlabor.cashpoint.client.ProductArrayAdapter;
import org.raumzeitlabor.cashpoint.client.entities.Group;
import org.raumzeitlabor.cashpoint.client.entities.Product;
import org.raumzeitlabor.cashpoint.client.entities.Session;
import org.raumzeitlabor.cashpoint.client.tasks.GetProductsTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product);
		
		final ListView list = (ListView) findViewById(R.id.productList);
		final ImageButton addbtn = (ImageButton) findViewById(R.id.addBtn);
		addbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ProductActivity.this, CreateProductActivity.class);
				startActivity(intent);
			}
		});
		
		final ImageButton refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				refreshProducts();
			}
			
		});
		
		refreshProducts();
	}
	
	public void refreshProducts() {
		GetProductsTask task = new GetProductsTask(new AsyncTaskCompleteListener() {
			private ProgressDialog dialog;
			private final ImageButton refreshBtn = (ImageButton) findViewById(R.id.refreshBtn);
			
			@Override
			public void onTaskStart() {
				refreshBtn.startAnimation(AnimationUtils.loadAnimation(ProductActivity.this, R.drawable.rotator));
			}
			
			@Override
			public void onTaskError(Exception error) {
				refreshBtn.clearAnimation();
				String msg = error.getLocalizedMessage();
				
				if (error instanceof HttpStatusException) {
					if (((HttpStatusException) error).getStatus() == 401) {
						Session.getInstance().destroy();
						Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
						ProductActivity.this.startActivity(intent);
						msg = ProductActivity.this.getString(R.string.auth_fail);
					}
				}
				
				Toast.makeText(ProductActivity.this,
						ProductActivity.this.getString(R.string.product_fetch_fail)+": "
						+msg, Toast.LENGTH_LONG).show();
			}
			
			@Override
			public <ProductList> void onTaskComplete(ProductList productList) {
				refreshBtn.clearAnimation();

				final ListView list = (ListView) ProductActivity.this.findViewById(R.id.productList);
				list.setAdapter(new ProductArrayAdapter(ProductActivity.this, (ArrayList<Product>)productList));

				TextView emptyView = new TextView(ProductActivity.this);
				emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
						LayoutParams.FILL_PARENT));
				emptyView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
				emptyView.setText(ProductActivity.this.getString(R.string.product_fetch_empty));
				emptyView.setVisibility(View.GONE);
				((ViewGroup)list.getParent()).addView(emptyView);
				list.setEmptyView(emptyView);
			}
		});
		
		task.execute();
	}

}

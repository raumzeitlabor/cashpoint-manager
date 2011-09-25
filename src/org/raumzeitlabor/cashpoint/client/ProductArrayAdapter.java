package org.raumzeitlabor.cashpoint.client;

import java.util.ArrayList;

import org.raumzeitlabor.cashpoint.R;
import org.raumzeitlabor.cashpoint.client.entities.Product;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProductArrayAdapter extends ArrayAdapter<Product> {
	private final Activity context;
	private final ArrayList<Product> productList;
	
	public ProductArrayAdapter(Activity context, ArrayList<Product> productList2) {
		super(context, R.layout.product_item, (ArrayList)productList2);
		this.context = context;
		this.productList = productList2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = context.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.product_item, null, true);
		TextView name = (TextView) rowView.findViewById(R.id.productName);
		TextView count = (TextView) rowView.findViewById(R.id.productStock);
		name.setText(productList.get(position).getName());
		count.setText(""+productList.get(position).getStock());
		return rowView;
	}
}

package com.ezpz.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.ezpz.pos.R;

/**
 * Created by RezaPramudhika on 8/31/2017.
 */

public class SaleItemAdapter extends BaseAdapter {

    Context context;
    String productName;
    String price;
    View view;
    LayoutInflater layoutInflater;

    public SaleItemAdapter(Context context, String productName, String price) {
        this.context = context;
        this.productName = productName;
        this.price = price;
    }

    @Override
    public int getCount() {
        return productName.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null){
            view = new View(context);
            view = layoutInflater.inflate(R.layout.list_item_bill, null);
            TextView txtItemBillProduct = (TextView) view.findViewById(R.id.itemBillProductName);
            TextView txtItemBillPrice = (TextView) view.findViewById(R.id.itemBillPrice);
            Button btnDelete = (Button) view.findViewById(R.id.btnBillItemDelete);

            txtItemBillProduct.setText(productName);
            txtItemBillPrice.setText(productName);

        }
        return view;
    }
}

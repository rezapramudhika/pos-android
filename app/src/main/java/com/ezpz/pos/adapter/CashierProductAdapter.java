package com.ezpz.pos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ezpz.pos.R;
<<<<<<< HEAD
import com.ezpz.pos.other.StaticFunction;
=======
>>>>>>> origin/master
import com.ezpz.pos.provider.Product;

import java.util.ArrayList;

/**
 * Created by RezaPramudhika on 8/31/2017.
 */

public class CashierProductAdapter extends BaseAdapter {

    private Context context;

    private View view;
    private int layout;
    private LayoutInflater layoutInflater;
    private ArrayList<Product> productArrayList;

    public CashierProductAdapter(Context context, int layout, ArrayList<Product> productArrayList) {
        this.context = context;
        this.layout = layout;
        this.productArrayList = productArrayList;
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return productArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout,null);
            holder.imageProduct = row.findViewById(R.id.imageCashierProductItem);
            holder.txtProductName = (TextView) row.findViewById(R.id.txtCashierProductItem);
            holder.txtProductStock = (TextView) row.findViewById(R.id.txtProductStock);
            holder.txtStock = row.findViewById(R.id.txtStock);
            row.setTag(holder);
        }else{
            holder = (ViewHolder) row.getTag();
        }

        Product product = productArrayList.get(position);
        holder.txtProductName.setText(product.getProductName());
        if(product.getStock()<1){
            holder.txtStock.setVisibility(View.GONE);
            holder.txtProductStock.setText("Out of stock");
            holder.txtProductStock.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
        }else{
            holder.txtProductStock.setText(String.valueOf(product.getStock()));
        }

        Glide.clear(holder.imageProduct);
        if (product.getPicture().equals("")){
            //Glide.with(thisActivity).load(StaticFunction.imageUrl("59b1df5bc9004.png")).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.imageViewProduct);
        }else
            Glide.with(context).load(StaticFunction.imageUrl(product.getPicture())).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.imageProduct);


        return row;
    }

    private class ViewHolder{
        ImageView imageProduct;
        TextView txtProductName;
        TextView txtProductStock;
        TextView txtStock;
    }

}

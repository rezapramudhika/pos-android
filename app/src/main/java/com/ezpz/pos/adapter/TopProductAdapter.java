package com.ezpz.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ezpz.pos.R;
import com.ezpz.pos.provider.ProductFav;

import java.util.List;

/**
 * Created by RezaPramudhika on 8/28/2017.
 */

public class TopProductAdapter extends RecyclerView.Adapter<TopProductAdapter.ViewHolder> {

    List<ProductFav> productList;
    List<ProductFav> productListName;
    Context context;
    Activity thisActivity;

    public TopProductAdapter(Activity thisActivity, List<ProductFav> productList, List<ProductFav> productListName, Context context) {
        this.productList = productList;
        this.context = context;
        this.thisActivity = thisActivity;
        this.productListName = productListName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_top_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ProductFav product = productList.get(position);
        final ProductFav name = productListName.get(position);
        holder.txtProductCode.setText((position+1)+". "+name.getProductName()+" (Sold: "+product.getCount()+")");
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtProductCode;

        public ViewHolder(View itemView) {
            super(itemView);
            txtProductCode = (TextView) itemView.findViewById(R.id.txtProductCode);
        }
    }


}

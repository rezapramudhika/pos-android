package com.ezpz.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ezpz.pos.R;
import com.ezpz.pos.activity.EditProductActivity;
import com.ezpz.pos.fragment.ProductFragment;
import com.ezpz.pos.provider.Product;

import java.util.List;

/**
 * Created by RezaPramudhika on 8/28/2017.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    List<Product> productList;
    Context context;
    Activity thisActivity;
    ProductFragment productFragment;

    public ProductAdapter(Activity thisActivity, List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
        this.thisActivity = thisActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Product product = productList.get(position);
        Glide.clear(holder.imageViewProduct);
        if (product.getPicture().equals("")){
            //Do nothing
         }else
            Glide.with(thisActivity).load(StaticFunction.imageUrl(product.getPicture())).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.imageViewProduct);

        holder.txtProductName.setText(product.getProductCode()+"/"+product.getProductName());
        holder.txtProductCategory.setText(product.getCategoryName());
        holder.txtProductStock.setText(String.valueOf(product.getStock()));
        holder.txtProductPrice.setText("Rp."+String.valueOf(product.getSellingPrice()));
        holder.txtProductDisc.setText(String.valueOf(product.getDisc())+"%");

        holder.linearLayoutProductItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putInt("productId", product.getId());
                bundle.putString("companyCode", product.getCompany_code());
                bundle.putString("picture", product.getPicture());

                Intent intent = new Intent(thisActivity, EditProductActivity.class).putExtras(bundle);
                thisActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtProductName;
        private TextView txtProductCategory;
        private TextView txtProductStock;
        private TextView txtProductPrice;
        private TextView txtProductDisc;
        private RelativeLayout linearLayoutProductItem;
        private ImageView imageViewProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            txtProductName = (TextView) itemView.findViewById(R.id.txtProductName);
            txtProductCategory = (TextView) itemView.findViewById(R.id.txtProductCategory);
            txtProductStock = (TextView) itemView.findViewById(R.id.txtProductStock);
            linearLayoutProductItem = (RelativeLayout) itemView.findViewById(R.id.layoutProductItem);
            imageViewProduct = (ImageView) itemView.findViewById(R.id.imageViewProduct);
            txtProductPrice = itemView.findViewById(R.id.txtSellingPrice);
            txtProductDisc = itemView.findViewById(R.id.txtProductDisc);
        }
    }


}

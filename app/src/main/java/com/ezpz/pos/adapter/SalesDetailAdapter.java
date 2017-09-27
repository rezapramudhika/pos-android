package com.ezpz.pos.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezpz.pos.R;
import com.ezpz.pos.provider.SalesDetail;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by RezaPramudhika on 9/27/2017.
 */

public class SalesDetailAdapter extends ArrayAdapter {
    private List<SalesDetail> salesList;
    private int resource;
    private LayoutInflater inflater;
    private Activity thisActivity;

    public SalesDetailAdapter(Activity thisActivity, int resource, List<SalesDetail> salesList) {
        super(thisActivity, resource, salesList);
        this.salesList = salesList;
        this.resource = resource;
        this.thisActivity = thisActivity;
        inflater =(LayoutInflater) thisActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SalesDetailAdapter.ViewHolder holder = null;
        if(convertView == null){
            holder = new SalesDetailAdapter.ViewHolder();

            convertView = inflater.inflate(resource,null);
            holder.txtProductName = (TextView) convertView.findViewById(R.id.itemBillProductName);
            holder.txtProductPrice = (TextView) convertView.findViewById(R.id.itemBillPrice);
            holder.txtQuantity = (TextView) convertView.findViewById(R.id.itemBillQuantity);
            holder.txtProductDisc = (TextView) convertView.findViewById(R.id.itemBillDisc);
            holder.btnDeleteItem = (Button) convertView.findViewById(R.id.btnBillItemDelete);
            holder.layoutQuantity = convertView.findViewById(R.id.layoutQuantity);
            convertView.setTag(holder);
        }else{
            holder = (SalesDetailAdapter.ViewHolder) convertView.getTag();
        }

        holder.btnDeleteItem.setVisibility(View.GONE);
        holder.layoutQuantity.setVisibility(View.GONE);

        final SalesDetail salesDetail = salesList.get(position);



        holder.txtProductName.setText(salesDetail.getProductName());
        if(salesDetail.getDisc()==0){
            holder.txtProductDisc.setVisibility(View.GONE);
            holder.txtProductPrice.setText(String.valueOf(salesDetail.getSellingPrice()));
        }else{
            int prodDisc = salesDetail.getSellingPrice()*salesDetail.getDisc()/100;
            int finalPrice = salesDetail.getSellingPrice()-prodDisc;
            holder.txtProductDisc.setText("Disc: "+salesDetail.getDisc()+"%");
            holder.txtProductPrice.setText(String.valueOf(finalPrice));
        }

//        holder.txtQuantity.setText("x"+salesDetail.getItemQuantity());

        return convertView;
    }

    class ViewHolder{
        private TextView txtProductName;
        private TextView txtProductPrice;
        private TextView txtQuantity;
        private TextView txtProductDisc;
        private Button btnDeleteItem;
        private LinearLayout layoutQuantity;
    }

}

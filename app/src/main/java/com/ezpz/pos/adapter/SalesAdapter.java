package com.ezpz.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.BillActivity;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Sales;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by RezaPramudhika on 8/28/2017.
 */

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    List<Sales> salesList;
    Context context;
    Activity thisActivity;

    public SalesAdapter(Activity thisActivity, List<Sales> salesList, Context context) {
        this.salesList = salesList;
        this.context = context;
        this.thisActivity = thisActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_sales, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Sales sales = salesList.get(position);

        holder.txtTimestamps.setText(getDate(sales.getCreatedAt()));

        holder.txtBillNumber.setText("#"+String.valueOf(sales.getBillNumber()));
        if(sales.getMemberCode().equalsIgnoreCase("Guest")){
            holder.txtMemberCode.setText("Customer    : "+sales.getMemberCode());
        }else{
            holder.txtMemberCode.setText("Customer    : "+sales.getMemberCode()+" / "+sales.getMemberName());
        }
        holder.txtQuantity.setText(String.valueOf(sales.getQuantity()));
        holder.txtTotalCash.setText(StaticFunction.moneyFormat(Double.valueOf(sales.getGrandTotal())));
        holder.layoutSalesItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("salesId", String.valueOf(sales.getId()));
                thisActivity.startActivity(new Intent(thisActivity, BillActivity.class).putExtras(bundle));
            }
        });

    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTimestamps;
        private TextView txtMemberCode;
        private TextView txtQuantity;
        private TextView txtTotalCash;
        private TextView txtBillNumber;
        private RelativeLayout layoutSalesItem;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTimestamps = (TextView) itemView.findViewById(R.id.txtTimestamps);
            txtMemberCode = (TextView) itemView.findViewById(R.id.txtMemberCode);
            txtQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
            txtTotalCash = (TextView) itemView.findViewById(R.id.txtTotalCash);
            txtBillNumber = itemView.findViewById(R.id.txtBillNumber);
            layoutSalesItem = (RelativeLayout) itemView.findViewById(R.id.layoutSalesItem);
        }
    }

    public String getDate(String inputDate){
        String curDate = inputDate;
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("hh:mm");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String output = "";
        try {
            cal.setTime(curDateFormat.parse(curDate));
            String desiredDate = desiredDateFormat.format(cal.getTime());
            output = desiredDate;
        } catch (Exception e) {}

        return output;
    }

}

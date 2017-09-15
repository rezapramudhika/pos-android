package com.ezpz.pos.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.MainPanelActivity;
import com.ezpz.pos.provider.Company;

import java.util.List;

/**
 * Created by RezaPramudhika on 8/19/2017.
 */

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder> {

    List<Company> companyList;
    Context context;

    public BusinessAdapter(List<Company> companyList, Context context) {
        this.companyList = companyList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_business, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Company company = companyList.get(position);
        holder.txtItemCompanyName.setText(company.getCompanyName());

        String i = company.getBusinessCategory();
        if (i.equals("1")) {
            i = "Restaurant";
        } else if (i.equals("2")) {
            i = "Shop";
        }

        holder.txtItemBusinessCategory.setText(i);
        holder.txtItemCompanyAddress.setText(company.getAddress());
        holder.txtItemCompanyContact.setText(company.getContact());
        holder.layoutCompanyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("companyId", String.valueOf(company.getId()));
                bundle.putString("companyCode", company.getCompanyCode());
                context.startActivity(new Intent(context, MainPanelActivity.class).putExtras(bundle));
            }
        });


    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtItemCompanyName;
        private TextView txtItemBusinessCategory;
        private TextView txtItemCompanyAddress;
        private TextView txtItemCompanyContact;
        private LinearLayout layoutCompanyItem;

        public ViewHolder(View itemView) {
            super(itemView);
            txtItemCompanyName = (TextView) itemView.findViewById(R.id.itemCompanyName);
            txtItemBusinessCategory = (TextView) itemView.findViewById(R.id.itemBusinessCategory);
            txtItemCompanyAddress = (TextView) itemView.findViewById(R.id.itemCompanyAddress);
            txtItemCompanyContact = (TextView) itemView.findViewById(R.id.itemCompanyContact);
            layoutCompanyItem = (LinearLayout) itemView.findViewById(R.id.layoutCompanyItem);
        }
    }

}

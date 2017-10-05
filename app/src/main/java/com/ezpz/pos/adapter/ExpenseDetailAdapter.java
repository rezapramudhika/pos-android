package com.ezpz.pos.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.ExpenseActivity;
import com.ezpz.pos.api.GetMemberDetail;
import com.ezpz.pos.api.PostDeleteExpenseItem;
import com.ezpz.pos.api.PostEditCustomer;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.ExpenseDetail;
import com.ezpz.pos.provider.Respon;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RezaPramudhika on 8/28/2017.
 */

public class ExpenseDetailAdapter extends RecyclerView.Adapter<ExpenseDetailAdapter.ViewHolder> {

    List<ExpenseDetail> expenseDetails;
    Context context;
    Activity thisActivity;
    private ProgressDialog mProgressDialog;
    String date, companyCode;
    boolean isDelete;

    public ExpenseDetailAdapter(Activity thisActivity, List<ExpenseDetail> expenseDetails, Context context, String date, String companyCode, boolean isDelete) {
        this.expenseDetails = expenseDetails;
        this.context = context;
        this.thisActivity = thisActivity;
        this.date = date;
        this.companyCode = companyCode;
        this.isDelete = isDelete;

        mProgressDialog = new ProgressDialog(thisActivity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_expense_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final ExpenseDetail expenseDetail = expenseDetails.get(position);

        holder.txtItemName.setText(expenseDetail.getItemName());
        holder.txtQty.setText(String.valueOf(expenseDetail.getQty()));
        holder.txtUnit.setText(expenseDetail.getUnit());
        holder.txtPrice.setText(StaticFunction.moneyFormat(Double.valueOf(expenseDetail.getPrice())));
        if (isDelete){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int totalPrice = ((ExpenseActivity) thisActivity).getTotalExpense()-expenseDetail.getPrice();
                    AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    builder.setTitle("Delete Item");
                    builder.setMessage("Are you sure?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            httpRequest_deleteExpenseItem(expenseDetail.getId(), expenseDetail.getCashOutId(), totalPrice);
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
        }else{
            holder.btnDelete.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return expenseDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtItemName;
        private TextView txtUnit;
        private TextView txtQty;
        private TextView txtPrice;
        private Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            txtItemName = (TextView) itemView.findViewById(R.id.txtItemName);
            txtUnit = (TextView) itemView.findViewById(R.id.txtUnit);
            txtQty = (TextView) itemView.findViewById(R.id.txtQty);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            btnDelete = itemView.findViewById(R.id.btnExpenseDelete);


        }
    }

    public String getDate(String inputDate){
        String curDate = inputDate;
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String output = "";
        try {
            cal.setTime(curDateFormat.parse(curDate));
            String desiredDate = desiredDateFormat.format(cal.getTime());
            output = desiredDate;
        } catch (Exception e) {}

        return output;
    }

    public void httpRequest_postEditCustomer(int id, String name, String email, String address, String contact, String companyCode, final Dialog dialog){
        //mProgressDialog.show();
        PostEditCustomer client =  StaticFunction.retrofit().create(PostEditCustomer.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id, name, email, address, contact, companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                //mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }else{
                    Toast.makeText(thisActivity.getApplicationContext(),
                            thisActivity.getApplicationContext().getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                Toast.makeText(thisActivity.getApplicationContext(),
                        thisActivity.getApplicationContext().getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void httpRequest_getSelectedMember(int id, final String memberCode, final String memberName) {
        mProgressDialog.show();
        GetMemberDetail client = StaticFunction.retrofit().create(GetMemberDetail.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id, memberCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    Respon respon = response.body();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setTitle(memberCode+"-"+memberName);
                    if(respon.getTotalPurchase()==0){
                        builder.setMessage("Member hasn't made any transactions yet.");
                    }else{
                        String totalPurchase = StaticFunction.moneyFormat(Double.valueOf(respon.getTotalPurchase()));
                        String favProduct = respon.getProductFav().getProductName();
                        builder.setMessage("Total Purchase: "+totalPurchase+"\n"+"Favorite Product: "+favProduct);
                    }
                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(thisActivity.getApplicationContext(),
                        thisActivity.getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void httpRequest_deleteExpenseItem(int id, int cashOutId, int total){
        PostDeleteExpenseItem client =  StaticFunction.retrofit().create(PostDeleteExpenseItem.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id, cashOutId, total);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        ((ExpenseActivity)thisActivity).httpRequest_getExpenseDetailList(companyCode,date);
                    }else
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(thisActivity.getApplicationContext(),
                            thisActivity.getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                Toast.makeText(thisActivity.getApplicationContext(),
                        thisActivity.getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

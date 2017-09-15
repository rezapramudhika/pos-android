package com.ezpz.pos.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.PostDeleteCashIn;
import com.ezpz.pos.fragment.CashInFragment;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.CashIn;
import com.ezpz.pos.provider.Respon;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RezaPramudhika on 9/10/2017.
 */

public class CashInAdapter extends RecyclerView.Adapter<CashInAdapter.MyHolder> {
    private List<CashIn> cashInList;
    private int resource;
    private Activity thisActivity;
    private CashInFragment fragment;
    private ProgressDialog mProgressDialog;

    public CashInAdapter(Activity thisActivity, int resource, List<CashIn> cashInList, CashInFragment fragment) {
        this.thisActivity = thisActivity;
        this.cashInList = cashInList;
        this.resource = resource;
        this.fragment = fragment;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView txtTimestamp;
        private TextView txtTotalCashIn;
        private TextView txtDescription;
        private RelativeLayout layoutCashInItem;

        public MyHolder(View view) {
            super(view);
            txtTimestamp = (TextView) view.findViewById(R.id.txtTimestamps);
            txtTotalCashIn = (TextView) view.findViewById(R.id.txtTotalCashIn);
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            layoutCashInItem = view.findViewById(R.id.layoutCashInItem);
        }
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        final CashIn cashIn = cashInList.get(position);
        holder.txtTimestamp.setText(getDate(cashIn.getCreated_at()));
        holder.txtTotalCashIn.setText(StaticFunction.moneyFormat(Double.valueOf(cashIn.getTotalCashIn())));
        holder.txtDescription.setText(cashIn.getDescription());
        holder.layoutCashInItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] items = {"Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

                builder.setTitle("Select The Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            httpRequest_postDeleteCashIn(cashIn.getId(),1, cashIn.getCompanyCode());
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return cashInList.size();
    }

    public String getDate(String inputDate){
        String curDate = inputDate;
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String output = "";
        try {
            cal.setTime(curDateFormat.parse(curDate));
            String desiredDate = desiredDateFormat.format(cal.getTime());
            output = desiredDate;
        } catch (Exception e) {}

        return output;
    }

    public void httpRequest_postDeleteCashIn(int id, int type, final String companyCode){
        PostDeleteCashIn client =  StaticFunction.retrofit().create(PostDeleteCashIn.class);
        Call<Respon> call = client.setVar(id, type);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        fragment.httpRequest_getCashIn(companyCode);
                    }else{
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
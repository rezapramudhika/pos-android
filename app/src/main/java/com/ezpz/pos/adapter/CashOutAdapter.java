package com.ezpz.pos.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.PostDeleteCashOut;
import com.ezpz.pos.fragment.CashOutFragment;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.CashOut;
import com.ezpz.pos.provider.ExpenseDetail;
import com.ezpz.pos.provider.Respon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by RezaPramudhika on 9/10/2017.
 */

public class CashOutAdapter extends RecyclerView.Adapter<CashOutAdapter.MyHolder> {
    private List<CashOut> cashOutList;
    private int resource;
    private Activity thisActivity;
    private CashOutFragment fragment;
    private RecyclerView expenseRecycleView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ExpenseDetail> expenseDetails = new ArrayList<>();
    private RecyclerView.Adapter adapter;

    public CashOutAdapter(Activity thisActivity, int resource, List<CashOut> cashOutList, CashOutFragment fragment) {
        this.thisActivity = thisActivity;
        this.cashOutList = cashOutList;
        this.resource = resource;
        this.fragment = fragment;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private TextView txtTimestamp;
        private TextView txtTotalCashOut;
        private TextView txtDescription;
        private RelativeLayout layoutCashOutItem;

        public MyHolder(View view) {
            super(view);
            txtTimestamp = (TextView) view.findViewById(R.id.txtTimestamps);
            txtTotalCashOut = (TextView) view.findViewById(R.id.txtTotalCashOut);
            txtDescription = (TextView) view.findViewById(R.id.txtDescription);
            layoutCashOutItem = view.findViewById(R.id.layoutCashOutItem);
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
        final CashOut cashOut = cashOutList.get(position);
        holder.txtTimestamp.setText(getDate(cashOut.getCreated_at()));
        holder.txtTotalCashOut.setText(StaticFunction.moneyFormat(Double.valueOf(cashOut.getTotalCashOut())));
        holder.txtDescription.setText(cashOut.getDescription());
        holder.layoutCashOutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(thisActivity);
                View mView = thisActivity.getLayoutInflater().inflate(R.layout.dialog_view_expense_item, null);
                TextView title = mView.findViewById(R.id.txtTitleExpenseDetail);
                title.setText(cashOut.getDescription());
                Button btnClose = mView.findViewById(R.id.btnClose);
                expenseRecycleView = (RecyclerView) mView.findViewById(R.id.expenseRecycleView);
                swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipeRefreshLayout);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                loadExpenseData(cashOut.getId(), cashOut.getCompanyCode());
            }
        });
        holder.layoutCashOutItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] items = {"Delete"};

                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

                builder.setTitle("Select The Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            httpRequest_postDeleteCashOut(cashOut.getId(),2, cashOut.getCompanyCode());
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
        return cashOutList.size();
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

    public void httpRequest_postDeleteCashOut(int id, int type, final String companyCode){
        PostDeleteCashOut client =  StaticFunction.retrofit().create(PostDeleteCashOut.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id, type);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        fragment.httpRequest_getCashOut(companyCode);
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

    public void loadExpenseData(final int id, final String companyCode){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(thisActivity);
        expenseRecycleView.setLayoutManager(mLayoutManager);
        expenseRecycleView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ExpenseDetailAdapter(thisActivity, expenseDetails, thisActivity.getApplicationContext(), null, companyCode, false);
        expenseRecycleView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                expenseDetails.clear();
                httpRequest_getExpenseDetailList(id, companyCode);
            }
        });
        httpRequest_getExpenseDetailList(id, companyCode);
    }

    public void httpRequest_getExpenseDetailList(int id, String companyCode){
        GetExpenseDetailLists client =  StaticFunction.retrofit().create(GetExpenseDetailLists.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()), id, companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        expenseDetails.clear();
                        for (ExpenseDetail expenseDetail : respon.getExpenseDetails()) {
                            expenseDetails.add(expenseDetail);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
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

    public interface GetExpenseDetailLists {
        @GET("api/v1/get-expense-item")
        Call<Respon> setVar(
                @Header("api_token") String apiToken,
                @Query("id") int id,
                @Query("company_code") String companyCode
        );
    }

}

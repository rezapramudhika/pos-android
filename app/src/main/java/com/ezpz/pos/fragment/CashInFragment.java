package com.ezpz.pos.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.MainPanelActivity;
import com.ezpz.pos.adapter.CashInAdapter;
import com.ezpz.pos.api.GetCashInList;
import com.ezpz.pos.api.PostCreateCashIn;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.CashIn;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CashInFragment extends Fragment {

    private List<CashIn> cashInList = new ArrayList<>();
    private RecyclerView cashInRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CashInAdapter cashInAdapter;
    private ProgressDialog mProgressDialog;
    private Button btnLinkToCashIn;
    private Activity thisActivity;
    private CashInFragment cashInFragment;
    private View thisView;


    public CashInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cash_in, container, false);
        thisActivity = getActivity();
        thisView = view;
        cashInFragment = this;

        initVar();
        loadCashInList();

        btnLinkToCashIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(thisActivity);
                View mView = thisActivity.getLayoutInflater().inflate(R.layout.dialog_add_cash, null);
                TextView txtTitle = mView.findViewById(R.id.txtTitleAddCash);
                final EditText inputTotalCashIn = mView.findViewById(R.id.inputTotalAddCash);
                final EditText inputCashInDescription = mView.findViewById(R.id.inputAddCashDescription);
                Button btnAddCashIn = mView.findViewById(R.id.btnAddCash);
                txtTitle.setText(R.string.txt_title_cash_in);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                btnAddCashIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        httpRequest_postAddCashIn(Integer.valueOf(inputTotalCashIn.getText().toString()),
                                inputCashInDescription.getText().toString(),
                                companyCode(),
                                1,
                                dialog);
                    }
                });
            }
        });
        return view;
    }

    private void initVar(){
        cashInRecyclerView = thisView.findViewById(R.id.cashInRecycleView);
        swipeRefreshLayout = thisView.findViewById(R.id.swipeRefreshLayout);
        btnLinkToCashIn = thisView.findViewById(R.id.btnLinkToAddCashIn);
    }

    private void loadCashInList(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        cashInAdapter = new CashInAdapter(thisActivity, R.layout.list_item_cash_in, cashInList, cashInFragment);
        cashInRecyclerView.setLayoutManager(mLayoutManager);
        cashInRecyclerView.setItemAnimator(new DefaultItemAnimator());
        cashInRecyclerView.setAdapter(cashInAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cashInList.clear();
                httpRequest_getCashIn(companyCode());

            }
        });
        httpRequest_getCashIn(companyCode());
    }

    public String companyCode() {
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }

    public void httpRequest_getCashIn(String companyCode) {
        GetCashInList client = StaticFunction.retrofit().create(GetCashInList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if (response.isSuccessful()) {
                    Respon respon = response.body();
                    cashInList.clear();
                    for (CashIn cashIn : respon.getCashInList()) {
                        cashInList.add(cashIn);
                    }
                    cashInAdapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                Toast.makeText(thisActivity.getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void httpRequest_postAddCashIn(int totalCash, String description, final String companyCode, int type, final Dialog dialog){
        mProgressDialog.show();
        PostCreateCashIn client =  StaticFunction.retrofit().create(PostCreateCashIn.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()), totalCash, description, companyCode, type);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        httpRequest_getCashIn(companyCode);
                    }
                }else{
                    Toast.makeText(thisActivity.getApplicationContext(),
                            getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(thisActivity.getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

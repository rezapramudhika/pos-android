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
import com.ezpz.pos.adapter.CashOutAdapter;
import com.ezpz.pos.api.GetCashOutList;
import com.ezpz.pos.api.PostCreateCashOut;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.CashOut;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CashOutFragment extends Fragment {
    private List<CashOut> cashOutList = new ArrayList<>();
    private RecyclerView cashOutRecycleView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CashOutAdapter cashOutAdapter;
    private ProgressDialog mProgressDialog;
    private Button btnLinkToCashOut;
    private Activity thisActivity;
    private CashOutFragment cashOutFragment;
    private View thisView;

    public CashOutFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cash_out, container, false);
        thisView = view;
        cashOutFragment = this;
        thisActivity = getActivity();

        initVar();
        loadCashOutList();

        btnLinkToCashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(thisActivity);
                View mView = thisActivity.getLayoutInflater().inflate(R.layout.dialog_add_cash, null);
                TextView txtTitle = mView.findViewById(R.id.txtTitleAddCash);
                final EditText inputTotalCashOut = mView.findViewById(R.id.inputTotalAddCash);
                final EditText inputCashInDescription = mView.findViewById(R.id.inputAddCashDescription);
                Button btnAddCashOut = mView.findViewById(R.id.btnAddCash);
                txtTitle.setText(R.string.txt_title_cash_out);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                btnAddCashOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        httpRequest_postAddCashOut(Integer.valueOf(inputTotalCashOut.getText().toString()),
                                inputCashInDescription.getText().toString(),
                                companyCode(),
                                2,
                                dialog);
                    }
                });
            }
        });

        return view;
    }

    private void initVar(){
        cashOutRecycleView = thisView.findViewById(R.id.cashOutRecycleView);
        swipeRefreshLayout = thisView.findViewById(R.id.swipeRefreshLayout);
        btnLinkToCashOut = thisView.findViewById(R.id.btnLinkToAddCashOut);
    }

    private void loadCashOutList(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        cashOutAdapter = new CashOutAdapter(getActivity(), R.layout.list_item_cash_out, cashOutList, cashOutFragment);
        cashOutRecycleView.setLayoutManager(mLayoutManager);
        cashOutRecycleView.setItemAnimator(new DefaultItemAnimator());

        //binding adapter
        cashOutRecycleView.setAdapter(cashOutAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cashOutList.clear();
                httpRequest_getCashOut(companyCode());

            }
        });
        httpRequest_getCashOut(companyCode());

    }

    public String companyCode() {
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }

    public void httpRequest_getCashOut(String companyCode) {
        GetCashOutList client = StaticFunction.retrofit().create(GetCashOutList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if (response.isSuccessful()) {
                    Respon respon = response.body();
                    cashOutList.clear();
                    for (CashOut cashOut : respon.getCashOutList()) {
                        cashOutList.add(cashOut);
                    }
                    cashOutAdapter.notifyDataSetChanged();
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

    public void httpRequest_postAddCashOut(int totalCash, String description, final String companyCode, int type, final Dialog dialog){
        mProgressDialog.show();
        PostCreateCashOut client =  StaticFunction.retrofit().create(PostCreateCashOut.class);
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
                        cashOutAdapter.notifyDataSetChanged();
                        httpRequest_getCashOut(companyCode);
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

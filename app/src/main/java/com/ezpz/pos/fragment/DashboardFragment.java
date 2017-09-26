package com.ezpz.pos.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.MainPanelActivity;
import com.ezpz.pos.adapter.TopProductAdapter;
import com.ezpz.pos.api.GetDashboard;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.ProductFav;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DashboardFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private TextView txtTotalSales, txtTotalMember, txtTotalCashIn, txtTotalCashOut;
    private List<ProductFav> productFavList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter adapter;
    private RecyclerView topProductRecyclerView;
    private Activity thisActivity;
    private Context thisContext;
    private View thisView;

    public DashboardFragment() {
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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        thisActivity = getActivity();
        thisContext = getContext();
        thisView=view;
        initVar();
        httpRequest_getDashboard(companyCode());
        loadTopProduct();
        return view;
    }

    private void initVar(){
        topProductRecyclerView = thisView.findViewById(R.id.topProductRecycleView);
        txtTotalSales = thisView.findViewById(R.id.txtTotalSales);
        txtTotalMember = thisView.findViewById(R.id.txtTotalMember);
        txtTotalCashIn = thisView.findViewById(R.id.txtTotalCashIn);
        txtTotalCashOut = thisView.findViewById(R.id.txtTotalCashOut);
        swipeRefreshLayout = thisView.findViewById(R.id.swipeRefreshLayoutProduct);
    }

    public void loadDashboardData(String totalSales, String totalMember, String totalCashIn, String totalCashOut){
        txtTotalSales.setText(totalSales);
        txtTotalMember.setText(totalMember);
        txtTotalCashIn.setText(StaticFunction.moneyFormat(Double.valueOf(totalCashIn)));
        txtTotalCashOut.setText(StaticFunction.moneyFormat(Double.valueOf(totalCashOut)));
    }

    public void loadTopProduct(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        topProductRecyclerView.setLayoutManager(mLayoutManager);
        topProductRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new TopProductAdapter(thisActivity, productFavList, thisContext);
        topProductRecyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productFavList.clear();
                httpRequest_getDashboard(companyCode());

            }
        });
    }

    public String companyCode(){
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }

    public void httpRequest_getDashboard(String companyCode){
        mProgressDialog.show();
        GetDashboard client =  StaticFunction.retrofit().create(GetDashboard.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        loadDashboardData(String.valueOf(respon.getTotalSales()),
                                String.valueOf(respon.getTotalMember()),
                                String.valueOf(respon.getTotalCashIn()),
                                String.valueOf(respon.getTotalCashOut()));
                        productFavList.clear();
                        for (ProductFav productFav : respon.getProductFavList()) {
                            productFavList.add(productFav);
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
                mProgressDialog.dismiss();
                Toast.makeText(thisActivity.getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

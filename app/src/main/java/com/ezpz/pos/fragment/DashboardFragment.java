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
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.ProductFav;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class DashboardFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private TextView txtTotalSales, txtTotalMember, txtTotalCashIn, txtTotalCashOut;
    private List<ProductFav> productFavList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter adapter;
    private RecyclerView topProductRecyclerView;
    private Activity thisActivity;
    private Context thisContext;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        thisActivity = getActivity();
        thisContext = getContext();

        topProductRecyclerView = (RecyclerView) view.findViewById(R.id.topProductRecycleView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        topProductRecyclerView.setLayoutManager(mLayoutManager);
        topProductRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new TopProductAdapter(thisActivity, productFavList, thisContext);

        topProductRecyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutProduct);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                productFavList.clear();
                httpRequest_getDashboard(companyCode());

            }
        });

        httpRequest_getDashboard(companyCode());

        return view;
    }

    public void loadDashboardData(String totalSales, String totalMember, String totalCashIn, String totalCashOut){
        final TextView txtTotalSales = getView().findViewById(R.id.txtTotalSales);
        final TextView txtTotalMember = getView().findViewById(R.id.txtTotalMember);
        final TextView txtTotalCashIn = getView().findViewById(R.id.txtTotalCashIn);
        final TextView txtTotalCashOut = getView().findViewById(R.id.txtTotalCashOut);
        txtTotalSales.setText(totalSales);
        txtTotalMember.setText(totalMember);
        txtTotalCashIn.setText(StaticFunction.moneyFormat(Double.valueOf(totalCashIn)));
        txtTotalCashOut.setText(StaticFunction.moneyFormat(Double.valueOf(totalCashOut)));
    }

    public void loadTopProduct(final List<ProductFav> productFavList){

    }

    public String companyCode(){
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }



    public void httpRequest_getDashboard(String companyCode){
        mProgressDialog.show();
        GetDashboard client =  StaticFunction.retrofit().create(GetDashboard.class);
        Call<Respon> call = client.setVar(companyCode);
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
                System.out.println("11");
                mProgressDialog.dismiss();
                System.out.println("12");
                Toast.makeText(thisActivity.getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface GetDashboard {
        @GET("api/v1/get-dashboard")
        Call<Respon> setVar(
                @Query("company_code") String companyCode
        );
    }



}

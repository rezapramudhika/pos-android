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
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.CashOut;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public class CashOutFragment extends Fragment {
    private List<CashOut> cashOutList = new ArrayList<>();
    private RecyclerView cashOutRecycleView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CashOutAdapter cashOutAdapter;
    private ProgressDialog mProgressDialog;
    private Button btnLinkToCashOut;
    private Activity thisActivity;
    private CashOutFragment cashOutFragment;

    public CashOutFragment() {
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
        View view = inflater.inflate(R.layout.fragment_cash_out, container, false);
        cashOutFragment = this;
        thisActivity = getActivity();
        cashOutRecycleView = (RecyclerView) view.findViewById(R.id.cashOutRecycleView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        cashOutAdapter = new CashOutAdapter(getActivity(), R.layout.list_item_cash_out, cashOutList, cashOutFragment);
        cashOutRecycleView.setLayoutManager(mLayoutManager);
        cashOutRecycleView.setItemAnimator(new DefaultItemAnimator());

        //binding adapter
        cashOutRecycleView.setAdapter(cashOutAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cashOutList.clear();
                httpRequest_getCashOut(companyCode());

            }
        });
        httpRequest_getCashOut(companyCode());

        btnLinkToCashOut = view.findViewById(R.id.btnLinkToAddCashOut);
        btnLinkToCashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(thisActivity);
                View mView = thisActivity.getLayoutInflater().inflate(R.layout.dialog_add_cash, null);
                TextView txtTitle = mView.findViewById(R.id.txtTitleAddCash);
                final EditText inputTotalCashOut = mView.findViewById(R.id.inputTotalAddCash);
                final EditText inputCashInDescription = mView.findViewById(R.id.inputAddCashDescription);
                Button btnAddCashOut = mView.findViewById(R.id.btnAddCash);
                txtTitle.setText("CASH OUT");
                btnAddCashOut.setText("Save");
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

    public String companyCode() {
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }

    @Override
    public void onResume() {
        super.onResume();
        //httpPost_getmessagebox(id_user);
    }

    public void httpRequest_getCashOut(String companyCode) {
        mProgressDialog.show();
        GetCashOut client = StaticFunction.retrofit().create(GetCashOut.class);
        Call<Respon> call = client.setVar(companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if (response.isSuccessful()) {
                    mProgressDialog.dismiss();
                    Respon respon = response.body();
                    cashOutList.clear();
                    for (CashOut cashOut : respon.getCashOutList()) {
                        cashOutList.add(cashOut);
                        //respon.getInbox() = tumpukan inbox yang dikirim web service
                        //setiap 1 tumpukan di bentuk menjadi message box
                        //setiap message box di tambahkan ke list inbox
                        //itu namanya for each dilakukan sampai tumpukan habis
                    }
                    cashOutAdapter.notifyDataSetChanged();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                mProgressDialog.dismiss();
            }
        });
    }

    public interface GetCashOut {
        @GET("api/v1/get-cash-out")
        Call<Respon> setVar(
                @Query("id") String companyCode
        );
    }

    public void httpRequest_postAddCashOut(int totalCash, String description, final String companyCode, int type, final Dialog dialog){
        mProgressDialog.show();
        AddCashOut client =  StaticFunction.retrofit().create(AddCashOut.class);
        Call<Respon> call = client.setVar(totalCash, description, companyCode, type);
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
                            "Server offline",
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



    public interface AddCashOut {
        @FormUrlEncoded
        @POST("api/v1/add-cash")
        Call<Respon> setVar(
                @Field("total_cash") int totalCash,
                @Field("description") String description,
                @Field("company_code") String companyCode,
                @Field("type") int type
        );
    }

}

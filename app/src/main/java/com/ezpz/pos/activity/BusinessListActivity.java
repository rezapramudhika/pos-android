package com.ezpz.pos.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.adapter.BusinessAdapter;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Company;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class BusinessListActivity extends AppCompatActivity {
    private RecyclerView businessList;
    private RecyclerView.Adapter adapter;
    private Activity thisActivity;
    private List<Company> companyList ;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_list);
        initVar();

    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBusinessList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Business List");


        businessList = (RecyclerView) findViewById(R.id.businessRecycleView);
        businessList.setLayoutManager(new LinearLayoutManager(this));
        businessList.setItemAnimator(new DefaultItemAnimator());

        companyList = new ArrayList<>();

        //binding cashierProductAdapter


        adapter = new BusinessAdapter(companyList, this);

        businessList.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                companyList.clear();
                httpRequest_getBusinessList(new Memcache(getApplicationContext()).getUser().getId());

            }
        });
        httpRequest_getBusinessList(new Memcache(getApplicationContext()).getUser().getId());
    }

    public void linkToAddBusiness(View view){
        Intent intent = new Intent(BusinessListActivity.this, AddNewBusinessActivity.class);
        startActivity(intent);
    }

    public void httpRequest_getBusinessList(int userId){
        mProgressDialog.show();
        GetBusinessList client =  StaticFunction.retrofit().create(GetBusinessList.class);
        Call<Respon> call = client.setVar(userId);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        for (Company messageBox : respon.getCompany()) {
                            companyList.add(messageBox);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface GetBusinessList {
        @GET("api/v1/get-business-list")
        Call<Respon> setVar(
                @Query("id") Integer userId
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.business_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.logout){
            logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        new Memcache(getApplicationContext()).logout();
        Intent intent = new Intent(BusinessListActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}

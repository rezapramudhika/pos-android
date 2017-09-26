package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.adapter.CategoryAdapter;
import com.ezpz.pos.api.GetCategory;
import com.ezpz.pos.api.PostCreateCategory;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCategory extends AppCompatActivity {

    private RecyclerView recycleViewCategory;
    private RecyclerView.Adapter adapter;
    private List<Category> categoryList;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText inputCategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        initVar();
        loadCategoryList();

    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarManageCategory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Manage Category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutCategory);
        inputCategoryName = (EditText) findViewById(R.id.editTextCategoryName);
        inputCategoryName.addTextChangedListener(new StaticFunction.TextWatcher(inputCategoryName));
        recycleViewCategory = (RecyclerView) findViewById(R.id.recycleViewCategory);

    }

    public void loadCategoryList(){
        recycleViewCategory.setLayoutManager(new LinearLayoutManager(this));
        recycleViewCategory.setItemAnimator(new DefaultItemAnimator());
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(categoryList, this);
        recycleViewCategory.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                categoryList.clear();
                httpRequest_getCategory(companyCode());

            }
        });

        httpRequest_getCategory(companyCode());
    }

    public void addCategory(View view){
        if (inputCategoryName.getText().toString().equalsIgnoreCase("")){
            inputCategoryName.setError("Please input category name");
        }else
        httpRequest_addCategory(inputCategoryName.getText().toString(), companyCode());
    }

    public void setEmpty(){
        inputCategoryName.setText("");
    }

    public String companyCode(){
        final Bundle bundle = getIntent().getExtras();
        final String myDataFromActivity = bundle.getString("companyCode");

        return myDataFromActivity;
    }

    public void httpRequest_getCategory(String companyCode){
        mProgressDialog.show();
        GetCategory client =  StaticFunction.retrofit().create(GetCategory.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()),companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        new Memcache(getApplicationContext()).setProductCategory(respon.getCategory());
                        for (Category category : respon.getCategory()) {
                            categoryList.add(category);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        setEmpty();
                    }else{
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
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

    public void httpRequest_addCategory(String categoryName, String companyCode){
        mProgressDialog.show();
        PostCreateCategory client =  StaticFunction.retrofit().create(PostCreateCategory.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), categoryName, companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(getApplicationContext(), ""+respon.getMessage(), Toast.LENGTH_LONG).show();
                        loadCategoryList();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putInt("state", 1);
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

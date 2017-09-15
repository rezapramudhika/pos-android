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
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Category;
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

public class ManageCategory extends AppCompatActivity {

    private RecyclerView recycleViewCategory;
    private RecyclerView.Adapter adapter;
    private List<Category> categoryList;
    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        toolbar.setNavigationIcon(R.drawable.backbtn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });

    }

    public void loadCategoryList(){
        recycleViewCategory = (RecyclerView) findViewById(R.id.recycleViewCategory);
        recycleViewCategory.setLayoutManager(new LinearLayoutManager(this));
        recycleViewCategory.setItemAnimator(new DefaultItemAnimator());

        categoryList = new ArrayList<>();

        adapter = new CategoryAdapter(categoryList, this);

        recycleViewCategory.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutCategory);
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
        EditText inputCategoryName = (EditText) findViewById(R.id.editTextCategoryName);
        httpRequest_addCategory(inputCategoryName.getText().toString(), companyCode());
    }

    public void setEmpty(){
        EditText inputCategoryName = (EditText) findViewById(R.id.editTextCategoryName);
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
        Call<Respon> call = client.setVar(companyCode);
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
        AddCategory client =  StaticFunction.retrofit().create(AddCategory.class);
        Call<Respon> call = client.setVar(categoryName, companyCode);

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
                            "Server offline",
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


    public interface AddCategory {
        @FormUrlEncoded
        @POST("api/v1/add-category")
        Call<Respon> setVar(
                @Field("category_name") String categoryName,
                @Field("company_code") String companyCode
        );
    }


    public interface GetCategory {
        @GET("api/v1/get-category")
        Call<Respon> setVar(
                @Query("id") String companyCode
        );
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putInt("state", 1);
        super.onBackPressed();
    }

}

package com.ezpz.pos.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.MainPanelActivity;
import com.ezpz.pos.adapter.ProductAdapter;
import com.ezpz.pos.api.GetCategoryList;
import com.ezpz.pos.api.GetProductList;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Product;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductFragment extends Fragment {

    private List<Category> categoryList;
    private List<Product> products;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String[] getItemCategory;
    private Spinner spnCategory;
    private RecyclerView productList;
    private RecyclerView.Adapter adapter;
    private ArrayAdapter<String> categoryAdapter;
    private ProgressDialog mProgressDialog;
    private View thisView;
    private Activity thisActivity;


    public ProductFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product, container, false);
        thisView = view;
        thisActivity = getActivity();
        initVar();

        httpRequest_getCategoryList(companyCode());

        loadProductList();

        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                httpRequest_getProductList(companyCode(),spnCategory.getSelectedItemPosition()-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                products.clear();
                httpRequest_getProductList(companyCode(),spnCategory.getSelectedItemPosition()-1);
                httpRequest_getCategoryList(companyCode());
            }
        });
        return view;
    }

    private void initVar(){
        spnCategory = thisView.findViewById(R.id.spinnerCategory);
        productList = thisView.findViewById(R.id.productListRecycleView);
        swipeRefreshLayout = thisView.findViewById(R.id.swipeRefreshLayoutProduct);
    }

    private void loadProductList(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        productList.setLayoutManager(mLayoutManager);
        productList.setItemAnimator(new DefaultItemAnimator());

        products = new ArrayList<>();

        adapter = new ProductAdapter(getActivity(),products, getContext());

        productList.setAdapter(adapter);
    }


    private void populatingSpinnerCategory(List<Category> thisCategoryList){
        categoryList = thisCategoryList;
        if (categoryList.size()==0){
            final String[] categoryItems = new String[0+1];
            categoryItems[0] = "All Product";

            categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categoryItems);
            spnCategory.setAdapter(categoryAdapter);
        }else{
            final String[] categoryItems = new String[categoryList.size()+1];
            categoryItems[0] = "All Product";

            int key = 1;
            for(Category category : categoryList) {
                categoryItems[key] = category.getCategoryName();
                key++;
            }
            categoryAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categoryItems);
            spnCategory.setAdapter(categoryAdapter);
        }
    }

    public String companyCode(){
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }


    public void httpRequest_getProductList(String companyCode, int category){
        String idCategory;
        if (category<0){
            idCategory = "";
        }else
            idCategory = String.valueOf(categoryList.get(category).getId());
        mProgressDialog.show();
        GetProductList client =  StaticFunction.retrofit().create(GetProductList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),companyCode, idCategory);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        products.clear();
                        for (Product product : respon.getProduct()) {
                            products.add(product);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void httpRequest_getCategoryList(String id){
        GetCategoryList client =  StaticFunction.retrofit().create(GetCategoryList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken((thisActivity.getApplicationContext())),id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        categoryList = respon.getCategory();
                        populatingSpinnerCategory(categoryList);
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

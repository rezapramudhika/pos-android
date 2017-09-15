package com.ezpz.pos.fragment;

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
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Product;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class ProductFragment extends Fragment {

    private List<Category> categoryList;
    private List<Product> products;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String[] getItemCategory;
    Spinner spnCategory;
    private RecyclerView productList;
    private RecyclerView.Adapter adapter;
    ArrayAdapter<String> categoryAdapter;
    private ProgressDialog mProgressDialog;


    public ProductFragment() {
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
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        spnCategory = (Spinner) view.findViewById(R.id.spinnerCategory);
        httpRequest_getCategoryList(companyCode());

        productList = (RecyclerView) view.findViewById(R.id.productListRecycleView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        productList.setLayoutManager(mLayoutManager);
        productList.setItemAnimator(new DefaultItemAnimator());

        products = new ArrayList<>();

        adapter = new ProductAdapter(getActivity(),products, getContext());

        productList.setAdapter(adapter);


        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                httpRequest_getProductList(companyCode(),spnCategory.getSelectedItemPosition()-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutProduct);
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


    private void populatingSpinnerCategory(List<Category> thisCategoryList){
        spnCategory = (Spinner) getView().findViewById(R.id.spinnerCategory);
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


    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
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
        Call<Respon> call = client.setVar(companyCode, idCategory);
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

    public interface GetProductList {
        @GET("api/v1/get-product")
        Call<Respon> setVar(
                @Query("company_code") String companyCode,
                @Query("category") String category
        );
    }

    public void httpRequest_getCategoryList(String id){
        GetCategoryList client =  StaticFunction.retrofit().create(GetCategoryList.class);
        Call<Respon> call = client.setVar(id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        spnCategory = (Spinner) getView().findViewById(R.id.spinnerAddProductCategory);
                        categoryList = respon.getCategory();
                        populatingSpinnerCategory(categoryList);
                        setCategoryList(categoryList);
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

    public interface GetCategoryList {
        @GET("api/v1/get-category")
        Call<Respon> setVar(
                @Query("id") String id
        );
    }

    @Override
    public void onResume() {
        httpRequest_getProductList(companyCode(),spnCategory.getSelectedItemPosition()-1);
        super.onResume();
    }
}

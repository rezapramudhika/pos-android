package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.GetCategoryList;
import com.ezpz.pos.api.PostCreateProduct;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Respon;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewProductActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private List<Category> categoryList;
    private Spinner spnCategory;
    private EditText inputProductCode, inputProductName, inputPurchasePrice, inputSellingPrice, inputDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);
        initVar();
        //loadSpinnerCategory();
        httpRequest_getCategoryList(companyCode());
    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddNewProduct);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spnCategory = (Spinner) findViewById(R.id.spinnerAddProductCategory);
        inputProductCode= (EditText) findViewById(R.id.inputProductCode);
        inputProductName = (EditText) findViewById(R.id.inputProductName);
        inputPurchasePrice = (EditText) findViewById(R.id.inputPurchasePrice);
        inputSellingPrice = (EditText) findViewById(R.id.inputSellingPrice);
        inputDescription = (EditText) findViewById(R.id.inputDescription);
        inputProductCode.addTextChangedListener(new StaticFunction.TextWatcher(inputProductCode));
        inputProductName.addTextChangedListener(new StaticFunction.TextWatcher(inputProductName));
        inputPurchasePrice.addTextChangedListener(new StaticFunction.TextWatcher(inputPurchasePrice));
        inputSellingPrice.addTextChangedListener(new StaticFunction.TextWatcher(inputSellingPrice));
        inputDescription.addTextChangedListener(new StaticFunction.TextWatcher(inputDescription));
    }

    public String companyCode(){
        final Bundle bundle = getIntent().getExtras();
        String companyCode = bundle.getString("companyCode");
        return companyCode;
    }

    private void populatingSpinnerCategory(List<Category> thisCategoryList){
        categoryList = thisCategoryList;
        if (categoryList.size()==0){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("You haven't set product category!");
            alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Add Category",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            Bundle bundle = new Bundle();
                            bundle.putString("companyCode", companyCode());
                            dialog.dismiss();
                            startActivity(new Intent(AddNewProductActivity.this, ManageCategory.class).putExtras(bundle));
                            finish();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else{
            final String[] categoryItems = new String[categoryList.size()];

            int key = 0;
            for(Category category : categoryList) {
                categoryItems[key] = category.getCategoryName();
                key++;
            }
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryItems);
            spnCategory.setAdapter(categoryAdapter);
        }
    }


    public Integer getSelectedCategory(){

        return categoryList.get(spnCategory.getSelectedItemPosition()).getId();
    }

    public void addNewProduct(View view){
        if(inputProductCode.getText().toString().equalsIgnoreCase("")){
            inputProductCode.setError("Please input product code");
        }else if(inputProductName.getText().toString().equalsIgnoreCase("")){
            inputProductName.setError("Please input product name");
        }else if(inputPurchasePrice.getText().toString().equalsIgnoreCase("")){
            inputPurchasePrice.setError("Please input purchase price");
        }else if(inputSellingPrice.getText().toString().equalsIgnoreCase("")){
            inputSellingPrice.setError("Please input selling price");
        }else{
            httpRequest_addNewProduct(inputProductCode.getText().toString(),
                    inputProductName.getText().toString(),
                    getSelectedCategory(),
                    Integer.valueOf(inputPurchasePrice.getText().toString()),
                    Integer.valueOf(inputSellingPrice.getText().toString()),
                    inputDescription.getText().toString(),
                    companyCode()
            );
        }
    }


    public void httpRequest_addNewProduct(String productCode, String name, int category, int purchasePrice, int sellingPrice, String description, String companyCode){
        mProgressDialog.show();
        PostCreateProduct client =  StaticFunction.retrofit().create(PostCreateProduct.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), productCode, name, category, purchasePrice, sellingPrice, description, companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    Toast.makeText(getApplicationContext(),
                            respon.getMessage(),
                            Toast.LENGTH_LONG).show();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
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

    public void httpRequest_getCategoryList(String id){
        mProgressDialog.show();
        GetCategoryList client =  StaticFunction.retrofit().create(GetCategoryList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()),id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        spnCategory = (Spinner) findViewById(R.id.spinnerAddProductCategory);
                        categoryList = respon.getCategory();
                        populatingSpinnerCategory(categoryList);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

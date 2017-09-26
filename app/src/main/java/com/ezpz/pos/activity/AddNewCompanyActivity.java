package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.PostCreateCompany;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.provider.BusinessCategory;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewCompanyActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private List<BusinessCategory> listBusinessCategory;
    private ArrayAdapter<String> adapterBusinessCategory;
    private EditText inputName, inputAddress, inputContact;
    private Spinner spinnerBusinessType;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_business);
        initVar();
        populatingBusinessCategory();
    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddNewBusiness);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Company");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spinnerBusinessType = (Spinner) findViewById(R.id.spinnerBusinessType);
        inputName = (EditText) findViewById(R.id.inputBusinessName);
        inputAddress = (EditText) findViewById(R.id.inputBusinessAddress);
        inputContact = (EditText) findViewById(R.id.inputBusinessContact);
        inputName.addTextChangedListener(new StaticFunction.TextWatcher(inputName));
        inputAddress.addTextChangedListener(new StaticFunction.TextWatcher(inputAddress));
        inputContact.addTextChangedListener(new StaticFunction.TextWatcher(inputContact));
        user = new Memcache(getApplicationContext()).getUser();
    }

    private void populatingBusinessCategory(){
        listBusinessCategory = new Memcache(getApplicationContext()).getBusinessCategory();
        String[] itemsBusinessCategory = new String[listBusinessCategory.size()];
        for(int i=0; i<listBusinessCategory.size(); i++){
            BusinessCategory businessCategory = listBusinessCategory.get(i);
            itemsBusinessCategory[i] = businessCategory.getBusinessCategory();
        }
        adapterBusinessCategory = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, itemsBusinessCategory);
        spinnerBusinessType.setAdapter(adapterBusinessCategory);
    }

    public void addNewBusinessOnClick(View view){
        if(inputName.getText().toString().equalsIgnoreCase("")){
            inputName.setError("Please input company name");
        }else if(inputAddress.getText().toString().equalsIgnoreCase("")){
            inputAddress.setError("Please input company address");
        }else if(inputContact.getText().toString().equalsIgnoreCase("")){
            inputContact.setError("Please input contact");
        }else{
            httpRequest_postNewCompany(inputName.getText().toString(),
                    inputAddress.getText().toString(),
                    inputContact.getText().toString(),
                    listBusinessCategory.get(spinnerBusinessType.getSelectedItemPosition()).getId(),
                    user.getId());
        }
    }

    public void httpRequest_postNewCompany(String name, String address, String contact, Integer businessCategory, Integer userId){
        PostCreateCompany client =  StaticFunction.retrofit().create(PostCreateCompany.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), name, address, contact, businessCategory, userId);

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
                        startActivity(new Intent(AddNewCompanyActivity.this, BusinessListActivity.class));
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
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

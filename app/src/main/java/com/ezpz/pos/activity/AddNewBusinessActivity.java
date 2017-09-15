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
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.BusinessCategory;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class AddNewBusinessActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    private List<BusinessCategory> listBusinessCategory;
    private ArrayAdapter<String> adapterBusinessCategory;
    EditText inputName, inputAddress, inputContact;
    Spinner spinnerBusinessType;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_business);
        initVar();

    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        spinnerBusinessType = (Spinner) findViewById(R.id.spinnerBusinessType);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddNewBusiness);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Business");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.backbtn);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddNewBusinessActivity.this, BusinessListActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //----spinner----//
        listBusinessCategory = new Memcache(getApplicationContext()).getBusinessCategory();
        String[] itemsBusinessCategory = new String[listBusinessCategory.size()];
        for(int i=0; i<listBusinessCategory.size(); i++){
            BusinessCategory businessCategory = listBusinessCategory.get(i);
            itemsBusinessCategory[i] = businessCategory.getBusinessCategory();
        }
        adapterBusinessCategory = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, itemsBusinessCategory);
        spinnerBusinessType.setAdapter(adapterBusinessCategory);
        //----spinner----//

        user = new Memcache(getApplicationContext()).getUser();
    }

    public void addNewBusinessOnClick(View view){
        inputName = (EditText) findViewById(R.id.inputBusinessName);
        inputAddress = (EditText) findViewById(R.id.inputBusinessAddress);
        inputContact = (EditText) findViewById(R.id.inputBusinessContact);
        httpRequest_postNewBusiness(inputName.getText().toString(),
                inputAddress.getText().toString(),
                inputContact.getText().toString(),
                listBusinessCategory.get(spinnerBusinessType.getSelectedItemPosition()).getId(),
                user.getId());
    }

    public void httpRequest_postNewBusiness(String name, String address, String contact, Integer businessCategory, Integer userId){
        AddNewBusiness client =  StaticFunction.retrofit().create(AddNewBusiness.class);
        Call<Respon> call = client.setVar(name, address, contact, businessCategory, userId);

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
                        startActivity(new Intent(AddNewBusinessActivity.this, BusinessListActivity.class));
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
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface AddNewBusiness {
        @FormUrlEncoded
        @POST("api/v1/add-new-business")
        Call<Respon> setVar(
                @Field("name") String name,
                @Field("address") String address,
                @Field("contact") String contact,
                @Field("business_category") Integer businessCategory,
                @Field("user_id") Integer userId
        );
    }


}

package com.ezpz.pos.activity;

import android.app.ProgressDialog;
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
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.Staff;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class AddNewUserActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    Spinner spnUserPosition;
    List<Staff> staffList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        initVar();
        loadSpinnerPosition();
    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddNewUser);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public String companyCode(){
        final Bundle bundle = getIntent().getExtras();
        String companyCode = bundle.getString("companyCode");
        return companyCode;
    }

    public void addUser(View view){
        EditText userName = (EditText) findViewById(R.id.inputUserName);
        EditText userEmail = (EditText) findViewById(R.id.inputUserEmail);
        EditText userPassword = (EditText) findViewById(R.id.inputUserPassword);
        spnUserPosition = (Spinner) findViewById(R.id.spinnerUserPosition);

        httpRequest_addNewUser(userName.getText().toString(),
                userEmail.getText().toString(),
                userPassword.getText().toString(),
                getSelectedPosition(),
                companyCode());

    }

    public void loadSpinnerPosition(){
        spnUserPosition = (Spinner) findViewById(R.id.spinnerUserPosition);

        staffList = new Memcache(getApplicationContext()).getStaff();
        String[] staffItems = new String[staffList.size()];
        for(int i=0; i<staffList.size(); i++){
            Staff staff = staffList.get(i);
            staffItems[i] = staff.getStaff();
        }
        ArrayAdapter<String> staffAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, staffItems);
        spnUserPosition.setAdapter(staffAdapter);
    }

    public Integer getSelectedPosition(){

        return staffList.get(spnUserPosition.getSelectedItemPosition()).getLevel();
    }

    public void httpRequest_addNewUser(String userName, String userEmail, String userPassword, int level, String companyCode){
        AddNewUser client =  StaticFunction.retrofit().create(AddNewUser.class);
        Call<Respon> call = client.setVar(userName, userEmail, StaticFunction.md5(userPassword), level, companyCode);

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
                        onBackPressed();
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

    public interface AddNewUser {
        @FormUrlEncoded
        @POST("api/v1/add-new-user")
        Call<Respon> setVar(
                @Field("name") String userName,
                @Field("email") String userEmail,
                @Field("password") String userPassword,
                @Field("level") int level,
                @Field("company_code") String companyCode
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

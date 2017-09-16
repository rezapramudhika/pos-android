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
import com.ezpz.pos.api.PostRegister;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.SendMail;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.Staff;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpz.pos.other.StaticFunction.getRandomString;

public class AddNewUserActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    Spinner spnUserPosition;
    List<Staff> staffList;
    String verification;


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
        verification = getRandomString(30);
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

        if(userName.getText().length()<2){
            Toast.makeText(getApplicationContext(), "Name at least 3 characters", Toast.LENGTH_SHORT).show();
        }else if(!StaticFunction.isValidEmail(userEmail.getText())){
            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
        }else if(userPassword.getText().length() < 6){
            Toast.makeText(getApplicationContext(), "Password at least 6 characters", Toast.LENGTH_SHORT).show();
        }else
            httpRequest_postRegister(userName.getText().toString(),
                    userEmail.getText().toString(),
                    userPassword.getText().toString(),
                    2,
                    verification,
                    companyCode());
    }

    private void sendEmail(String email, String verification) {
        //Getting content for email

        String subject="Welcome to EZPZ Point of Sale";
        String message="http://pasienesia.com/pos/api/v1/verification?email="+email+"&verification="+verification;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message, this, AddNewUserActivity.class);

        //Executing sendmail to send email
        sm.execute();
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

    public void httpRequest_postRegister(String name, final String email, String password, int level, final String verification, String companyCode){
        PostRegister client =  StaticFunction.retrofit().create(PostRegister.class);
        Call<Respon> call = client.setVar(name, email, StaticFunction.md5(password), level, companyCode, verification);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    Toast.makeText(getApplicationContext(),
                            respon.getMessage(),
                            Toast.LENGTH_LONG).show();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        sendEmail(email, verification);
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

package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    EditText emailInput, passwordInput, nameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        initVar();
    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

        nameInput = (EditText) findViewById(R.id.inputRegisterName);
        emailInput = (EditText) findViewById(R.id.inputRegisterEmail);
        passwordInput = (EditText) findViewById(R.id.inputRegisterPassword);
    }
    public void register(View view){
        if(nameInput.getText().length()<2){
            Toast.makeText(getApplicationContext(), "Name at least 3 characters", Toast.LENGTH_SHORT).show();
        }else if(!isValidEmail(emailInput.getText())){
            Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
        }else if(passwordInput.getText().length() < 7){
            Toast.makeText(getApplicationContext(), "Password at least 6 characters", Toast.LENGTH_SHORT).show();
        }else
            httpRequest_postRegister(nameInput.getText().toString(), emailInput.getText().toString(), passwordInput.getText().toString());
    }

    public void linkToLogin(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void httpRequest_postRegister(String name, String email, String password){
        Register client =  StaticFunction.retrofit().create(Register.class);
        Call<Respon> call = client.setVar(name, email, StaticFunction.md5(password));

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
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
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

    public interface Register {
        @FormUrlEncoded
        @POST("api/v1/register")
        Call<Respon> setVar(
                @Field("name") String name,
                @Field("email") String email,
                @Field("password") String password
        );
    }

}

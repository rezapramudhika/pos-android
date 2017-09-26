package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.PostRegister;
import com.ezpz.pos.other.SendMail;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.ezpz.pos.other.StaticFunction.getRandomString;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private EditText emailInput, passwordInput, nameInput, confirmPasswordInput;
    private String verification;

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
        mProgressDialog.setCanceledOnTouchOutside(false);

        nameInput = (EditText) findViewById(R.id.inputRegisterName);
        emailInput = (EditText) findViewById(R.id.inputRegisterEmail);
        passwordInput = (EditText) findViewById(R.id.inputRegisterPassword);
        confirmPasswordInput = (EditText) findViewById(R.id.inputRegisterPasswordConfirm);
        nameInput.addTextChangedListener(new StaticFunction.TextWatcher(nameInput));
        emailInput.addTextChangedListener(new StaticFunction.TextWatcher(emailInput));
        passwordInput.addTextChangedListener(new StaticFunction.TextWatcher(passwordInput));
        confirmPasswordInput.addTextChangedListener(new StaticFunction.TextWatcher(confirmPasswordInput));
        verification = getRandomString(30);

    }

    public void register(View view){
        if(nameInput.getText().length()<3){
            nameInput.setError("Name at least 3 characters");
        }else if(!StaticFunction.isValidEmail(emailInput.getText())){
            nameInput.setError("Please input a valid email");
        }else if(passwordInput.getText().length() < 6){
            passwordInput.setError( "Password at least 6 characters");
        }else if(!confirmPasswordInput.getText().toString().equals(passwordInput.getText().toString())){
            confirmPasswordInput.setError("Password not match");
        }else{
            httpRequest_postRegister(nameInput.getText().toString(),
                    emailInput.getText().toString(),
                    passwordInput.getText().toString(),
                    1,
                    "",
                    verification);
        }
    }

    private void sendEmail() {
        //Getting content for email


        String email = emailInput.getText().toString();

        String subject="Welcome to EZPZ Point of Sale";
        String message="http://pasienesia.com/pos/user/verification?email="+email+"&verification="+verification;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message, this, LoginActivity.class);

        //Executing sendmail to send email
        sm.execute();
    }

    public void linkToLogin(View view){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }


    public void httpRequest_postRegister(String name, String email, String password, int level, String companyCode, String verification){
        PostRegister client =  StaticFunction.retrofit().create(PostRegister.class);
        Call<Respon> call = client.setVar(name, email, StaticFunction.md5(password), level, companyCode, verification);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                //mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    Toast.makeText(getApplicationContext(),
                            respon.getMessage(),
                            Toast.LENGTH_LONG).show();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){

                        //Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        sendEmail();
                        //finish();
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
}

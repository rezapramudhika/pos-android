package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.PostLogin;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.SendMail;
<<<<<<< HEAD
import com.ezpz.pos.other.StaticFunction;
=======
>>>>>>> origin/master
import com.ezpz.pos.provider.Respon;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private EditText emailInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        initVar();
    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        emailInput = (EditText) findViewById(R.id.inputLoginEmail);
        passwordInput = (EditText) findViewById(R.id.inputLoginPassword);
        emailInput.addTextChangedListener(new StaticFunction.TextWatcher(emailInput));
        passwordInput.addTextChangedListener(new StaticFunction.TextWatcher(passwordInput));
    }

    public void login(View view){
        if(StaticFunction.isValidEmail(emailInput.getText()) && passwordInput.getText().length() > 5){
            httpRequest_postLogin(emailInput.getText().toString(), passwordInput.getText().toString());
        }else{
            Toast.makeText(getApplicationContext(), "Email or password doesn't valid", Toast.LENGTH_SHORT).show();
        }
    }

    public void linkToRegister(View view){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void sendEmail(String email, String verification) {

        String subject="Welcome to EZPZ Point of Sale";
        String message="http://pasienesia.com/pos/user/verification?email="+email+"&verification="+verification;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message, this, LoginActivity.class);

        //Executing sendmail to send email
        sm.execute();
    }

    public void httpRequest_postLogin(final String email, String password){
        mProgressDialog.show();
        PostLogin client =  StaticFunction.retrofit().create(PostLogin.class);
        Call<Respon> call = client.setVar(email, StaticFunction.md5(password));

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    final Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        if(respon.getUser().getConfirmed()==0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setTitle("Account has not been activated");
                            builder.setMessage("Please checkout your email to activated your account.");
                            builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setNegativeButton("Re-send activation link", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    sendEmail(email, respon.getUser().getVerification());
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }else{
                            if(respon.getLevel()==1){
                                new Memcache(getApplicationContext()).setUser(respon.getUser());
                                new Memcache(getApplicationContext()).setStaff(respon.getStaff());
                                new Memcache(getApplicationContext()).setBusinessCategory(respon.getBusinessCategory());
                                startActivity(new Intent(LoginActivity.this, BusinessListActivity.class));
                                finish();
                            }else if(respon.getLevel()==2){
                                new Memcache(getApplicationContext()).setUser(respon.getUser());
                                startActivity(new Intent(LoginActivity.this, LoadMemcachedCashier.class));
                                finish();
                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
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
}

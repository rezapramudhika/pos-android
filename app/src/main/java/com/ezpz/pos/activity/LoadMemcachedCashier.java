package com.ezpz.pos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.GetCashierData;
import com.ezpz.pos.other.Memcache;
<<<<<<< HEAD
import com.ezpz.pos.other.StaticFunction;
=======
>>>>>>> origin/master
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadMemcachedCashier extends AppCompatActivity {
    private Handler myHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_load_memcached_cashier);
        final User user = new Memcache(getApplicationContext()).getUser();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                httpRequest_getCashierData(user.getCompanyCode());
            }
        }, 3000);
    }

    public void httpRequest_getCashierData(String companyCode){
        GetCashierData client =  StaticFunction.retrofit().create(GetCashierData.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()),companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        new Memcache(getApplicationContext()).setProductCategory(respon.getCategory());
                        new Memcache(getApplicationContext()).setProduct(respon.getProduct());
                        new Memcache(getApplicationContext()).setCompany(respon.getCompanySelected());
                        startActivity(new Intent(LoadMemcachedCashier.this, CashierActivity.class));
                        finish();
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
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

package com.ezpz.pos.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ezpz.pos.BuildConfig;
import com.ezpz.pos.R;
import com.ezpz.pos.api.GetAppVersion;
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

public class SplashScreenActivity extends AppCompatActivity {
    private Handler myHandler;
    private Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        activity = this;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                httpRequest_getAppVersion();
            }
        }, 3000);
    }

    public void httpRequest_getAppVersion(){
        GetAppVersion client =  StaticFunction.retrofit().create(GetAppVersion.class);
        Call<Respon> call = client.setVar();
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        int versionCode = BuildConfig.VERSION_CODE;
                        if(versionCode!=respon.getAppVersion().getId()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setTitle("Update Available");
                            builder.setMessage("New version has been released. Please update your app!");
                            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String url = "https://play.google.com/store/apps/details?id=com.ezpz.pos";
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("Update later", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }else{
                            new Memcache(getApplicationContext()).setMailConfig(respon.getMailConfiguration());
                            final User user = new Memcache(getApplicationContext()).getUser();
                            if(user != null){
                                if(user.getLevel()==1){
                                    startActivity(new Intent(SplashScreenActivity.this, BusinessListActivity.class));
                                    finish();
                                }else if(user.getLevel()==2){
                                    startActivity(new Intent(SplashScreenActivity.this, CashierActivity.class));
                                    finish();
                                }

                            }else{
                                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                                finish();
                            }
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
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

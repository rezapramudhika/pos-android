package com.ezpz.pos.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ezpz.pos.R;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.provider.User;

public class HomeActivity extends AppCompatActivity {

    TextView nama,email,password,code;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        user = new Memcache(getApplicationContext()).getUser();
        final Bundle bundle = getIntent().getExtras();


        nama = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        password = (TextView) findViewById(R.id.password);
        code = (TextView) findViewById(R.id.code);

        nama.setText(bundle.getString("companyCode"));
        email.setText(bundle.getString("companyName"));
        password.setText(bundle.getString("companyAddress"));
        code.setText(bundle.getString("companyPhone"));


    }
}

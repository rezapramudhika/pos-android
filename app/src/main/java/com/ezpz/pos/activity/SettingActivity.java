package com.ezpz.pos.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.GetCompany;
import com.ezpz.pos.api.PostEditCompany;
import com.ezpz.pos.api.PostUpdatePassword;
import com.ezpz.pos.other.Memcache;
<<<<<<< HEAD
import com.ezpz.pos.other.StaticFunction;
=======
>>>>>>> origin/master
import com.ezpz.pos.provider.Company;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    EditText editName, editAddress, editContact, editDiscount, editTax;
    ImageView logo;
    Switch taxSwitch, discountSwitch;
    Company company;
    LinearLayout layoutCompanyTax, layoutCompanyDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initVar();
        httpRequest_getCompany(companyCode());

    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSetting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        editName = (EditText) findViewById(R.id.editCompanyName);
        editAddress = (EditText) findViewById(R.id.editCompanyAddress);
        editContact = (EditText) findViewById(R.id.editCompanyContact);
        editDiscount = (EditText) findViewById(R.id.editCompanyMemberDiscount);
        editTax = (EditText) findViewById(R.id.editCompanyTax);
        logo = (ImageView) findViewById(R.id.editCompanyLogo);
        taxSwitch = (Switch) findViewById(R.id.switchTax);
        discountSwitch = (Switch) findViewById(R.id.switchDiscount);
        layoutCompanyDiscount = (LinearLayout) findViewById(R.id.layoutCompanyDiscount);
        layoutCompanyTax = (LinearLayout) findViewById(R.id.layoutCompanyTax);
    }

    public String companyCode(){
        final Bundle bundle = getIntent().getExtras();
        final String myDataFromActivity = bundle.getString("companyCode");
        return myDataFromActivity;
    }

    public String companyId(){
        final Bundle bundle = getIntent().getExtras();
        final String myDataFromActivity = bundle.getString("companyId");
        return myDataFromActivity;
    }


    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    private void fillCompany(String name, String address, String contact, final int discount, final int tax){
        editName.setText(name);
        editAddress.setText(address);
        editContact.setText(contact);

        if (discount==0){
            discountSwitch.setChecked(false);
            discountSwitch.setText("Off");
            layoutCompanyDiscount.setVisibility(View.GONE);
        }else{
            discountSwitch.setChecked(true);
            discountSwitch.setText("On");
            layoutCompanyDiscount.setVisibility(View.VISIBLE);
            editDiscount.setText(String.valueOf(discount));
        }
        if (tax==0){
            taxSwitch.setChecked(false);
            taxSwitch.setText("Off");
            layoutCompanyTax.setVisibility(View.GONE);
        }else{
            taxSwitch.setChecked(true);
            taxSwitch.setText("On");
            layoutCompanyTax.setVisibility(View.VISIBLE);
            editTax.setText(String.valueOf(tax));
        }

        discountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(discountSwitch.isChecked()){
                    layoutCompanyDiscount.setVisibility(View.VISIBLE);
                    discountSwitch.setText("On");
                    editDiscount.setText(String.valueOf(discount));
                }else {
                    layoutCompanyDiscount.setVisibility(View.GONE);
                    discountSwitch.setText("Off");
                }
            }
        });

        taxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(taxSwitch.isChecked()){
                    layoutCompanyTax.setVisibility(View.VISIBLE);
                    taxSwitch.setText("On");
                    editTax.setText(String.valueOf(tax));
                }else {
                    layoutCompanyTax.setVisibility(View.GONE);
                    taxSwitch.setText("Off");
                }
            }
        });
    }

    public void saveChange(View view){
        String name, address, contact, discount, tax;
        name = editName.getText().toString();
        address = editAddress.getText().toString();
        contact = editContact.getText().toString();
        if (discountSwitch.getText().toString().equalsIgnoreCase("on")){
            discount = editDiscount.getText().toString();
        }else
            discount ="";
        if (taxSwitch.getText().toString().equalsIgnoreCase("on")){
            tax = editTax.getText().toString();
        }else
            tax ="";

        httpRequest_editCompany(name, address, contact, discount, tax, companyId());

    }

    public int userId(){
        User user = new Memcache(getApplicationContext()).getUser();

        return user.getId();
    }

    public void updatePassword (View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        final EditText inputOldPassword = (EditText) mView.findViewById(R.id.inputOldPassword);
        final EditText inputNewPassword = (EditText) mView.findViewById(R.id.inputNewPassword);
        final EditText inputConfirmNewPassword = (EditText) mView.findViewById(R.id.inputConfirmNewPassword);
        Button btnSubmitPassword = (Button) mView.findViewById(R.id.btnSubmitPassword);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        btnSubmitPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inputNewPassword.getText().toString().length()<7){
                    Toast.makeText(getApplicationContext(),"Password at least 6 character", Toast.LENGTH_SHORT).show();
                }
                else if(inputNewPassword.getText().toString().equalsIgnoreCase(inputConfirmNewPassword.getText().toString())){
                    httpRequest_updatePassword(userId(),
                            inputOldPassword.getText().toString(),
                            inputNewPassword.getText().toString(),
                            dialog
                    );
                }else{
                    Toast.makeText(getApplicationContext(),"Confirm password not match", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void httpRequest_updatePassword(int id, String oldPassword, String newPassword, final Dialog dialog){
        PostUpdatePassword client =  StaticFunction.retrofit().create(PostUpdatePassword.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), id, StaticFunction.md5(oldPassword), StaticFunction.md5(newPassword));

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
                        dialog.dismiss();
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

    public void httpRequest_getCompany(String companyCode){
        mProgressDialog.show();
        GetCompany client =  StaticFunction.retrofit().create(GetCompany.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()),companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        setCompany(respon.getCompanySelected());
                        fillCompany(getCompany().getCompanyName(),
                                getCompany().getAddress(),
                                getCompany().getContact(),
                                getCompany().getMemberDisc(),
                                getCompany().getTax());
                    }else{
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
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

    public void httpRequest_editCompany(String name, String address, String contact, String discount, String tax, final String companyId){
        mProgressDialog.show();
        PostEditCompany client =  StaticFunction.retrofit().create(PostEditCompany.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), name, address, contact, discount, tax, companyId);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(getApplicationContext(), ""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("companyId", companyId());
                        bundle.putString("companyCode", companyCode());
                        startActivity(new Intent(SettingActivity.this, MainPanelActivity.class).putExtras(bundle));
                        finish();
                    }else
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
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
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.adapter.SalesDetailAdapter;
import com.ezpz.pos.api.GetSalesDetail;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.Sales;
import com.ezpz.pos.provider.SalesDetail;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private TextView billNo, trnDate, cashier, customer, totalBill, memberDisc, tax, netBill, cash, change;
    private ListView billItemListView;
    private SalesDetailAdapter salesDetailAdapter;
    private List<SalesDetail> billItemProduct = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        initVar();

        httpRequest_getSalesDetail(salesId());

        salesDetailAdapter = new SalesDetailAdapter(BillActivity.this, R.layout.list_item_bill, billItemProduct);
        billItemListView.setAdapter(salesDetailAdapter);
    }

    public void initVar() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarBill);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        billNo = (TextView) findViewById(R.id.txtBillNumber);
        trnDate = (TextView) findViewById(R.id.txtDateAndTime);
        cashier = (TextView) findViewById(R.id.txtCashierName);
        customer = (TextView) findViewById(R.id.txtCustomerName);
        totalBill = (TextView) findViewById(R.id.txtTotalBill);
        memberDisc = (TextView) findViewById(R.id.txtTotalDisc);
        tax = (TextView) findViewById(R.id.txtTotalTax);
        netBill = (TextView) findViewById(R.id.txtNetBill);
        cash = (TextView) findViewById(R.id.txtTotalCash);
        change = (TextView) findViewById(R.id.txtChange);
        billItemListView = (ListView) findViewById(R.id.listViewBillItem);
    }

    private int salesId(){
        final Bundle bundle = getIntent().getExtras();
        int salesId = Integer.valueOf(bundle.getString("salesId"));
        return salesId;
    }

    private void loadBill(String txtBillNo, String txtTrnDate, String txtCashier, String txtCustomer, String txtTotalBill,
                          String txtMemberDisc, String txtTax, String txtNetBill, String txtCash, String txtChange){
        billNo.setText(": "+txtBillNo);
        trnDate.setText(": "+txtTrnDate);
        cashier.setText(": "+txtCashier);
        customer.setText(": "+txtCustomer);
        totalBill.setText(": "+StaticFunction.moneyFormat(Double.valueOf(txtTotalBill)));
        memberDisc.setText(": "+StaticFunction.moneyFormat(Double.valueOf(txtMemberDisc)));
        tax.setText(": "+StaticFunction.moneyFormat(Double.valueOf(txtTax)));
        netBill.setText(": "+StaticFunction.moneyFormat(Double.valueOf(txtNetBill)));
        cash.setText(": "+StaticFunction.moneyFormat(Double.valueOf(txtCash)));
        change.setText(": "+StaticFunction.moneyFormat(Double.valueOf(txtChange)));
    }

    public void httpRequest_getSalesDetail(int id){
        mProgressDialog.show();
        GetSalesDetail client =  StaticFunction.retrofit().create(GetSalesDetail.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()),id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        Sales sales = respon.getSelectedSales();
                        loadBill(String.valueOf(sales.getBillNumber()), sales.getCreatedAt(),
                                String.valueOf(sales.getUserId()), sales.getMemberCode(),
                                String.valueOf(sales.getTotal()), String.valueOf(sales.getDisc()),
                                String.valueOf(sales.getTax()), String.valueOf(sales.getGrandTotal()),
                                String.valueOf(sales.getCash()), String.valueOf(sales.getChange()));
                        billItemProduct.clear();
                        for (SalesDetail salesDetail : respon.getSalesDetailList()) {
                            billItemProduct.add(salesDetail);
                        }
                        salesDetailAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

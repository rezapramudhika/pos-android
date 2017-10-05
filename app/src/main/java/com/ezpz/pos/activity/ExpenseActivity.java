package com.ezpz.pos.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.adapter.ExpenseDetailAdapter;
import com.ezpz.pos.api.GetExpenseDetailList;
import com.ezpz.pos.api.PostCreateExpenseItem;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.ExpenseDetail;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private TextView txtTimestamps;
    private Button btnAddItem;
    private RecyclerView expenseRecycleView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ExpenseDetail> expenseDetails = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private int totalPrice;
    private int id, totalExpense;
    private String maxDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        initVar();
        setDate();
        loadExpenseData();
    }

    private void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarExpense);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Expense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        txtTimestamps = (TextView) findViewById(R.id.txtTimestamps);
        btnAddItem = (Button) findViewById(R.id.btnAddItem);
        expenseRecycleView = (RecyclerView) findViewById(R.id.expenseRecycleView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    }

    public String companyCode(){
        User user = new Memcache(getApplicationContext()).getUser();
        return user.getCompanyCode();
    }

    public void setDate(){
        txtTimestamps.setText(setBtnDate(dateNow()));
    }

    public String dateNow(){
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public String setBtnDate(String inputDate){
        String curDate = inputDate;
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        desiredDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String output = "";
        try {
            cal.setTime(curDateFormat.parse(curDate));
            cal.add(Calendar.DAY_OF_MONTH, +1);
            String desiredDate = desiredDateFormat.format(cal.getTime());
            output = desiredDate;
        } catch (Exception e) {}

        return output;
    }

    public void loadExpenseData(){
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        expenseRecycleView.setLayoutManager(mLayoutManager);
        expenseRecycleView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ExpenseDetailAdapter(this, expenseDetails, getApplicationContext(), dateNow(), companyCode(), true);
        expenseRecycleView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                expenseDetails.clear();
                httpRequest_getExpenseDetailList(companyCode(), dateNow());
            }
        });
        httpRequest_getExpenseDetailList(companyCode(), dateNow());
    }

    public void addItem(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ExpenseActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_expense_item, null);
        final EditText inputItemName = mView.findViewById(R.id.inputItemName);
        final EditText inputQuantity = mView.findViewById(R.id.inputQuantity);
        final EditText inputTotalPrice = mView.findViewById(R.id.inputTotalPrice);
        final Spinner spnUnit = mView.findViewById(R.id.spinnerUnit);
        String[] unit = getResources().getStringArray(R.array.unit);
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, unit);
        spnUnit.setAdapter(unitAdapter);
        final Button btnAddCashOut = mView.findViewById(R.id.btnAddExpense);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        btnAddCashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPrice(Integer.valueOf(inputTotalPrice.getText().toString()));
                httpRequest_postAddExpenseItem(
                        getId(),
                        inputItemName.getText().toString(),
                        Integer.valueOf(inputQuantity.getText().toString()),
                        spnUnit.getSelectedItem().toString(),
                        Integer.valueOf(inputTotalPrice.getText().toString()),
                        companyCode(),
                        dialog,
                        txtTimestamps.getText().toString());
            }
        });
    }

    public int totalPrice(int itemPrice){
        int total = getTotalExpense()+itemPrice;
        return total;
    }

    public void httpRequest_postAddExpenseItem(int id, String itemName, int qty, String unit, int price, final String companyCode, final Dialog dialog, String date){
        mProgressDialog.show();
        PostCreateExpenseItem client =  StaticFunction.retrofit().create(PostCreateExpenseItem.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), id, itemName, qty, unit, price, totalPrice(price), companyCode, date);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        httpRequest_getExpenseDetailList(companyCode, dateNow());
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
//                        cashOutAdapter.notifyDataSetChanged();
//                        httpRequest_getCashOut(companyCode);
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

    public void httpRequest_getExpenseDetailList(String companyCode, String date){
        mProgressDialog.show();
        GetExpenseDetailList client =  StaticFunction.retrofit().create(GetExpenseDetailList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), companyCode, date);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        if(respon.getTotalExpense()==0){
                            setId(respon.getMaxId()+1);
                        }else {
                            setId(respon.getMaxId());
                        }
                        //setMaxDate(respon.getMaxDate());
                        setTotalExpense(respon.getTotalExpense());
                        expenseDetails.clear();
                        for (ExpenseDetail expenseDetail : respon.getExpenseDetails()) {
                            expenseDetails.add(expenseDetail);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
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

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(int totalExpense) {
        this.totalExpense = totalExpense;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

package com.ezpz.pos.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.MainPanelActivity;
import com.ezpz.pos.adapter.SalesAdapter;
import com.ezpz.pos.api.GetSalesList;
import com.ezpz.pos.api.PostCreateCashIn;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.Sales;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SalesFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private RecyclerView salesList;
    private RecyclerView.Adapter adapter;
    private List<Sales> sales;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String selectedDate;
    private TextView txtTotalSales, txtTotalIncome, txtFavProduct, txtMember;
    private int totalSales, totalIncome;
    private String productFav, saveTotalIncome, saveDescription;
    private Activity thisActivity;

    public SalesFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
    }

    @RequiresApi(api = 26)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales, container, false);
        salesList = (RecyclerView) view.findViewById(R.id.salesRecycleView);
        thisActivity = getActivity();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        salesList.setLayoutManager(mLayoutManager);
        salesList.setItemAnimator(new DefaultItemAnimator());
        sales = new ArrayList<>();

        adapter = new SalesAdapter(getActivity(), sales, getContext());

        salesList.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutSales);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sales.clear();
                if(getSelectedDate()==null){
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                    httpRequest_getSalesList(companyCode(), timeStamp);
                }else{
                    httpRequest_getSalesList(companyCode(), getSelectedDate());
                }


            }
        });

        final Button btnDate = view.findViewById(R.id.buttonDatePicker);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int yy, int mm,
                                  int dd) {
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.set(yy, mm, dd);
                int day  = calendar.get(Calendar.DAY_OF_MONTH);
                int month  = calendar.get(Calendar.MONTH)+1;
                int year  = calendar.get(Calendar.YEAR);
                String date = year+"-"+month+"-"+day;
                setSelectedDate(getDate(date));
//                int dayMin = day-1;
//                String displayDate = year+"-"+month+"-"+dayMin;
                httpRequest_getSalesList(companyCode(), getDate(date));
                btnDate.setText(setBtnDate(date));
                setSaveDescription(setBtnDate(date));
            }

        };

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button btnSaveToCashIn = view.findViewById(R.id.btnSaveToCashIn);
        btnSaveToCashIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(thisActivity);
                View mView = thisActivity.getLayoutInflater().inflate(R.layout.dialog_add_cash, null);
                TextView txtTitle = mView.findViewById(R.id.txtTitleAddCash);
                final EditText inputTotalCashIn = mView.findViewById(R.id.inputTotalAddCash);
                final EditText inputCashInDescription = mView.findViewById(R.id.inputAddCashDescription);
                Button btnAddCashIn = mView.findViewById(R.id.btnAddCash);
                inputTotalCashIn.setText(String.valueOf(getTotalIncome()));
                if(getSaveDescription()==null){
                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                    inputCashInDescription.setText("(Total Income) "+setBtnDate(timeStamp));
                }else{
                    inputCashInDescription.setText("(Total Income) "+getSaveDescription());
                }

                txtTitle.setText("CASH IN");
                btnAddCashIn.setText("Save");
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                btnAddCashIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        httpRequest_postAddCashIn(Integer.valueOf(inputTotalCashIn.getText().toString()),
                                inputCashInDescription.getText().toString(),
                                companyCode(),
                                1,
                                dialog);
                    }
                });
            }
        });

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        SimpleDateFormat getDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat setBtnDate = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(getDate.parse(timeStamp));
            String desiredDate = getDate.format(calendar.getTime());
            String txtBtnDate = setBtnDate.format(calendar.getTime());
            httpRequest_getSalesList(companyCode(), desiredDate);
            btnDate.setText(txtBtnDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return view;
    }

    public void setSalesResume(int totalSales, int totalIncome, String productFav){
        txtTotalSales = getView().findViewById(R.id.txtTotalSales);
        txtTotalIncome = getView().findViewById(R.id.txtTotalIncome);
        txtFavProduct = getView().findViewById(R.id.txtFavProduct);
        txtTotalSales.setText(String.valueOf(totalSales));
        txtTotalIncome.setText(StaticFunction.moneyFormat(Double.valueOf(totalIncome)));
        txtFavProduct.setText(productFav);
        setTotalIncome(totalIncome);
    }

    public String getSaveDescription() {
        return saveDescription;
    }

    public void setSaveDescription(String saveDescription) {
        this.saveDescription = saveDescription;
    }

    public String getDate(String inputDate){
        String curDate = inputDate;
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("yyyy-MM-dd");
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

    public String companyCode(){
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getProductFav() {
        return productFav;
    }

    public void setProductFav(String productFav) {
        this.productFav = productFav;
    }


    public void httpRequest_getSalesList(String companyCode, String date){
        mProgressDialog.show();
        GetSalesList client =  StaticFunction.retrofit().create(GetSalesList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()), companyCode, date);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        sales.clear();
                        for (Sales salesResponse : respon.getSalesList()) {
                            sales.add(salesResponse);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        if (respon.getProductFav() == null){
                            setSalesResume(respon.getTotalSales(), respon.getTotalIncome(), "");
                        }else
                            setSalesResume(respon.getTotalSales(), respon.getTotalIncome(), respon.getProductFav().getProductCode());
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity().getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void httpRequest_postAddCashIn(int totalCash, String description, final String companyCode, int type, final Dialog dialog){
        mProgressDialog.show();
        PostCreateCashIn client =  StaticFunction.retrofit().create(PostCreateCashIn.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()), totalCash, description, companyCode, type);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(thisActivity.getApplicationContext(),
                            getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(thisActivity.getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//
    }

}

package com.ezpz.pos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.adapter.BillAdapter;
import com.ezpz.pos.adapter.CashierProductAdapter;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.printer.BillModel;
import com.ezpz.pos.printer.DeviceListActivity;
import com.ezpz.pos.printer.StaticValue;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Company;
import com.ezpz.pos.provider.Member;
import com.ezpz.pos.provider.Product;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;
import com.mocoo.hang.rtprinter.driver.BarcodeType;
import com.mocoo.hang.rtprinter.driver.Contants;
import com.mocoo.hang.rtprinter.driver.HsBluetoothPrintDriver;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class CashierActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private GridView productGridView;
    Spinner spnCategory;
    private List<Category> categoryList;
    ArrayAdapter<String> categoryAdapter;
    private ArrayList<Product> productList;
    CashierProductAdapter cashierProductAdapter;
    ListView billItemListView;
    BillAdapter billAdapter;
    private List<Product> billItemProduct= new ArrayList<Product>();

    private List<com.ezpz.pos.provider.Member> memberList;
    ArrayAdapter<String> memberAdapter;

    TextView txtCustomerName, txtQuantity, txtTotalPrice, txtTotalTax, txtTotalDisc, txtNetBill;
    LinearLayout layoutDisc, layoutTax;
    String selectedMemberCode;
    int stockOnGridListViewAfter;

    private static final String TAG = "BloothPrinterActivity";
    private static BluetoothDevice device;
    private static Context CONTEXT;
    private android.app.AlertDialog.Builder alertDlgBuilder;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothAdapter mBluetoothAdapter = null;
    public static HsBluetoothPrintDriver BLUETOOTH_PRINTER = null;

    private static Button mBtnConnetBluetoothDevice = null;
    private static Button mBtnPrint = null;
    private static TextView txtPrinterStatus = null;
    private static ImageView mImgPosPrinter = null;

    int change, cash, disc, tax, totalBill, netBillForPrint;
    boolean i;
    int billNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);
        initVar();
        loadData();
        getBillItem();

    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCashier);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" Cashier");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        btnCheckoutDisable();
        btnResetCustomerGone();

        alertDlgBuilder = new android.app.AlertDialog.Builder(CashierActivity.this);

        CONTEXT = getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not available in your device
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        TextView txtDateAndTime = (TextView) findViewById(R.id.txtDateAndTime);
        TextView txtCashierName = (TextView) findViewById(R.id.txtCashierName);
        txtDateAndTime.setText(getDate(dateNow()));
        User user = new Memcache(getApplicationContext()).getUser();
        txtCashierName.setText(user.getName());

        layoutDisc = (LinearLayout) findViewById(R.id.layoutCashierDisc);
        layoutTax = (LinearLayout) findViewById(R.id.layoutCashierTax);
        txtTotalTax = (TextView) findViewById(R.id.txtTotalTax);
        txtTotalDisc = (TextView) findViewById(R.id.txtTotalDisc);


        if (getCompanyDisc().equalsIgnoreCase("0")){
            layoutDisc.setVisibility(View.GONE);
        }else{
            if(getSelectedMemberCode()==null){
                txtTotalDisc.setText("");
                layoutDisc.setVisibility(View.GONE);
            }else{
                layoutDisc.setVisibility(View.VISIBLE);
            }
        }
        if (getCompanyTax().equalsIgnoreCase("0")){
            layoutTax.setVisibility(View.GONE);
        }else{
            layoutTax.setVisibility(View.VISIBLE);
        }

        Button btnReset = (Button) findViewById(R.id.btnResetCustomer);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("1");
                System.out.println("2");
                if(getCompanyDisc().equalsIgnoreCase("0")){
                    System.out.println("4");
                }else {
                    System.out.println("5");
                    billItemProduct.clear();
                    System.out.println("6");
                    sumQty();
                    System.out.println("7");
                    layoutDisc.setVisibility(View.GONE);


                    System.out.println("8");
                }
                System.out.println("9");
                setCustomerName("Guest");
                setSelectedMemberCode(null);
                btnResetCustomerGone();
            }
        });
    }

    public String getDate(String inputDate){
        String curDate = inputDate;
        SimpleDateFormat curDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String output = "";
        try {
            cal.setTime(curDateFormat.parse(curDate));
            String desiredDate = desiredDateFormat.format(cal.getTime());
            output = desiredDate;
        } catch (Exception e) {}

        return output;
    }

    public void getGridItem(int category){
        productGridView = (GridView) findViewById(R.id.gridProductList);
        productList = new ArrayList<>();
        cashierProductAdapter = new CashierProductAdapter(this, R.layout.list_item_cashier_product, productList);
        productGridView.setAdapter(cashierProductAdapter);
        httpRequest_getProductList(companyCode(), category);
        productGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getBillItem();
                billItemListView = (ListView) findViewById(R.id.listViewBillItem);
                billAdapter = new BillAdapter(CashierActivity.this, R.layout.list_item_bill, billItemProduct);

                Product productFromClick = (Product) productGridView.getAdapter().getItem(i);

                if (productFromClick.getStock()<1){
                    Toast.makeText(getApplicationContext(),"Out of stock", Toast.LENGTH_SHORT).show();
                }else{
                    billItemListView.setAdapter(billAdapter);
                    billItemProduct.add(productFromClick);
                    billAdapter.notifyDataSetChanged();
                }
                List<Product>  products = billAdapter.getListProduct();

                int sumPrice = 0;
                int sumQty = 0;

                int size = products.size();

                for(int x = 0; x < size; x++){
                    if(products.get(x).getDisc()==0){
                        sumPrice += products.get(x).getSellingPrice()*products.get(x).getItemQuantity();
                    }else {
                        int prodDisc = products.get(x).getSellingPrice()*products.get(x).getDisc()/100;
                        int finalPrice = products.get(x).getSellingPrice()-prodDisc;
                        sumPrice += finalPrice*products.get(x).getItemQuantity();
                    }

                    sumQty += products.get(x).getItemQuantity();
                }

                int companyTax = Integer.valueOf(getCompanyTax());
                int companyDisc = Integer.valueOf(getCompanyDisc());

                if(companyDisc==0){
                    int totalTax = sumPrice*companyTax/100;
                    int netBill = sumPrice+totalTax;

                    setQuantity(String.valueOf(sumQty));
                    setTotalPrice(String.valueOf(sumPrice));
                    setTotalTax(String.valueOf(totalTax));
                    setNetBill(String.valueOf(netBill));
                }else if(companyTax==0) {
                    if(getSelectedMemberCode()==null){
                        setQuantity(String.valueOf(sumQty));
                        setTotalPrice(String.valueOf(sumPrice));
                        setNetBill(String.valueOf(sumPrice));
                    }else{
                        int totalDisc = sumPrice * companyDisc/100;
                        int netBill = sumPrice - totalDisc;

                        setQuantity(String.valueOf(sumQty));
                        setTotalPrice(String.valueOf(sumPrice));
                        layoutDisc.setVisibility(View.VISIBLE);
                        setTotalDisc(String.valueOf(totalDisc));
                        setNetBill(String.valueOf(netBill));
                    }
                }else if(companyDisc==0 && companyTax==0){
                    setQuantity(String.valueOf(sumQty));
                    setNetBill(String.valueOf(sumPrice));
                }else{
                    if(getSelectedMemberCode()==null){
                        int totalTax = sumPrice*companyTax/100;
                        int netBill = sumPrice+totalTax;

                        setQuantity(String.valueOf(sumQty));
                        setTotalPrice(String.valueOf(sumPrice));
                        setTotalTax(String.valueOf(totalTax));
                        setNetBill(String.valueOf(netBill));
                    }else{
                        int totalTax = sumPrice*companyTax/100;
                        int totalDisc = sumPrice * companyDisc/100;
                        int netBill = (sumPrice-totalDisc)+totalTax;

                        setQuantity(String.valueOf(sumQty));
                        layoutDisc.setVisibility(View.VISIBLE);
                        setTotalDisc(String.valueOf(totalDisc));
                        setTotalPrice(String.valueOf(sumPrice));
                        setTotalTax(String.valueOf(totalTax));
                        setNetBill(String.valueOf(netBill));
                    }
                }



                btnCheckoutEnable();
            }
        });

    }

    public void sumQty(){
        billAdapter = new BillAdapter(CashierActivity.this, R.layout.list_item_bill, billItemProduct);
        List<Product>  products = billAdapter.getListProduct();
        int size = products.size();

        int sumPrice = 0;
        int sumQty = 0;

        for(int x = 0; x < size; x++){
            if(products.get(x).getDisc()==0){
                sumPrice += products.get(x).getSellingPrice()*products.get(x).getItemQuantity();
            }else {
                if(getSelectedMemberCode()==null){
                    sumPrice += products.get(x).getSellingPrice()*products.get(x).getItemQuantity();
                }else{
                    int prodDisc = products.get(x).getSellingPrice()*products.get(x).getDisc()/100;
                    int finalPrice = products.get(x).getSellingPrice()-prodDisc;
                    sumPrice += finalPrice*products.get(x).getItemQuantity();
                }
            }
            //sumPrice += products.get(x).getSellingPrice()*products.get(x).getItemQuantity();
            sumQty += products.get(x).getItemQuantity();
        }

//        int tax = sumPrice*10/100;
//        int netBill = sumPrice+tax;
//        setQuantity(String.valueOf(sumQty));
//        setTotalPrice(String.valueOf(sumPrice));
//        setTotalTax(String.valueOf(tax));
//        setNetBill(String.valueOf(netBill));

        int companyTax = Integer.valueOf(getCompanyTax());
        int companyDisc = Integer.valueOf(getCompanyDisc());

        if(companyDisc==0){
            int totalTax = sumPrice*companyTax/100;
            int netBill = sumPrice+totalTax;

            setQuantity(String.valueOf(sumQty));
            setTotalPrice(String.valueOf(sumPrice));
            setTotalTax(String.valueOf(totalTax));
            setNetBill(String.valueOf(netBill));
        }else if(companyTax==0) {
            if(getSelectedMemberCode()==null){
                setQuantity(String.valueOf(sumQty));
                setTotalPrice(String.valueOf(sumPrice));
                setNetBill(String.valueOf(sumPrice));
            }else{
                int totalDisc = sumPrice * companyDisc/100;
                int netBill = sumPrice - totalDisc;

                setQuantity(String.valueOf(sumQty));
                setTotalPrice(String.valueOf(sumPrice));
                layoutDisc.setVisibility(View.VISIBLE);
                setTotalDisc(String.valueOf(totalDisc));
                setNetBill(String.valueOf(netBill));
            }
        }else if(companyDisc==0 && companyTax==0){
            setQuantity(String.valueOf(sumQty));
            setNetBill(String.valueOf(sumPrice));
        }else{
            if(getSelectedMemberCode()==null){
                int totalTax = sumPrice*companyTax/100;
                int netBill = sumPrice+totalTax;

                setQuantity(String.valueOf(sumQty));
                setTotalPrice(String.valueOf(sumPrice));
                setTotalTax(String.valueOf(totalTax));
                setNetBill(String.valueOf(netBill));
            }else{
                int totalTax = sumPrice*companyTax/100;
                int totalDisc = sumPrice * companyDisc/100;
                int netBill = (sumPrice-totalDisc)+totalTax;

                setQuantity(String.valueOf(sumQty));
                layoutDisc.setVisibility(View.VISIBLE);
                setTotalDisc(String.valueOf(totalDisc));
                setTotalPrice(String.valueOf(sumPrice));
                setTotalTax(String.valueOf(totalTax));
                setNetBill(String.valueOf(netBill));
            }
        }
    }


    public void getBillItem(){
        billItemListView = (ListView) findViewById(R.id.listViewBillItem);
        billAdapter = new BillAdapter(CashierActivity.this, R.layout.list_item_bill, billItemProduct);
        billItemListView.setAdapter(billAdapter);
    }

    public void loadData(){
        spnCategory = (Spinner) findViewById(R.id.spinnerCashierCategory);
        categoryList = new Memcache(getApplicationContext()).getProductCategory();
        final String[] categoryItems = new String[categoryList.size()+1];
        categoryItems[0] = "All Product";

        int key = 1;
        for(Category category : categoryList) {
            categoryItems[key] = category.getCategoryName();
            key++;
        }
        categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryItems);
        spnCategory.setAdapter(categoryAdapter);

        spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getGridItem(spnCategory.getSelectedItemPosition()-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void populatingMemberList(List<com.ezpz.pos.provider.Member> members){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CashierActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_search_customer, null);
        final EditText inputSearch = (EditText) mView.findViewById(R.id.inputSearchMember);
        final Button btnSearch = (Button) mView.findViewById(R.id.btnSearchMember);
        final ListView listViewMember = (ListView) mView.findViewById(R.id.listMember);

        final String[] memberName = new String[members.size()];
        final String[] memberCode = new String [members.size()];
        int key = 0;
        for(Member member : members) {
            memberName[key] = member.getMemberName();
            memberCode[key] = (member.getMemberCode());
            key++;
        }
        memberAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, memberName);
        listViewMember.setAdapter(memberAdapter);



        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();


        listViewMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedMemberName = (String) listViewMember.getAdapter().getItem(i);
                selectedMemberCode = (String) memberCode[i] ;
                setSelectedMemberCode(selectedMemberCode);
                setCustomerName(selectedMemberName);
                if(getCompanyDisc().equalsIgnoreCase("0")){

                }else {
                    billItemProduct.clear();
                    layoutDisc.setVisibility(View.VISIBLE);
                    sumQty();
                }
                dialog.dismiss();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(final DialogInterface dialog, int keyCode, KeyEvent event) {
                btnSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(inputSearch.getText().toString().equalsIgnoreCase("")){

                        }else{
                            httpRequest_getMemberList(companyCode(), inputSearch.getText().toString());
                        }
                        dialog.dismiss();
                    }
                });
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(true);

    }


    public void dialogAddCustomer(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CashierActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_new_customer, null);
        final EditText inputCustomerName = (EditText) mView.findViewById(R.id.inputCustomerName);
        final EditText inputCustomerEmail = (EditText) mView.findViewById(R.id.inputCustomerEmail);
        final EditText inputCustomerAddress = (EditText) mView.findViewById(R.id.inputCustomerAddress);
        final EditText inputCustomerContact = (EditText) mView.findViewById(R.id.inputCustomerContact);
        Button btnAddCustomer = (Button) mView.findViewById(R.id.btnAddNewCustomer);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpRequest_postAddCustomer(inputCustomerName.getText().toString(),
                        inputCustomerEmail.getText().toString(),
                        inputCustomerAddress.getText().toString(),
                        inputCustomerContact.getText().toString(),
                        companyCode(),
                        dialog
                );
            }
        });
    }

    public void searchCustomer(View view){
        httpRequest_getMemberList(companyCode(), "");
    }


    public void btnCheckoutEnable(){
        Button btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnCheckout.setEnabled(true);
    }

    public void btnCheckoutDisable(){
        Button btnCheckout = (Button) findViewById(R.id.btnCheckout);
        btnCheckout.setEnabled(false);
    }

    public void btnResetCustomerGone(){
        Button btnReset = (Button) findViewById(R.id.btnResetCustomer);
        btnReset.setVisibility(View.GONE);
    }

    public void btnResetCustomerVisible(){
        Button btnReset = (Button) findViewById(R.id.btnResetCustomer);
        btnReset.setVisibility(View.VISIBLE);
    }


    public String companyCode(){
        User user = new Memcache(getApplicationContext()).getUser();
        return user.getCompanyCode();
    }

    public String getCompanyDisc(){
        Company company = new Memcache(getApplicationContext()).getCompany();
        String disc = String.valueOf(company.getMemberDisc());
        return disc;
    }

    public String getCompanyTax(){
        Company company = new Memcache(getApplicationContext()).getCompany();
        String tax = String.valueOf(company.getTax());
        return tax;
    }

    public int userId(){
        User user = new Memcache(getApplicationContext()).getUser();
        return user.getId();
    }

    public String companyDisc(){
        Company company = new Memcache(getApplicationContext()).getCompany();

        if(company.getMemberDisc()==0){
            return "";
        }else
            return String.valueOf(company.getMemberDisc());
    }

    public void setCustomerName(String customerName){
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        txtCustomerName.setText(customerName);
        btnResetCustomerVisible();
    }

    public String getCustomerName(){
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        String customerName = txtCustomerName.getText().toString();
        return customerName;
    }

    public String getSelectedMemberCode() {
        return selectedMemberCode;
    }

    public void setSelectedMemberCode(String selectedMemberCode) {
        this.selectedMemberCode = selectedMemberCode;
    }

    public void setQuantity (String quantity){
        txtQuantity = (TextView) findViewById(R.id.txtTotalItem);
        txtQuantity.setText(quantity);
    }

    public void setTotalPrice (String totalPrice){
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalBill);
        txtTotalPrice.setText(String.valueOf(totalPrice));
    }

    public void setTotalTax (String totalTax){
        txtTotalTax = (TextView) findViewById(R.id.txtTotalTax);
        txtTotalTax.setText(String.valueOf(totalTax));
    }

    public void setTotalDisc (String totalDisc){
        txtTotalDisc = (TextView) findViewById(R.id.txtTotalDisc);
        txtTotalDisc.setText(String.valueOf(totalDisc));
    }

    public void setNetBill (String netBill){
        txtNetBill = (TextView) findViewById(R.id.txtNetBill);
        txtNetBill.setText(String.valueOf(netBill));
    }


    public int getChange() {
        return change;
    }

    public void setChange(int change) {
        this.change = change;
    }

    public int getCash() {
        return cash;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public int getDisc() {
        return disc;
    }

    public void setDisc(int disc) {
        this.disc = disc;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(int totalBill) {
        this.totalBill = totalBill;
    }

    public int getNetBillForPrint() {
        return netBillForPrint;
    }

    public void setNetBillForPrint(int netBillForPrint) {
        this.netBillForPrint = netBillForPrint;
    }

    public int getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(int billNumber) {
        this.billNumber = billNumber;
    }

    public void done(){
        txtCustomerName = (TextView) findViewById(R.id.txtCustomerName);
        txtQuantity = (TextView) findViewById(R.id.txtTotalItem);
        txtTotalPrice = (TextView) findViewById(R.id.txtTotalBill);
        txtNetBill = (TextView) findViewById(R.id.txtNetBill);
        txtTotalTax = (TextView) findViewById(R.id.txtTotalTax);
        txtTotalDisc = (TextView) findViewById(R.id.txtTotalDisc);

        if(Integer.valueOf(getCompanyDisc())==0){
            httpRequest_postAddSales(
                    userId(),
                    getSelectedMemberCode(),
                    Integer.valueOf(txtQuantity.getText().toString()),
                    Integer.valueOf(txtTotalPrice.getText().toString()),
                    "",
                    txtTotalTax.getText().toString(),
                    Integer.valueOf(txtNetBill.getText().toString()),
                    companyCode()
            );
            setTotalBill(Integer.valueOf(txtTotalPrice.getText().toString()));
            setDisc(0);
            setTax(Integer.valueOf(txtTotalTax.getText().toString()));
            setNetBillForPrint(Integer.valueOf(txtNetBill.getText().toString()));
        }else if(Integer.valueOf(getCompanyTax())==0) {
            if(getSelectedMemberCode()==null){
                httpRequest_postAddSales(
                        userId(),
                        getSelectedMemberCode(),
                        Integer.valueOf(txtQuantity.getText().toString()),
                        Integer.valueOf(txtTotalPrice.getText().toString()),
                        "",
                        "",
                        Integer.valueOf(txtNetBill.getText().toString()),
                        companyCode()
                );
                setTotalBill(Integer.valueOf(txtTotalPrice.getText().toString()));
                setDisc(0);
                setTax(0);
                setNetBillForPrint(Integer.valueOf(txtNetBill.getText().toString()));
            }else{
                httpRequest_postAddSales(
                        userId(),
                        getSelectedMemberCode(),
                        Integer.valueOf(txtQuantity.getText().toString()),
                        Integer.valueOf(txtTotalPrice.getText().toString()),
                        txtTotalDisc.getText().toString(),
                        "",
                        Integer.valueOf(txtNetBill.getText().toString()),
                        companyCode()
                );
                setTotalBill(Integer.valueOf(txtTotalPrice.getText().toString()));
                setDisc(Integer.valueOf(txtTotalDisc.getText().toString()));
                setTax(0);
                setNetBillForPrint(Integer.valueOf(txtNetBill.getText().toString()));
            }
        }else if(Integer.valueOf(getCompanyTax())==0 && Integer.valueOf(getCompanyDisc())==0){
            httpRequest_postAddSales(
                    userId(),
                    getSelectedMemberCode(),
                    Integer.valueOf(txtQuantity.getText().toString()),
                    Integer.valueOf(txtTotalPrice.getText().toString()),
                    "",
                    "",
                    Integer.valueOf(txtNetBill.getText().toString()),
                    companyCode()
            );
            setTotalBill(Integer.valueOf(txtTotalPrice.getText().toString()));
            setDisc(0);
            setTax(0);
            setNetBillForPrint(Integer.valueOf(txtNetBill.getText().toString()));
        }else{
            if(getSelectedMemberCode()==null){
                httpRequest_postAddSales(
                        userId(),
                        getSelectedMemberCode(),
                        Integer.valueOf(txtQuantity.getText().toString()),
                        Integer.valueOf(txtTotalPrice.getText().toString()),
                        "",
                        txtTotalTax.getText().toString(),
                        Integer.valueOf(txtNetBill.getText().toString()),
                        companyCode()
                );
                setTotalBill(Integer.valueOf(txtTotalPrice.getText().toString()));
                setDisc(0);
                setTax(Integer.valueOf(txtTotalTax.getText().toString()));
                setNetBillForPrint(Integer.valueOf(txtNetBill.getText().toString()));
            }else{
                httpRequest_postAddSales(
                        userId(),
                        getSelectedMemberCode(),
                        Integer.valueOf(txtQuantity.getText().toString()),
                        Integer.valueOf(txtTotalPrice.getText().toString()),
                        txtTotalDisc.getText().toString(),
                        txtTotalTax.getText().toString(),
                        Integer.valueOf(txtNetBill.getText().toString()),
                        companyCode()
                );
                setTotalBill(Integer.valueOf(txtTotalPrice.getText().toString()));
                setDisc(Integer.valueOf(txtTotalDisc.getText().toString()));
                setTax(Integer.valueOf(txtTotalTax.getText().toString()));
                setNetBillForPrint(Integer.valueOf(txtNetBill.getText().toString()));
            }
        }


//        int totalPrice = Integer.valueOf(txtTotalPrice.getText().toString());
//        int totalDisc = 0;
//        if (companyDisc() == ""){
//            totalDisc = 0;
//        }else
//            totalDisc = totalPrice*Integer.valueOf(companyDisc())/100;
//
//        int totalTax = Integer.valueOf(txtTotalTax.getText().toString());
//        int netBill = Integer.valueOf(txtNetBill.getText().toString());
//        int totalNetBill;
//        int grandTotal;
//        if (totalDisc==0){
//            totalNetBill = netBill;
//        }else{
//            grandTotal = totalPrice-totalDisc;
//            totalNetBill = grandTotal+totalTax;
//        }


        List<Product>  products = billAdapter.getListProduct();
        String productCode;
        int sellingPrice;
        String disc;
        int subTotal;
        String discTotal;
        int discPrice;
        int productId;
        int size = products.size();

        for(int x = 0; x < size; x++){
            productId = products.get(x).getId();
            productCode = products.get(x).getProductCode();
            sellingPrice = products.get(x).getSellingPrice();
            disc = String.valueOf(products.get(x).getDisc());
            if (disc == "0"){
                discTotal = "0";
                subTotal = sellingPrice;
            }else {
                discTotal = disc;
                discPrice = sellingPrice * Integer.valueOf(discTotal)/100;
                subTotal = sellingPrice - discPrice;
            }
            int productQuantity = products.get(x).getItemQuantity();
            for(int y = 0; y < productQuantity; y++){
                httpRequest_postAddSalesDetail(productId, productCode, getSelectedMemberCode(), sellingPrice, discTotal, subTotal, companyCode());
            }
        }

    }

    public void setBillModelArrayList(){
        billAdapter = new BillAdapter(CashierActivity.this, R.layout.list_item_bill, billItemProduct);
        List<Product>  products = billAdapter.getListProduct();
        String name;
        int qty;
        int yy, dd;
        int size = products.size();
        for(int x = 0; x < size; x++){
            name = products.get(x).getProductName();
            qty = products.get(x).getItemQuantity();
            yy = products.get(x).getSellingPrice();
            dd = products.get(x).getDisc();
            BillModel.generatedMoneyReceipt(name, qty, yy, dd);


        }
    }

    public void checkout(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CashierActivity.this);
        final View mView = getLayoutInflater().inflate(R.layout.dialog_checkout, null);
        final EditText inputCash = (EditText) mView.findViewById(R.id.inputCash);
        final TextView inputChange = (TextView) mView.findViewById(R.id.inputChange);
        final Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
        final Button btnPay = (Button) mView.findViewById(R.id.btnPay);
        final Button btnDone = (Button) mView.findViewById(R.id.btnDone);
        final Button btnPrint = (Button) mView.findViewById(R.id.btnPrint);
        final TextView inputGrandTotal = (TextView) mView.findViewById(R.id.inputGrandTotal);
        final LinearLayout layoutChange = (LinearLayout) mView.findViewById(R.id.layoutChange);
        layoutChange.setVisibility(View.GONE);

        int grandTotal = Integer.valueOf(txtNetBill.getText().toString());
        inputGrandTotal.setText(String.valueOf(grandTotal));


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputCash.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Input cash!", Toast.LENGTH_SHORT).show();
                }else{
                    int change = Integer.valueOf(inputCash.getText().toString())-Integer.valueOf(inputGrandTotal.getText().toString());
                    inputChange.setText(String.valueOf(change));
                    inputCash.setEnabled(false);
                    btnCancel.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                    btnPay.setBackgroundColor(getResources().getColor(R.color.colorLightGrey));
                    btnCancel.setEnabled(false);
                    btnPay.setEnabled(false);
                    layoutChange.setVisibility(View.VISIBLE);
                    done();
                }
                setBillModelArrayList();
                setChange(Integer.valueOf(inputChange.getText().toString()));
                setCash(Integer.valueOf(inputCash.getText().toString()));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startActivity(new Intent(CashierActivity.this, CashierActivity.class));
                StaticValue.billModelArrayList.clear();
                finish();
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BLUETOOTH_PRINTER.IsNoConnection()){
                    Intent intent = new Intent(CashierActivity.this, DeviceListActivity.class);
                    startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                }else {
                    printBillFromOrder(CashierActivity.this);
                }
            }
        });




    }


    public void httpRequest_getProductList(String companyCode, Integer category){
        String idCategory;

        if (category<0){
            idCategory = "";
        }else
            idCategory = String.valueOf(categoryList.get(category).getId());

        mProgressDialog.show();
        GetProductList client =  StaticFunction.retrofit().create(GetProductList.class);
        Call<Respon> call = client.setVar(companyCode, idCategory);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        productList.clear();
                        for (Product product : respon.getProduct()) {
                            productList.add(product);
                        }
                        cashierProductAdapter.notifyDataSetChanged();
                        setBillNumber(respon.getBillNumber());
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

    public interface GetProductList {
        @GET("api/v1/get-product")
        Call<Respon> setVar(
                @Query("company_code") String companyCode,
                @Query("category") String category
        );
    }

    public void httpRequest_postAddCustomer(String name, String email, String address, String contact, String companyCode, final Dialog dialog){
        mProgressDialog.show();
        AddNewCustomer client =  StaticFunction.retrofit().create(AddNewCustomer.class);
        Call<Respon> call = client.setVar(name, email, address, contact, companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
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



    public interface AddNewCustomer {
        @FormUrlEncoded
        @POST("api/v1/add-new-member")
        Call<Respon> setVar(
                @Field("name") String name,
                @Field("email") String email,
                @Field("address") String address,
                @Field("contact") String contact,
                @Field("company_code") String companyCode
        );
    }

    public void httpRequest_getMemberList(String companyCode, String inputSearch){
        mProgressDialog.show();
        GetMember client =  StaticFunction.retrofit().create(GetMember.class);
        Call<Respon> call = client.setVar(companyCode, inputSearch);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        populatingMemberList(respon.getMemberList());
                        memberAdapter.notifyDataSetChanged();
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

    public interface GetMember {
        @GET("api/v1/get-member")
        Call<Respon> setVar(
                @Query("company_code") String companyCode,
                @Query("search") String search
        );
    }

    public void httpRequest_postAddSales(int idUser, String memberCode, int quantity, int total, String disc, String tax, int grandTotal, String companyCode){
        AddSales client =  StaticFunction.retrofit().create(AddSales.class);
        Call<Respon> call = client.setVar(idUser, memberCode, quantity, total, disc, tax, grandTotal, companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        setBillNumber(respon.getBillNumber());
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



    public interface AddSales {
        @FormUrlEncoded
        @POST("api/v1/add-sales")
        Call<Respon> setVar(
                @Field("user_id") int idUser,
                @Field("member_code") String memberCode,
                @Field("quantity") int quantity,
                @Field("total") int total,
                @Field("disc") String disc,
                @Field("tax") String tax,
                @Field("grand_total") int grandTotal,
                @Field("company_code") String companyCode
        );
    }

    public void httpRequest_postAddSalesDetail(int productId, String productCode, String memberCode, int sellingPrice, String disc, int subtotal, String companyCode){
        AddSalesDetail client =  StaticFunction.retrofit().create(AddSalesDetail.class);
        Call<Respon> call = client.setVar(productId, productCode, memberCode, sellingPrice, disc, subtotal, companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
//                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
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



    public interface AddSalesDetail {
        @FormUrlEncoded
        @POST("api/v1/add-sales-detail")
        Call<Respon> setVar(
                @Field("product_id") int productId,
                @Field("product_code") String productCode,
                @Field("member_code") String memberCode,
                @Field("selling_price") int sellingPrice,
                @Field("disc") String disc,
                @Field("subtotal") int subtotal,
                @Field("company_code") String companyCode
        );
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that to be enabled.
        // initializeBluetoothDevice() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (BLUETOOTH_PRINTER == null){
                initializeBluetoothDevice();
            }else{
                if(BLUETOOTH_PRINTER.IsNoConnection()){
                    ///mImgPosPrinter.setImageResource(R.drawable.pos_printer_offliine);
                }else{
                    //Toast.makeText(getApplicationContext(),""+R.string.title_connected_to, Toast.LENGTH_SHORT).show();
                    //txtPrinterStatus.setText(R.string.title_connected_to);
                    //txtPrinterStatus.append(device.getCategoryName());
                    //mImgPosPrinter.setImageResource(R.drawable.pos_printer);
                }
            }

        }
    }

    static class BluetoothHandler extends Handler {
        private final WeakReference<CashierActivity> myWeakReference;

        //Creating weak reference of BluetoothPrinterActivity class to avoid any leak
        BluetoothHandler(CashierActivity weakReference) {
            myWeakReference = new WeakReference<CashierActivity>(weakReference);
        }
        @Override
        public void handleMessage(Message msg)
        {
            CashierActivity bluetoothPrinterActivity = myWeakReference.get();
            if (bluetoothPrinterActivity != null) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                switch (data.getInt("flag")) {
                    case Contants.FLAG_STATE_CHANGE:
                        int state = data.getInt("state");
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + state);
                        switch (state) {
                            case HsBluetoothPrintDriver.CONNECTED_BY_BLUETOOTH:
                                //Toast.makeText(getApplicationContext(),""+R.string.title_connected_to, Toast.LENGTH_SHORT).show();
                                //txtPrinterStatus.setText(R.string.title_connected_to);
                               // txtPrinterStatus.append(device.getCategoryName());
                                StaticValue.isPrinterConnected=true;
                                Toast.makeText(CONTEXT,"Connection successful.", Toast.LENGTH_SHORT).show();
                                //mImgPosPrinter.setImageResource(R.drawable.pos_printer);
                                break;
                            case HsBluetoothPrintDriver.FLAG_SUCCESS_CONNECT:
                                //txtPrinterStatus.setText(R.string.title_connecting);
                                break;

                            case HsBluetoothPrintDriver.UNCONNECTED:
                                //txtPrinterStatus.setText(R.string.no_printer_connected);
                                break;
                        }
                        break;
                    case Contants.FLAG_SUCCESS_CONNECT:
                        //txtPrinterStatus.setText(R.string.title_connecting);
                        break;
                    case Contants.FLAG_FAIL_CONNECT:
                        Toast.makeText(CONTEXT,"Connection failed.",Toast.LENGTH_SHORT).show();
                        //mImgPosPrinter.setImageResource(R.drawable.pos_printer_offliine);
                        break;
                    default:
                        break;

                }
            }
        };
    }


    private void initializeBluetoothDevice() {
        Log.d(TAG, "setupChat()");
        // Initialize HsBluetoothPrintDriver class to perform bluetooth connections
        BLUETOOTH_PRINTER = HsBluetoothPrintDriver.getInstance();//
        BLUETOOTH_PRINTER.setHandler(new BluetoothHandler(CashierActivity.this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    BLUETOOTH_PRINTER.start();
                    BLUETOOTH_PRINTER.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    initializeBluetoothDevice();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

            getMenuInflater().inflate(R.menu.cashier_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.connectPrinter){
//            Bundle bundle = new Bundle();
//            bundle.putString("companyCode",companyCode);
            //Intent serverIntent = null;

                //If bluetooth is disabled then ask user to enable it again
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }else{//If the connection is lost with last connected bluetooth printer
                    if(BLUETOOTH_PRINTER.IsNoConnection()){
                        Intent intent = new Intent(CashierActivity.this, DeviceListActivity.class);
                        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                    }else{ //If an existing connection is still alive then ask user to kill it and re-connect again
                        Toast.makeText(getApplicationContext(),""+R.string.alert_title, Toast.LENGTH_SHORT).show();
                        alertDlgBuilder.setTitle(getResources().getString(R.string.alert_title));
                        alertDlgBuilder.setMessage(getResources().getString(R.string.alert_message));
                        alertDlgBuilder.setNegativeButton(getResources().getString(R.string.alert_btn_negative), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }
                        );
                        alertDlgBuilder.setPositiveButton(getResources().getString(R.string.alert_btn_positive), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        BLUETOOTH_PRINTER.stop();
                                        Intent intent = new Intent(CashierActivity.this, DeviceListActivity.class);
                                        startActivityForResult(intent, REQUEST_CONNECT_DEVICE);
                                    }
                                }
                        );
                        alertDlgBuilder.show();

                    }
                }

            //onPause();
        }else if(id == R.id.changePassword){
            changePassword();
            //testPrint(CashierActivity.this);
        }else if(id == R.id.logout){
            logoutUser();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        new Memcache(getApplicationContext()).logout();
        Intent intent = new Intent(CashierActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    public void changePassword(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(CashierActivity.this);
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
                if(inputNewPassword.getText().toString().equalsIgnoreCase(inputConfirmNewPassword.getText().toString())){
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

    public String dateNow(){
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }

    public void httpRequest_updatePassword(int id, String oldPassword, String newPassword, final Dialog dialog){
        UpdatePassword client =  StaticFunction.retrofit().create(UpdatePassword.class);
        Call<Respon> call = client.setVar(id, StaticFunction.md5(oldPassword), StaticFunction.md5(newPassword));

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

    public interface UpdatePassword {
        @FormUrlEncoded
        @POST("api/v1/change-password")
        Call<Respon> setVar(
                @Field("id") int id,
                @Field("old_password") String oldPassword,
                @Field("new_password") String newPassword
        );
    }

    public void testPrint(Context context){
        CashierActivity.BLUETOOTH_PRINTER.Begin();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//CENTER
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//30 * 0.125mm
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x10);//normal
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n\n"+"TEST PRINT"+"\n");
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.AddCodePrint(BarcodeType.CODE128, "Reza123");
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n\nTT\n\n");
        CashierActivity.BLUETOOTH_PRINTER.AddCodePrint(BarcodeType.UPC_E, "Reza123");
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n\nTT\n\n");
        CashierActivity.BLUETOOTH_PRINTER.AddCodePrint(BarcodeType.EAN13, "081283891447");
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n\nTT\n\n");
        CashierActivity.BLUETOOTH_PRINTER.AddCodePrint(BarcodeType.UPC_A, "081283891447");
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n\nTT\n\n");
        CashierActivity.BLUETOOTH_PRINTER.AddCodePrint(BarcodeType.ITF, "081283891447");
    }

    public void printBillFromOrder(Context context){

        Company company = new Memcache(getApplicationContext()).getCompany();
        User user = new Memcache(getApplicationContext()).getUser();
        String userName = user.getName();
        String companyName = company.getCompanyName();
        String companyAddress = company.getAddress();
        String companyContact = company.getContact();

        int billDisc = getDisc();
        int billTax = getTax();
        int billTotalBill = getTotalBill();
        int billNetBill = getNetBillForPrint();

        if(CashierActivity.BLUETOOTH_PRINTER.IsNoConnection()){
        }

        int totalBill=0, netBill=0, totalVat=0, totalDisc=0;

        //LF = Line feed
        CashierActivity.BLUETOOTH_PRINTER.Begin();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//CENTER
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//30 * 0.125mm
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x10);//normal
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n\n"+companyName+"\n");

        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//CENTER
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//30 * 0.125mm
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal
        CashierActivity.BLUETOOTH_PRINTER.BT_Write(companyAddress+"\n");

        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//CENTER
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//30 * 0.125mm
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal
        CashierActivity.BLUETOOTH_PRINTER.BT_Write(companyContact+"\n");

        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);
        //BT_Write() method will initiate the printer to start printing.
        CashierActivity.BLUETOOTH_PRINTER.BT_Write(
                        "\nBill No: " + getBillNumber() +
                        "\nTrn. Date: " + dateNow() +
                        "\nCashier: " + userName + "\nCustomer: " + getCustomerName()+"\n");

        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
        CashierActivity.BLUETOOTH_PRINTER.LF();

        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//LEFT
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//50 * 0.125mm
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font
        //receiptHeadBuffer.append(Util.nameLeftValueRightJustify("Device:", hwDevice, DataConstants.RECEIPT_WIDTH) + "\n");

        //static sales record are generated
        // BillModel.generatedMoneyReceipt();

        for(int i = 0; i<StaticValue.billModelArrayList.size(); i++){
            BillModel salesModel = StaticValue.billModelArrayList.get(i);

            if(salesModel.getUnitDisc()==0){
                CashierActivity.BLUETOOTH_PRINTER.LF();
                CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//LEFT
                CashierActivity.BLUETOOTH_PRINTER.BT_Write(salesModel.getProductShortName()+"\n");
                CashierActivity.BLUETOOTH_PRINTER.LF();
                //CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//LEFT
                CashierActivity.BLUETOOTH_PRINTER.BT_Write(StaticValue.nameLeftValueRightJustify(salesModel.getSalesAmount() + "x" + salesModel.getUnitSalesCost(),StaticFunction.moneyFormat(Double.valueOf(salesModel.getSalesAmount() * salesModel.getUnitSalesCost())) , 32));
                //CashierActivity.BLUETOOTH_PRINTER.BT_Write(salesModel.getSalesAmount() + "x" + salesModel.getUnitSalesCost() +"         ");
//                CashierActivity.BLUETOOTH_PRINTER.LF();
//                CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//LEFT
//                CashierActivity.BLUETOOTH_PRINTER.BT_Write(StaticValue.CURRENCY + salesModel.getSalesAmount() * salesModel.getUnitSalesCost());
            }else{
                int unitDisc = salesModel.getUnitSalesCost()*salesModel.getUnitDisc()/100;
                int unitFinalPrice = salesModel.getUnitSalesCost()-unitDisc;
                CashierActivity.BLUETOOTH_PRINTER.LF();
                CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//LEFT
                CashierActivity.BLUETOOTH_PRINTER.BT_Write(salesModel.getProductShortName()+"\n");
                //CashierActivity.BLUETOOTH_PRINTER.BT_Write(salesModel.getProductShortName()+" (Disc:"+salesModel.getUnitDisc()+"%)\n");
                CashierActivity.BLUETOOTH_PRINTER.LF();
               // CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//LEFT
                CashierActivity.BLUETOOTH_PRINTER.BT_Write(StaticValue.nameLeftValueRightJustify(salesModel.getSalesAmount() + "x" + salesModel.getUnitSalesCost()+" (Disc:"+salesModel.getUnitDisc()+"%)", StaticFunction.moneyFormat(Double.valueOf(salesModel.getSalesAmount() * unitFinalPrice)), 32));
               // CashierActivity.BLUETOOTH_PRINTER.BT_Write(salesModel.getSalesAmount() + "x" + salesModel.getUnitSalesCost() +"");
               // CashierActivity.BLUETOOTH_PRINTER.LF();
               // CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//LEFT
               // CashierActivity.BLUETOOTH_PRINTER.BT_Write("         "+StaticValue.CURRENCY + salesModel.getSalesAmount() * unitFinalPrice);
            }
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n");
            totalBill=totalBill + (salesModel.getUnitSalesCost() * salesModel.getSalesAmount());

        }

        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//RIGHT
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//50 * 0.125mm
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte)0x00);//normal font


        if(billDisc==0 && billTax>0){

            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("Total Bill: " + StaticFunction.moneyFormat(Double.valueOf(billTotalBill)) +"\n");

            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("Tax: " +
                    StaticFunction.moneyFormat(Double.valueOf(billTax))+"\n");

            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
            CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
            CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
            CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("Net Bill: " + StaticFunction.moneyFormat(Double.valueOf(billNetBill))+"\n");

        }else if(billTax==0 && billDisc>0){

            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("Total Bill: " + StaticFunction.moneyFormat(Double.valueOf(billTotalBill))+"\n");

            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("Discount: -" + StaticFunction.moneyFormat(Double.valueOf(billDisc))+"\n");

            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
            CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
            CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
            CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("Net Bill: " + StaticFunction.moneyFormat(Double.valueOf(billNetBill))+"\n");
        }else if(billDisc>0){
            if (billTax>0){

                CashierActivity.BLUETOOTH_PRINTER.LF();
                CashierActivity.BLUETOOTH_PRINTER.BT_Write("Total Bill: " + StaticFunction.moneyFormat(Double.valueOf(billTotalBill))+"\n");

                CashierActivity.BLUETOOTH_PRINTER.LF();
                CashierActivity.BLUETOOTH_PRINTER.BT_Write("Member Disc: -" +
                        StaticFunction.moneyFormat(Double.valueOf(billDisc))+"\n");

                CashierActivity.BLUETOOTH_PRINTER.LF();
                CashierActivity.BLUETOOTH_PRINTER.BT_Write("Tax: " +
                        StaticFunction.moneyFormat(Double.valueOf(billTax))+"\n");

                CashierActivity.BLUETOOTH_PRINTER.LF();
                CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
                CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


                CashierActivity.BLUETOOTH_PRINTER.LF();
                CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
                CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
                CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);
                CashierActivity.BLUETOOTH_PRINTER.BT_Write("Net Bill: " + StaticFunction.moneyFormat(Double.valueOf(billNetBill))+"\n");
        }
        }else if(billDisc<1 && billTax<1){

//            CashierActivity.BLUETOOTH_PRINTER.LF();
//            CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
//            CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));


            CashierActivity.BLUETOOTH_PRINTER.LF();
            CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
            CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
            CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);
            CashierActivity.BLUETOOTH_PRINTER.BT_Write("Net Bill: " + StaticFunction.moneyFormat(Double.valueOf(billNetBill))+"\n");
        }

//        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//RIGHT
//        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//50 * 0.125mm
//        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte)0x00);//normal font
//
//        totalVat=Double.parseDouble(Utility.doubleFormatter(totalBill*(StaticValue.VAT/100)));
//        netBill=totalBill+totalVat;
//
//        CashierActivity.BLUETOOTH_PRINTER.LF();
//        CashierActivity.BLUETOOTH_PRINTER.BT_Write("Total Bill: " + StaticValue.CURRENCY + Utility.doubleFormatter(totalBill)+"\n");
//
//        CashierActivity.BLUETOOTH_PRINTER.LF();
//        CashierActivity.BLUETOOTH_PRINTER.BT_Write(Double.toString(StaticValue.VAT) + "% Tax: " +
//                StaticValue.CURRENCY + Utility.doubleFormatter(totalVat)+"\n");
//
//        CashierActivity.BLUETOOTH_PRINTER.LF();
//        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
//        CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
//
//
//        CashierActivity.BLUETOOTH_PRINTER.LF();
//        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
//        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
//        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);
//        CashierActivity.BLUETOOTH_PRINTER.BT_Write("Net Bill: " + StaticValue.CURRENCY + Utility.doubleFormatter(netBill)+"\n");
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("Cash: " + StaticFunction.moneyFormat(Double.valueOf(getCash()))+"\n");

        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font
        CashierActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));

        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
        CashierActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("Change: " + StaticFunction.moneyFormat(Double.valueOf(getChange()))+"\n");

//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//left
//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write("VAT Reg. No:" + StaticValue.VAT_REGISTRATION_NUMBER);
//
//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//left
//		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(StaticValue.BRANCH_ADDRESS);

        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//Center
        CashierActivity.BLUETOOTH_PRINTER.BT_Write("\n\nThank You\n\n\n");

        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
        CashierActivity.BLUETOOTH_PRINTER.LF();
    }


}

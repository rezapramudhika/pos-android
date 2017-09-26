package com.ezpz.pos.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.GetCategoryList;
import com.ezpz.pos.api.GetCompany;
import com.ezpz.pos.api.PostCreateMember;
import com.ezpz.pos.fragment.DashboardFragment;
import com.ezpz.pos.fragment.ExpenseFragment;
import com.ezpz.pos.fragment.MemberFragment;
import com.ezpz.pos.fragment.ProductFragment;
import com.ezpz.pos.fragment.SalesFragment;
import com.ezpz.pos.fragment.UserFragment;
import com.ezpz.pos.other.Memcache;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Company;
import com.ezpz.pos.provider.Respon;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPanelActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private TextView txtCompanyName, txtCompanyAddress, txtCompanyContact;
    private Toolbar toolbar;
    private String companyCode, companyId;
    private List<Category> categoryList;
    private static List<Category> passCategoryList;
    private FragmentTransaction transaction;
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_DASHBOARD = "dashboard";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_SALES = "sales";
    private static final String TAG_EXPENSE = "expense";
    private static final String TAG_MEMBER = "member";
    private static final String TAG_USER = "user";
    public static String CURRENT_TAG = TAG_DASHBOARD;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    private ProgressDialog mProgressDialog;


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Bundle bundleStates = new Bundle();
        Integer i = bundleStates.getInt("state");
        outState.putInt("state", i);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_panel);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        final Bundle bundle = getIntent().getExtras();
        companyCode = bundle.getString("companyCode");
        companyId = bundle.getString("companyId");

        httpRequest_getCategoryList(companyCode);
        httpRequest_getCompany(companyCode);
        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);




        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_DASHBOARD;
            loadHomeFragment();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader(String companyName, String companyAddress, String companyContact) {
        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtCompanyName = navHeader.findViewById(R.id.headerCompanyName);
        txtCompanyAddress = navHeader.findViewById(R.id.headerCompanyAddress);
        txtCompanyContact = navHeader.findViewById(R.id.headerCompanyContact);
        txtCompanyName.setText(companyName);
        if(companyAddress.equalsIgnoreCase(null)){
            txtCompanyAddress.setVisibility(View.GONE);
        }else{
            txtCompanyAddress.setVisibility(View.VISIBLE);
            txtCompanyAddress.setText(companyAddress);
        }
        if(companyContact.equalsIgnoreCase(null)){
            txtCompanyContact.setVisibility(View.GONE);
        }else{
            txtCompanyContact.setVisibility(View.VISIBLE);
            txtCompanyContact.setText(companyContact);
        }
    }


    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public String getCompanyCode(){
        return companyCode;
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                DashboardFragment dashboardFragment = new DashboardFragment();
                return dashboardFragment;
            case 1:
                // photos
                ProductFragment productFragment = new ProductFragment();
                return productFragment;
            case 2:
                // movies fragment
                SalesFragment salesFragment = new SalesFragment();
                return salesFragment;
            case 3:
                // notifications fragment
                ExpenseFragment expenseFragment = new ExpenseFragment();;
                return expenseFragment;

            case 4:
                // settings fragment
                MemberFragment memberFragment = new MemberFragment();
                return memberFragment;

            case 5:
                // settings fragment
                UserFragment userFragment = new UserFragment();
                return userFragment;

            default:
                return new DashboardFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_dashboard:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_DASHBOARD;
                        break;
                    case R.id.nav_product:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PRODUCT;
                        break;
                    case R.id.nav_sales:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_SALES;
                        break;
                    case R.id.nav_expense:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_EXPENSE;
                        break;
                    case R.id.nav_member:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_MEMBER;
                        break;
                    case R.id.nav_user:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_USER;
                        break;
                    case R.id.nav_logout:
                        logoutUser();
                        return true;
                    case R.id.nav_setting:
                        Bundle bundle = new Bundle();
                        bundle.putString("companyCode",companyCode);
                        bundle.putString("companyId",companyId);
                        startActivity(new Intent(MainPanelActivity.this, SettingActivity.class).putExtras(bundle));
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
//        if (shouldLoadHomeFragOnBackPress) {
//            // checking if user is on other navigation menu
//            // rather than home
//            if (navItemIndex != 0) {
//                navItemIndex = 0;
//                CURRENT_TAG = TAG_DASHBOARD;
//                loadHomeFragment();
//                return;
//            }
//        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds itemss to the action bar if it is present.
        // show menu only when home fragment is selected
        if (navItemIndex == 1) {
            getMenuInflater().inflate(R.menu.main_panel, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.setting_manage_category){
            Bundle bundle = new Bundle();
            bundle.putString("companyCode",companyCode);
            Intent intent = new Intent(MainPanelActivity.this, ManageCategory.class).putExtras(bundle);
            startActivity(intent);
            //onPause();
        }
        //noinspection SimplifiableIfStatement

        // user is in notifications fragment
        // and selected 'Mark all as Read'

        // user is in notifications fragment
        // and selected 'Clear All'

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (navItemIndex == 1) {
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("companyCode", companyCode);
                    Intent intent = new Intent(getApplicationContext(), AddNewProductActivity.class).putExtras(bundle1);
                    startActivity(intent);
                }
            });
        }else if (navItemIndex == 5){
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle1 = new Bundle();
                bundle1.putString("companyCode", companyCode);
                Intent intent = new Intent(getApplicationContext(), AddNewUserActivity.class).putExtras(bundle1);
                startActivity(intent);
            }
        });
        }else if (navItemIndex == 4){
            fab.show();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainPanelActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_add_new_customer, null);
                    final EditText inputCustomerName = (EditText) mView.findViewById(R.id.inputCustomerName);
                    inputCustomerName.addTextChangedListener(new StaticFunction.TextWatcher(inputCustomerName));
                    final EditText inputCustomerEmail = (EditText) mView.findViewById(R.id.inputCustomerEmail);
                    inputCustomerEmail.addTextChangedListener(new StaticFunction.TextWatcher(inputCustomerEmail));
                    final EditText inputCustomerAddress = (EditText) mView.findViewById(R.id.inputCustomerAddress);
                    inputCustomerAddress.addTextChangedListener(new StaticFunction.TextWatcher(inputCustomerAddress));
                    final EditText inputCustomerContact = (EditText) mView.findViewById(R.id.inputCustomerContact);
                    inputCustomerContact.addTextChangedListener(new StaticFunction.TextWatcher(inputCustomerContact));
                    Button btnAddCustomer = (Button) mView.findViewById(R.id.btnAddNewCustomer);
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                    btnAddCustomer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(inputCustomerName.getText().toString().equalsIgnoreCase("")){
                                inputCustomerName.setError("Please input customer name");
                            }else if(!StaticFunction.isValidEmail(inputCustomerEmail.getText())){
                                inputCustomerEmail.setError("Please input a valid email");
                            }else if(inputCustomerAddress.getText().toString().equalsIgnoreCase("")){
                                inputCustomerAddress.setError("Please input customer address");
                            }else if(inputCustomerContact.getText().toString().equalsIgnoreCase("")){
                                inputCustomerContact.setError("Please input customer contact");
                            }else
                            httpRequest_postAddCustomer(inputCustomerName.getText().toString(),
                                    inputCustomerEmail.getText().toString(),
                                    inputCustomerAddress.getText().toString(),
                                    inputCustomerContact.getText().toString(),
                                    getCompanyCode(),
                                    dialog
                            );
                        }
                    });
                }
            });
        }else
            fab.hide();
    }

    private void logoutUser() {
        new Memcache(getApplicationContext()).logout();
        Intent intent = new Intent(MainPanelActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    public void httpRequest_getCategoryList(String id){
        //mProgressDialog.show();
       GetCategoryList client =  StaticFunction.retrofit().create(GetCategoryList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()),id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                //mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        setCategoryList(respon.getCategory());
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

    public void httpRequest_postAddCustomer(String name, String email, String address, String contact, String companyCode, final Dialog dialog){
        mProgressDialog.show();
        PostCreateMember client =  StaticFunction.retrofit().create(PostCreateMember.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(getApplicationContext()), name, email, address, contact, companyCode);
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


    public void httpRequest_getCompany(final String companyCode){
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
                        Company company = respon.getCompanySelected();
                        loadNavHeader(company.getCompanyName(), company.getAddress(), company.getContact());
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
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

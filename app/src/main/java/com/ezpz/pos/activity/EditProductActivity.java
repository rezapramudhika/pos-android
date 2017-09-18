package com.ezpz.pos.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ezpz.pos.R;
import com.ezpz.pos.api.GetCategoryList;
import com.ezpz.pos.api.GetProductDetail;
import com.ezpz.pos.api.GetProductPicture;
import com.ezpz.pos.api.PostDeleteProduct;
import com.ezpz.pos.api.PostUpdateProductDiscount;
import com.ezpz.pos.api.PostUpdateProductInformation;
import com.ezpz.pos.api.PostUpdateProductStock;
import com.ezpz.pos.other.FileUtils;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Product;
import com.ezpz.pos.provider.Respon;
import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class EditProductActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private List<Category> categoryList;
    private ImageView imageProfile;
    private int PICK_IMAGE_REQUEST = 1;
    private String url_profile = "";
    private String categoryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        initVar();
        setLayout();
    }

    public void initVar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarEditProduct);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


    }

    private int productId(){
        final Bundle bundle = getIntent().getExtras();
        int productId = bundle.getInt("productId");
        return productId;
    }

    public String companyCode(){
        final Bundle bundle = getIntent().getExtras();
        final String companyCode = bundle.getString("companyCode");
        return companyCode;
    }


    public String getUrl_profile() {
        return url_profile;
    }

    public void setUrl_profile(String url_profile) {
        this.url_profile = url_profile;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setLayout(){
        final LinearLayout layoutEditInformation = (LinearLayout) findViewById(R.id.layoutEditInformation);
        final LinearLayout layoutEditStock = (LinearLayout) findViewById(R.id.layoutEditStock);
        final LinearLayout layoutEditDiscount= (LinearLayout) findViewById(R.id.layoutEditDiscount);
        final LinearLayout layoutDeleteProduct= (LinearLayout) findViewById(R.id.layoutDeleteProduct);

        Spinner spnSelectEdit = (Spinner) findViewById(R.id.spinnerSelectLayout);
        String[] items = getResources().getStringArray(R.array.edit_product_layout);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, items);
        spnSelectEdit.setAdapter(categoryAdapter);

        spnSelectEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    layoutEditInformation.setVisibility(View.VISIBLE);
                    layoutEditStock.setVisibility(View.GONE);
                    layoutEditDiscount.setVisibility(View.GONE);
                    layoutDeleteProduct.setVisibility(View.GONE);
                    httpRequest_getProductInformation(productId(), i);
                }else if(i==1){
                    layoutEditInformation.setVisibility(View.GONE);
                    layoutEditStock.setVisibility(View.VISIBLE);
                    layoutEditDiscount.setVisibility(View.GONE);
                    layoutDeleteProduct.setVisibility(View.GONE);
                    httpRequest_getProductInformation(productId(), i);
                }else if(i==2){
                    layoutEditInformation.setVisibility(View.GONE);
                    layoutEditStock.setVisibility(View.GONE);
                    layoutEditDiscount.setVisibility(View.VISIBLE);
                    layoutDeleteProduct.setVisibility(View.GONE);
                    httpRequest_getProductInformation(productId(), i);
                }else if(i==3){
                    layoutEditInformation.setVisibility(View.GONE);
                    layoutEditStock.setVisibility(View.GONE);
                    layoutEditDiscount.setVisibility(View.GONE);
                    layoutDeleteProduct.setVisibility(View.VISIBLE);
                    deleteProduct();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void getProductInformation(String productCode, String productName, String purchasePrice, String sellingPrice, String description, String categoryName, String picture){
        final Bundle bundle = getIntent().getExtras();
        final int productId = bundle.getInt("productId");
        final String companyCode = bundle.getString("companyCode");
        setCategoryName(categoryName);
        final EditText editProductCode= (EditText) findViewById(R.id.editProductCode);
        editProductCode.addTextChangedListener(new StaticFunction.TextWatcher(editProductCode));
        final EditText editProductName = (EditText) findViewById(R.id.editProductName);
        editProductName.addTextChangedListener(new StaticFunction.TextWatcher(editProductName));
        final EditText editPurchasePrice = (EditText) findViewById(R.id.editPurchasePrice);
        editPurchasePrice.addTextChangedListener(new StaticFunction.TextWatcher(editPurchasePrice));
        final EditText editSellingPrice = (EditText) findViewById(R.id.editSellingPrice);
        editSellingPrice.addTextChangedListener(new StaticFunction.TextWatcher(editSellingPrice));
        final EditText editDescription = (EditText) findViewById(R.id.editDescription);
        editDescription.addTextChangedListener(new StaticFunction.TextWatcher(editDescription));
        final Spinner spnCategory = (Spinner) findViewById(R.id.spinnerEditProductCategory);
        Button btnUpdateInformation = (Button) findViewById(R.id.btnEditProductInformation);

        imageProfile = (ImageView) findViewById(R.id.editProductPicture);
        Glide.with(getApplicationContext()).load(StaticFunction.imageUrl(getUrl_profile())).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageProfile);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imageProfile.setImageBitmap(null);
                Crop.pickImage(EditProductActivity.this);
            }
        });

        httpRequest_getProductPicture(productId());


        editProductCode.setText(productCode);
        editProductName.setText(productName);
        editPurchasePrice.setText(purchasePrice);
        editSellingPrice.setText(sellingPrice);
        editDescription.setText(description);


        spnCategory.setSelection(getSpinnerSelectedItem(spnCategory, categoryName));
        populatingSpinnerCategory();
        btnUpdateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editProductCode.getText().toString().equalsIgnoreCase("")){
                    editProductCode.setError("Please input product code");
                }else if(editProductName.getText().toString().equalsIgnoreCase("")){
                    editProductName.setError("Please input product name");
                }else if(editPurchasePrice.getText().toString().equalsIgnoreCase("")){
                    editPurchasePrice.setError("Please input purchase price");
                }else if(editSellingPrice.getText().toString().equalsIgnoreCase("")){
                    editSellingPrice.setError("Please input selling price");
                }else {
                    httpRequest_updateProductInformation(
                            productId,
                            editProductCode.getText().toString(),
                            editProductName.getText().toString(),
                            categoryList.get(spnCategory.getSelectedItemPosition()).getId(),
                            Integer.valueOf(editPurchasePrice.getText().toString()),
                            Integer.valueOf(editSellingPrice.getText().toString()),
                            editDescription.getText().toString(),
                            companyCode,
                            getUrl_profile()
                    );
                }
            }
        });
    }

    private void populatingSpinnerCategory(){
        httpRequest_getCategoryList(companyCode());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            httpPost_uploadProfile(Crop.getOutput(result), String.valueOf(productId()));
            imageProfile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {

            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void httpPost_uploadProfile(Uri fileUri, String id_user) {
        File file = FileUtils.getFile(this, fileUri);
        RequestBody idUser = RequestBody.create(MediaType.parse("text/plain"), id_user);
        RequestBody photoBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part photoPart = MultipartBody.Part.createFormData("photo", file.getName(), photoBody);

        mProgressDialog.show();
        UploadImage client = StaticFunction.retrofit().create(UploadImage.class);
        Call<Respon> call = client.setVar(idUser, photoPart);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()){
                    Respon respon = response.body();
                    Toast.makeText(getApplicationContext(),
                            respon.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        setUrl_profile(respon.getImage());
                    }
                }else{
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"GAGAL", Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
            }
        });
    }

    private String encodeImage(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String encImage = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encImage;
    }

    public interface UploadImage{
        @Multipart
        @POST("api/v1/upload-image")
        Call<Respon> setVar(
                @Part("id_product") RequestBody idUser,
                @Part MultipartBody.Part photo);
    }

    private int getSpinnerSelectedItem(Spinner spinner, String myString){
        int index = 0;
        for (int i=0; i<spinner.getCount(); i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    public void getProductStock(String inStock){
        final Bundle bundle = getIntent().getExtras();
        final int productId = bundle.getInt("productId");

        final EditText productInStock= (EditText) findViewById(R.id.editInStock);
        final EditText editProductStock = (EditText) findViewById(R.id.editAddStock);
        Button btnUpdateStock = (Button) findViewById(R.id.btnUpdateStock);
        productInStock.setText(inStock);

        btnUpdateStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editProductStock.getText().toString().equalsIgnoreCase("")){

                }else{
                    final int totalStock = Integer.valueOf(productInStock.getText().toString()) + Integer.valueOf(editProductStock.getText().toString());
                    httpRequest_updateProductStock(productId, totalStock);
                    editProductStock.setText("");
                }
           }
        });
    }

    public void refreshStock(View view){
        final Bundle bundle = getIntent().getExtras();
        final int productId = bundle.getInt("productId");

        httpRequest_getProductInformation(productId, 1);
        final EditText editProductStock = (EditText) findViewById(R.id.editAddStock);

        editProductStock.setText("");
    }

    public void getProductDiscount(String disc){
        final Bundle bundle = getIntent().getExtras();
        final int productId = bundle.getInt("productId");

        final EditText editProductDisc= (EditText) findViewById(R.id.editDiscount);
        Button btnUpdateDisc = (Button) findViewById(R.id.btnUpdateDiscount);
        editProductDisc.setText(disc);
        btnUpdateDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editProductDisc.getText().toString().equalsIgnoreCase("")){

                }else {
                    httpRequest_updateProductDiscount(productId, Integer.valueOf(editProductDisc.getText().toString()));
                }
            }
        });
    }

    public void deleteProduct(){
        final Bundle bundle = getIntent().getExtras();
        final int productId = bundle.getInt("productId");
        Button btnDeleteProduct = (Button) findViewById(R.id.btnDeleteProduct);
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpRequest_deleteProduct(productId);
            }
        });
    }


    public void httpRequest_updateProductInformation(final int productId, String productCode, String name, int category, int purchasePrice, int sellingPrice, String description, String companyCode, String url_profile){
        PostUpdateProductInformation client =  StaticFunction.retrofit().create(PostUpdateProductInformation.class);
        Call<Respon> call = client.setVar(productId, productCode, name, category, purchasePrice, sellingPrice, description, companyCode, url_profile);

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
                        httpRequest_getProductInformation(productId, 0);
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

    public void httpRequest_updateProductStock(final int productId, int stock){
        mProgressDialog.show();
        PostUpdateProductStock client =  StaticFunction.retrofit().create(PostUpdateProductStock.class);
        Call<Respon> call = client.setVar(productId, stock);
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
                        httpRequest_getProductInformation(productId, 1);
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

    public void httpRequest_updateProductDiscount(final int productId, int disc){
        mProgressDialog.show();
        PostUpdateProductDiscount client =  StaticFunction.retrofit().create(PostUpdateProductDiscount.class);
        Call<Respon> call = client.setVar(productId, disc);

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
                        httpRequest_getProductInformation(productId, 2);
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

    public void httpRequest_deleteProduct(int productId){
        mProgressDialog.show();
        PostDeleteProduct client =  StaticFunction.retrofit().create(PostDeleteProduct.class);
        Call<Respon> call = client.setVar(productId);

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
                        onBackPressed();
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

    public void httpRequest_getProductInformation(int id, final int index){
        mProgressDialog.show();
        GetProductDetail client =  StaticFunction.retrofit().create(GetProductDetail.class);
        Call<Respon> call = client.setVar(id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        Product product = respon.getProductDetail();
                        if (index==0) {
                             getProductInformation(product.getProductCode(),
                                    product.getProductName(),
                                    String.valueOf(product.getPurchasePrice()),
                                    String.valueOf(product.getSellingPrice()),
                                    product.getDescription(),
                                    product.getCategoryName(),
                                    product.getPicture()
                            );
                            setUrl_profile(respon.getProductDetail().getPicture());
                        }else if(index==1){
                            getProductStock(String.valueOf(product.getStock()));
                        }else if(index==2){
                            getProductDiscount(String.valueOf(product.getDisc()));
                        }
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

    public void httpRequest_getProductPicture(int id){
        mProgressDialog.show();
        GetProductPicture client =  StaticFunction.retrofit().create(GetProductPicture.class);
        Call<Respon> call = client.setVar(id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        if(respon.getProductPict().getPicture().equals("")){
                            //Toast.makeText(getApplicationContext(),"Gak Ada"+respon.getProductPict().getPicture(), Toast.LENGTH_LONG).show();
                        }else{
                            setUrl_profile(respon.getProductPict().getPicture());
                            imageProfile = (ImageView) findViewById(R.id.editProductPicture);
                            Glide.with(getApplicationContext()).load(StaticFunction.imageUrl(getUrl_profile())).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageProfile);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else {
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

    public void httpRequest_getCategoryList(String id){
       GetCategoryList client =  StaticFunction.retrofit().create(GetCategoryList.class);
        Call<Respon> call = client.setVar(id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        Spinner spnCategory = (Spinner) findViewById(R.id.spinnerEditProductCategory);
                        categoryList = respon.getCategory();
                        final String[] categoryItems = new String[categoryList.size()];
                        int key = 0;
                        for(Category category : categoryList) {
                            categoryItems[key] = category.getCategoryName();
                            key++;
                        }
                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categoryItems);
                        spnCategory.setAdapter(categoryAdapter);
                        spnCategory.setSelection(getSpinnerSelectedItem(spnCategory, getCategoryName()));
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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

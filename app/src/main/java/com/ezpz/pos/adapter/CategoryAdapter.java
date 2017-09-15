package com.ezpz.pos.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.ManageCategory;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Category;
import com.ezpz.pos.provider.Respon;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by RezaPramudhika on 8/23/2017.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    List<Category> categoryList;
    Context context;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Category category = categoryList.get(position);
        holder.txtCategoryName.setText(category.getCategoryName());
        holder.btnDeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle("Confirm");
                builder.setMessage("Deleting category will also delete all products in this category. Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        httpRequest_deleteCategory(category.getId(), category.getCategoryCompanyCode());
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtCategoryName;
        private Button btnDeleteCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCategoryName = (TextView) itemView.findViewById(R.id.txtCategoryName);
            btnDeleteCategory = (Button) itemView.findViewById(R.id.btnDeleteCategory);
        }
    }

    public void httpRequest_deleteCategory(int id, final String companyCode){
        DeleteCategory client =  StaticFunction.retrofit().create(DeleteCategory.class);
        Call<Respon> call = client.setVar(id, companyCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        CategoryAdapter.this.categoryList.clear();
                        ((ManageCategory) context).httpRequest_getCategory(companyCode);
                        Toast.makeText(context.getApplicationContext(), ""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context.getApplicationContext(),
                            "Server offline",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {

                Toast.makeText(context.getApplicationContext(),
                        context.getApplicationContext().getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface DeleteCategory {
        @FormUrlEncoded
        @POST("api/v1/delete-category")
        Call<Respon> setVar(
                @Field("id") int id,
                @Field("company_code") String companyCode
        );
    }
}

package com.ezpz.pos.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.PostDeleteUser;
import com.ezpz.pos.fragment.UserFragment;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RezaPramudhika on 8/30/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> userList;
    Context context;
    Activity thisActivity;
    UserFragment fragment;

    public UserAdapter(Activity thisActivity, List<User> userList, Context context, UserFragment userFragment) {
        this.userList = userList;
        this.context = context;
        this.thisActivity = thisActivity;
        this.fragment = userFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.txtUserName.setText(user.getName());
        String i = "";
        if(user.getLevel()==1){
            i="Owner";
        }else if(user.getLevel()==2){
            i="Cashier";
        }
        holder.txtUserPosition.setText(i);

        holder.layoutUserItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final CharSequence[] items = {"Delete User"};

                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

                builder.setTitle("Select The Action");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setTitle("Delete "+user.getName());
                            builder.setMessage("Are you sure?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    httpRequest_deleteUser(user.getId(), dialog, user.getCompanyCode());
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
                    }
                });
                builder.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUserName;
        private TextView txtUserPosition;
        private LinearLayout layoutUserItem;


        public ViewHolder(View itemView) {
            super(itemView);
            txtUserName = (TextView) itemView.findViewById(R.id.txtUserName);
            txtUserPosition = (TextView) itemView.findViewById(R.id.txtUserPosition);
            layoutUserItem = (LinearLayout) itemView.findViewById(R.id.layoutUserItem);
        }
    }

    public void httpRequest_deleteUser(int id, final DialogInterface dialog, final String companyCode){
        PostDeleteUser client =  StaticFunction.retrofit().create(PostDeleteUser.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        fragment.httpRequest_getUser(companyCode, 2);
                        dialog.dismiss();
                    }else
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(thisActivity.getApplicationContext(),
                            thisActivity.getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                Toast.makeText(thisActivity.getApplicationContext(),
                        thisActivity.getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
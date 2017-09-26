package com.ezpz.pos.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.api.GetMemberDetail;
import com.ezpz.pos.api.PostDeleteMember;
import com.ezpz.pos.api.PostEditCustomer;
import com.ezpz.pos.fragment.MemberFragment;
<<<<<<< HEAD
import com.ezpz.pos.other.StaticFunction;
=======
>>>>>>> origin/master
import com.ezpz.pos.provider.Member;
import com.ezpz.pos.provider.Respon;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by RezaPramudhika on 8/28/2017.
 */

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {

    List<Member> memberList;
    Context context;
    Activity thisActivity;
    MemberFragment fragment;

    public MemberAdapter(Activity thisActivity, List<Member> memberList, Context context, MemberFragment fragment) {
        this.memberList = memberList;
        this.context = context;
        this.thisActivity = thisActivity;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_company_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Member member = memberList.get(position);

        holder.txtMemberCode.setText(member.getMemberCode());
        holder.txtMemberName.setText(": "+member.getMemberName());
        holder.txtTimestamps.setText(": "+getDate(member.getCreatedAt()));
        holder.layoutMemberItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final CharSequence[] items = {"Total Purchase", "Edit Member", "Delete Member"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

                builder.setTitle(member.getMemberCode()+"-"+member.getMemberName());
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if(item==0){
                            httpRequest_getSelectedMember(member.getId(), member.getMemberCode(), member.getMemberName());
                        }else if(item==1){
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(thisActivity);
                            View mView = thisActivity.getLayoutInflater().inflate(R.layout.dialog_add_new_customer, null);
                            final EditText inputCustomerName = (EditText) mView.findViewById(R.id.inputCustomerName);
                            final EditText inputCustomerEmail = (EditText) mView.findViewById(R.id.inputCustomerEmail);
                            final EditText inputCustomerAddress = (EditText) mView.findViewById(R.id.inputCustomerAddress);
                            final EditText inputCustomerContact = (EditText) mView.findViewById(R.id.inputCustomerContact);
                            final TextView txtTitle = (TextView) mView.findViewById(R.id.titleEditCustomer);
                            Button btnAddCustomer = (Button) mView.findViewById(R.id.btnAddNewCustomer);
                            btnAddCustomer.setText(R.string.btn_save_change);
                            txtTitle.setText(R.string.txt_title_edit_member);
                            inputCustomerName.setText(member.getMemberName());
                            inputCustomerEmail.setText(member.getMemberEmail());
                            inputCustomerAddress.setText(member.getMemberAddress());
                            inputCustomerContact.setText(member.getMemberContact());
                            mBuilder.setView(mView);
                            final AlertDialog dialogs = mBuilder.create();
                            dialogs.setCanceledOnTouchOutside(true);
                            dialogs.show();
                            btnAddCustomer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    httpRequest_postEditCustomer(member.getId(),
                                            inputCustomerName.getText().toString(),
                                            inputCustomerEmail.getText().toString(),
                                            inputCustomerAddress.getText().toString(),
                                            inputCustomerContact.getText().toString(),
                                            member.getMemberCompanyCode(),
                                            dialogs
                                    );
                                }
                            });
                        }else if(item==2){
                            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                            builder.setIcon(android.R.drawable.ic_dialog_alert);
                            builder.setTitle("Delete "+member.getMemberName());
                            builder.setMessage("Are you sure?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    httpRequest_deleteMember(member.getId(), member.getMemberCompanyCode(), dialog);
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
        return memberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTimestamps;
        private TextView txtMemberCode;
        private TextView txtMemberName;
        private LinearLayout layoutMemberItem;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTimestamps = (TextView) itemView.findViewById(R.id.txtJoinDate);
            txtMemberCode = (TextView) itemView.findViewById(R.id.txtMemberCode);
            txtMemberName = (TextView) itemView.findViewById(R.id.txtMemberName);
            layoutMemberItem = (LinearLayout) itemView.findViewById(R.id.layoutMemberItem);
        }
    }

    public String getDate(String inputDate){
        String curDate = inputDate;
        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat desiredDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String output = "";
        try {
            cal.setTime(curDateFormat.parse(curDate));
            String desiredDate = desiredDateFormat.format(cal.getTime());
            output = desiredDate;
        } catch (Exception e) {}

        return output;
    }

    public void httpRequest_postEditCustomer(int id, String name, String email, String address, String contact, String companyCode, final Dialog dialog){
        //mProgressDialog.show();
        PostEditCustomer client =  StaticFunction.retrofit().create(PostEditCustomer.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id, name, email, address, contact, companyCode);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                //mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }else{
                    Toast.makeText(thisActivity.getApplicationContext(),
                            thisActivity.getApplicationContext().getResources().getString(R.string.error_async_text),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {
                Toast.makeText(thisActivity.getApplicationContext(),
                        thisActivity.getApplicationContext().getResources().getString(R.string.error_async_text),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    public void httpRequest_getSelectedMember(int id, final String memberCode, final String memberName) {
        GetMemberDetail client = StaticFunction.retrofit().create(GetMemberDetail.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id, memberCode);

        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if (response.isSuccessful()) {
                    Respon respon = response.body();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setTitle(memberCode+"-"+memberName);
                    if(respon.getTotalPurchase()==0){
                        builder.setMessage("Member hasn't made any transactions yet.");
                    }else{
                        String totalPurchase = StaticFunction.moneyFormat(Double.valueOf(respon.getTotalPurchase()));
                        String favProduct = respon.getProductFav().getProductCode();
                        builder.setMessage("Total Purchase: "+totalPurchase+"\n"+"Favorite Product: "+favProduct);
                    }
                    builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }

            @Override
            public void onFailure(Call<Respon> call, Throwable t) {

            }
        });
    }

    public void httpRequest_deleteMember(int id, final String companyCode, final DialogInterface dialog){
        PostDeleteMember client =  StaticFunction.retrofit().create(PostDeleteMember.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()),id);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if(respon.getStatusCode().equalsIgnoreCase("200")){
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_SHORT).show();
                        fragment.httpRequest_getMemberList(companyCode, "");
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

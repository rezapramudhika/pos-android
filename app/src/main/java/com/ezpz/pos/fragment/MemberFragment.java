package com.ezpz.pos.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.MainPanelActivity;
import com.ezpz.pos.adapter.MemberAdapter;
import com.ezpz.pos.api.GetMemberList;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Member;
import com.ezpz.pos.provider.Respon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MemberFragment extends Fragment {
    ProgressDialog mProgressDialog;
    private List<Member> memberList = new ArrayList<>();
    private RecyclerView memberListRecyclerView;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    View view;
    private Activity thisActivity;
    private Context thisContext;
    private EditText inputMemberName;
    private ImageButton btnSearchMember;

    public MemberFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_member, container, false);
        thisActivity = getActivity();
        thisContext = getContext();
        inputMemberName = view.findViewById(R.id.inputSearchMember);
        btnSearchMember = view.findViewById(R.id.btnSearchMember);
        memberListRecyclerView = (RecyclerView) view.findViewById(R.id.memberListRecycleView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        memberListRecyclerView.setLayoutManager(mLayoutManager);
        memberListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MemberAdapter(thisActivity, memberList, thisContext, this);
        memberListRecyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutMember);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                memberList.clear();
                    httpRequest_getMemberList(companyCode(), "");
            }
        });
        httpRequest_getMemberList(companyCode(), "");
        btnSearchMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity().getApplicationContext(),""+companyCode()+inputMemberName.getText().toString(),Toast.LENGTH_SHORT).show();
                httpRequest_getMemberList(companyCode(), inputMemberName.getText().toString());
            }
        });

        return view;
    }

    public String companyCode(){
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }

    public void httpRequest_getMemberList(String companyCode, String inputSearch){
        mProgressDialog.show();
        GetMemberList client =  StaticFunction.retrofit().create(GetMemberList.class);
        Call<Respon> call = client.setVar(StaticFunction.apiToken(thisActivity.getApplicationContext()), companyCode, inputSearch);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        memberList.clear();
                        for (Member member : respon.getMemberList()) {
                            memberList.add(member);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }else{
                        Toast.makeText(thisActivity.getApplicationContext(),""+respon.getMessage(), Toast.LENGTH_LONG).show();
                    }
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

}

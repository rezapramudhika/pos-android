package com.ezpz.pos.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ezpz.pos.R;
import com.ezpz.pos.activity.MainPanelActivity;
import com.ezpz.pos.adapter.UserAdapter;
import com.ezpz.pos.other.StaticFunction;
import com.ezpz.pos.provider.Respon;
import com.ezpz.pos.provider.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class UserFragment extends Fragment {

    private ProgressDialog mProgressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView userList;
    private RecyclerView.Adapter adapter;
    private List<User> users;
    private UserFragment fragment;

    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        fragment = this;
        userList = (RecyclerView) view.findViewById(R.id.userListRecycleView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        userList.setLayoutManager(mLayoutManager);
        userList.setItemAnimator(new DefaultItemAnimator());

        users = new ArrayList<>();

        adapter = new UserAdapter(getActivity(), users, getContext(), fragment);

        userList.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutProduct);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                users.clear();
                httpRequest_getUser(companyCode(), 2);

            }
        });
        httpRequest_getUser(companyCode(), 2);
        return view;
    }

    public String companyCode(){
        MainPanelActivity activity = (MainPanelActivity) getActivity();
        final String myDataFromActivity = activity.getCompanyCode();
        return myDataFromActivity;
    }

    public void httpRequest_getUser (String companyCode, int level){
        mProgressDialog.show();
        GetUser client =  StaticFunction.retrofit().create(GetUser.class);
        Call<Respon> call = client.setVar(companyCode, level);
        call.enqueue(new Callback<Respon>() {
            @Override
            public void onResponse(Call<Respon> call, Response<Respon> response) {
                mProgressDialog.dismiss();
                if(response.isSuccessful()) {
                    Respon respon = response.body();
                    if (respon.getStatusCode().equals("200")) {
                        users.clear();
                        for (User user : respon.getStaffList()) {
                            users.add(user);
                        }
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
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

    public interface GetUser {
        @GET("api/v1/get-staff")
        Call<Respon> setVar(
                @Query("company_code") String companyCode,
                @Query("level") int level
        );
    }


}

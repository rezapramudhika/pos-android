package com.ezpz.pos.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ezpz.pos.R;
import com.ezpz.pos.adapter.ViewPagerAdapter;


public class ExpenseFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog mProgressDialog;
    private CashInFragment cashInFragment;
    private CashOutFragment cashOutFragment;
    private View thisView;

    public ExpenseFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);
        thisView = view;
        initVar();
        setupViewPager(viewPager);
        return view;
    }

    private void initVar(){
        viewPager = thisView.findViewById(R.id.viewpager);
        tabLayout = thisView.findViewById(R.id.tabs);
        cashInFragment = new CashInFragment();
        cashOutFragment = new CashOutFragment();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(cashInFragment, getActivity().getResources().getString(R.string.txt_title_cash_in));
        adapter.addFrag(cashOutFragment, getActivity().getResources().getString(R.string.txt_title_cash_out));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0);
        tabLayout.getTabAt(1);
    }

}

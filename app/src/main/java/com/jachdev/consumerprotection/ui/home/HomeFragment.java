package com.jachdev.consumerprotection.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.commonlibs.widget.CircleImageView;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.FirebasePredictionData;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.ui.adapter.CommonAdaptor;
import com.jachdev.consumerprotection.util.Helper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.tv_header)
    CustomTextView tvHeader;
    @BindView(R.id.btn_add_org)
    FrameLayout btnAddOrg;
    @BindView(R.id.view_org)
    LinearLayoutCompat layoutOrg;
    @BindView(R.id.iv_logo)
    CircleImageView ivLogo;
    @BindView(R.id.tv_org_name)
    CustomTextView tvOrgName;
    @BindView(R.id.tv_org_description)
    CustomTextView tvOrgDesc;

    @BindView(R.id.recyclerViewSales)
    RecyclerView recyclerViewSales;
    private CommonAdaptor<FirebasePredictionData> salesAdapter;
    private List<FirebasePredictionData> sales = new ArrayList<>();

    @BindView(R.id.recyclerViewPrice)
    RecyclerView recyclerViewPrice;
    private CommonAdaptor<FirebasePredictionData> priceAdapter;
    private List<FirebasePredictionData> price = new ArrayList<>();

    @BindView(R.id.recyclerViewImport)
    RecyclerView recyclerViewImport;
    private CommonAdaptor<FirebasePredictionData> importAdapter;
    private List<FirebasePredictionData> imp = new ArrayList<>();

    private HomeViewModel homeViewModel;
    private FragmentEventListener listener;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        showWaiting();

        init();

        homeViewModel.getUser().observe(getViewLifecycleOwner(), mUserObserver);
        homeViewModel.getOrganization().observe(getViewLifecycleOwner(), mOrgObserver);
        homeViewModel.getSalesPredictionData().observe(getViewLifecycleOwner(), mSalesPredictionObserver);
        homeViewModel.getPricePredictionData().observe(getViewLifecycleOwner(), mPricePredictionObserver);
        homeViewModel.getImportPredictionData().observe(getViewLifecycleOwner(), mImportPredictionObserver);
    }

    private void init() {
        //sales
        salesAdapter = new CommonAdaptor<>(getBaseActivity(), sales, new CommonAdaptor.CommonAdaptorCallback() {
            @Override
            public void onView(int viewType, int position) {

            }
        });
        salesAdapter.setViewType(CommonAdaptor.VIEW_TYPE_FIREBASE_PREDICTION);

        recyclerViewSales.setLayoutManager(new GridLayoutManager(getBaseActivity(), 2));
        recyclerViewSales.setAdapter(salesAdapter);

        //price
        priceAdapter = new CommonAdaptor<>(getBaseActivity(), price, new CommonAdaptor.CommonAdaptorCallback() {
            @Override
            public void onView(int viewType, int position) {

            }
        });
        priceAdapter.setViewType(CommonAdaptor.VIEW_TYPE_FIREBASE_PREDICTION);

        recyclerViewPrice.setLayoutManager(new GridLayoutManager(getBaseActivity(), 2));
        recyclerViewPrice.setAdapter(priceAdapter);

        //imports
        importAdapter = new CommonAdaptor<>(getBaseActivity(), imp, new CommonAdaptor.CommonAdaptorCallback() {
            @Override
            public void onView(int viewType, int position) {

            }
        });
        importAdapter.setViewType(CommonAdaptor.VIEW_TYPE_FIREBASE_PREDICTION);

        recyclerViewImport.setLayoutManager(new GridLayoutManager(getBaseActivity(), 2));
        recyclerViewImport.setAdapter(importAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listener = (FragmentEventListener) context;
    }

    private Observer<? super User> mUserObserver = new Observer<User>() {
        @Override
        public void onChanged(@Nullable User user) {
            String header = getString(R.string.hello) + " " + user.getEmail();
            tvHeader.setAnyText(header);
        }
    };

    private Observer<? super Organization> mOrgObserver = new Observer<Organization>() {
        @Override
        public void onChanged(Organization organization) {
            dismissWaiting();
            if(organization == null){
                btnAddOrg.setVisibility(View.VISIBLE);
                layoutOrg.setVisibility(View.GONE);
            }else{
                layoutOrg.setVisibility(View.VISIBLE);
                btnAddOrg.setVisibility(View.GONE);

                Helper.loadImageByUrl(ivLogo, organization.getLogo());
                tvOrgName.setAnyText(organization.getName());
                tvOrgDesc.setAnyText(organization.getDescription());
            }
        }
    };

    private Observer<? super HashMap<String, FirebasePredictionData>> mSalesPredictionObserver = new Observer<HashMap<String, FirebasePredictionData>>() {
        @Override
        public void onChanged(HashMap<String, FirebasePredictionData> map) {

            List<FirebasePredictionData> sales = new ArrayList<>();

            if(map == null)
                return;

            for (String key : map.keySet()) {
                FirebasePredictionData data = map.get(key);
                data.setName(key);
                sales.add(data);
            }

            salesAdapter.setItems(sales);

        }
    };

    private Observer<? super HashMap<String, FirebasePredictionData>> mPricePredictionObserver = new Observer<HashMap<String, FirebasePredictionData>>() {
        @Override
        public void onChanged(HashMap<String, FirebasePredictionData> map) {

            List<FirebasePredictionData> price = new ArrayList<>();

            if(map == null)
                return;

            for (String key : map.keySet()) {
                FirebasePredictionData data = map.get(key);
                data.setName(key);
                price.add(data);
            }

            priceAdapter.setItems(price);

        }
    };

    private Observer<? super HashMap<String, FirebasePredictionData>> mImportPredictionObserver = new Observer<HashMap<String, FirebasePredictionData>>() {
        @Override
        public void onChanged(HashMap<String, FirebasePredictionData> map) {

            List<FirebasePredictionData> imp = new ArrayList<>();

            if(map == null)
                return;

            for (String key : map.keySet()) {
                FirebasePredictionData data = map.get(key);
                data.setName(key);
                imp.add(data);
            }

            importAdapter.setItems(imp);

        }
    };

    @OnClick(R.id.btn_add_org)
    public void onAddOrganizationClick(){
        if(listener != null)
            listener.onAddOrganizationClick();
    }

    @OnClick(R.id.view_org)
    public void onViewOrganizationClick(){
        if(listener != null)
            listener.onViewOrganizationClick();
    }

    public interface FragmentEventListener{
        void onAddOrganizationClick();
        void onViewOrganizationClick();
    }
}
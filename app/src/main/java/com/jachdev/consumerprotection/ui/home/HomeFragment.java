package com.jachdev.consumerprotection.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.commonlibs.widget.CircleImageView;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.util.Helper;
import com.jachdev.consumerprotection.util.SessionManager;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
        homeViewModel.getUser().observe(getViewLifecycleOwner(), mUserObserver);
        homeViewModel.getOrganization().observe(getViewLifecycleOwner(), mOrgObserver);
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
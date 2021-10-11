package com.jachdev.consumerprotection.ui.vendor;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.commonlibs.widget.CircleImageView;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.commonlibs.widget.CustomImageView;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AddOrgRequest;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Category;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.data.enums.PickerType;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.ui.adapter.CommonAdaptor;
import com.jachdev.consumerprotection.util.Helper;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViewOrgFragment extends BaseFragment {
    private static final String TAG = ViewOrgFragment.class.getSimpleName();

    @BindView(R.id.iv_logo)
    CustomImageView ivLogo;
    @BindView(R.id.tv_org_name)
    CustomTextView tvOrgName;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private FragmentEventListener listener;
    private AppService service;
    private CommonAdaptor<Shop> adaptor;
    private List<Shop> shops = new ArrayList<>();

    public static Fragment newInstance() {
        return new ViewOrgFragment();
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_view_org;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        service = AppApplication.getInstance().getAppService();
        adaptor = new CommonAdaptor<>(getBaseActivity(), shops, new CommonAdaptor.CommonAdaptorCallback() {
            @Override
            public void onView(int viewType, int position) {

            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(adaptor);

        Organization organization = SessionManager.getInstance().getOrganization();

        Helper.loadImageByUrl(ivLogo, organization.getLogo());
        tvOrgName.setAnyText(organization.getName());

        getShops(organization.getId());
    }

    private void getShops(long id) {
        showWaiting();
        service.getServer().getShops(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AppResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NotNull AppResponse response) {
                        dismissWaiting();
                        switch (response.getCode()){
                            case 0:
                                List<Shop> shops = Arrays.asList(response.getObjectToType(Shop[].class).clone());
                                setShops(shops);
                                break;
                            default:
                                showMessage(response.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        showMessage(e.getMessage());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        listener = (FragmentEventListener) context;
        super.onAttach(context);
    }

    @OnClick(R.id.btnAddShop)
    public void onAddShopClick(){
        if(listener != null)
            listener.onAddShopClick();
    }

    public void setShops(List<Shop> shops) {
        try{

            this.shops = shops;
            adaptor.setItems(shops);
        }catch (Exception e){}
    }

    public interface FragmentEventListener{
        void onAddShopClick();
    }
}
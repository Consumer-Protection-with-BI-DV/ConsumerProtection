package com.jachdev.consumerprotection.ui.vendor;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.commonlibs.validator.Validator;
import com.jachdev.commonlibs.widget.CircleImageView;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.Address;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Category;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.Helper;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddShopFragment extends BaseFragment {
    private static final String TAG = AddShopFragment.class.getSimpleName();

    @BindView(R.id.et_name)
    CustomEditText et_name;
    @BindView(R.id.et_phone)
    CustomEditText et_phone;
    @BindView(R.id.et_address)
    CustomTextView et_address;

    private Address address;
    private AppService service;

    private FragmentEventListener listener;

    public static Fragment newInstance() {
        return new AddShopFragment();
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_add_shop;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        service = AppApplication.getInstance().getAppService();
    }

    @Override
    public void onAttach(Context context) {
        listener = (FragmentEventListener) context;
        super.onAttach(context);
    }

    @OnClick(R.id.et_address)
    public void onAddressClick(){
        if(listener != null)
            listener.onAddressClick();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitClick(){
        if(!Validator.isValidField(et_name) || !Validator.isValidPhoneNumber(et_phone)){
            return;
        }
        if(et_address.getText().length() == 0){
            showMessage(getString(R.string.error_add_a_location));
        }

        Shop shop = new Shop();
        shop.setName(et_name.getTrimText());
        shop.setAddress(et_address.getTrimText());
        shop.setPhoneNumber(et_phone.getTrimText());
        shop.setOrganizationId(SessionManager.getInstance().getOrganization().getId());

        if(address != null)
            shop.setLocation(address.getLatitude(), address.getLongitude());


        showWaiting();
        service.getServer().addShop(shop)
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
                                if(listener != null)
                                    listener.onShopAdded(shops);
                                showMessage(getString(R.string.successfully_added));
                                break;
                            default:
                                showError(response.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        dismissWaiting();
                        showError(e.getMessage());
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });

    }

    public void setShopAddress(Address address) {
        this.address = address;
        String loc = address.getLongAddress();
        et_address.setAnyText(loc);
    }

    private void showError(String message){

        ChocoBar.builder().setBackgroundColor(Color.parseColor("#FFA61B1B"))
                .setTextSize(18)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(message)
                .setMaxLines(4)
                .centerText()
                .setActionText(getString(R.string.ok))
                .setActivity(getBaseActivity())
                .build()
                .show();
    }

    public interface FragmentEventListener{
        void onAddressClick();
        void onShopAdded(List<Shop> shops);
    }
}
package com.jachdev.consumerprotection.ui.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.enums.PickerType;
import com.jachdev.consumerprotection.ui.vendor.AddOrgFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import butterknife.OnClick;

public class DashboardFragment extends BaseFragment {

    private DashboardViewModel dashboardViewModel;
    private FragmentEventListener listener;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_dashboard;
    }

    @Override
    public void onAttach(Context context) {
        listener = (FragmentEventListener) context;
        super.onAttach(context);
    }

    @OnClick(R.id.btnNearestStocks)
    public void onNearestStocksClick(){
        if(listener != null)
            listener.onNearestStocksClick();
    }

    @OnClick(R.id.btnNearestShops)
    public void onNearestShopsClick(){
        if(listener != null)
            listener.onNearestShopsClick();
    }

    @OnClick(R.id.btnImport)
    public void onImportClick(){
        if(listener != null)
            listener.onImportClick();
    }

    @OnClick(R.id.btnPrice)
    public void onPriceClick(){
        if(listener != null)
            listener.onPriceClick();
    }

    @OnClick(R.id.btnSale)
    public void onSaleClick(){
        if(listener != null)
            listener.onSaleClick();
    }

    @OnClick(R.id.btnComplaints)
    public void onComplaintsClick(){
        if(listener != null)
            listener.onComplaintsClick();
    }

    @OnClick(R.id.btnContactUS)
    public void onContactUsClick(){
        if(listener != null)
            listener.onContactUsClick();
    }

    public interface FragmentEventListener{
        void onNearestStocksClick();
        void onNearestShopsClick();
        void onImportClick();
        void onPriceClick();
        void onComplaintsClick();
        void onContactUsClick();
        void onSaleClick();
    }
}
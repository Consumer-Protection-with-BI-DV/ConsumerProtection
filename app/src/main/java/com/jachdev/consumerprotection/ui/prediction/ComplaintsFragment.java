package com.jachdev.consumerprotection.ui.prediction;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.commonlibs.validator.Validator;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.PredictionData;
import com.jachdev.consumerprotection.data.PredictionRequest;
import com.jachdev.consumerprotection.data.Report;
import com.jachdev.consumerprotection.data.enums.PredictionType;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.Helper;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ComplaintsFragment extends BaseFragment {
    public static final String ACTION = "ACTION_COMPLAINTS";
    private static final String TAG = ComplaintsFragment.class.getSimpleName();

    @BindView(R.id.et_name)
    CustomEditText et_name;
    @BindView(R.id.et_email)
    CustomEditText et_email;
    @BindView(R.id.et_title)
    CustomEditText et_title;
    @BindView(R.id.et_description)
    CustomEditText et_description;

    private FragmentEventListener listener;
    private AppService service;

    public static Fragment newInstance() {
        ComplaintsFragment fragment = new ComplaintsFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_complaintsn;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        service = AppApplication.getInstance().getAppService();
    }

    @OnClick(R.id.btn_submit)
    void onClickSubmit(){
        if(!Validator.isValidField(et_name) || !Validator.isValidEmail(et_email) || !Validator.isValidField(et_title) || !Validator.isValidField(et_description)){
            return;
        }

        callComplaints();
    }

    private void callComplaints() {
        showWaiting();

        Report report = new Report();
        report.setUid(SessionManager.getInstance().getUser().getId());
        report.setName(et_name.getTrimText());
        report.setEmail(et_email.getTrimText());
        report.setTitle(et_title.getTrimText());
        report.setDescription(et_description.getTrimText());

        service.getServer().complaints(report)
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
                                showMessage(response.getMessage());
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

    @Override
    public void onAttach(Context context) {
        listener = (FragmentEventListener) context;
        super.onAttach(context);
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
    }
}
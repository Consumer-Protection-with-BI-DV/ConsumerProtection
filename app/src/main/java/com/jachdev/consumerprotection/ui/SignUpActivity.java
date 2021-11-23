package com.jachdev.consumerprotection.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.commonlibs.validator.Validator;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.data.enums.UserType;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    @BindView(R.id.rg_user_type)
    RadioGroup radioGroup;

    @BindView(R.id.et_name)
    CustomEditText etName;
    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.et_phone)
    CustomEditText etPhone;
    @BindView(R.id.et_password)
    CustomEditText etPassword;
    @BindView(R.id.et_confirm_password)
    CustomEditText etConfirmPassword;

    @BindView(R.id.tv_user_type)
    CustomTextView tvUserType;

    private AppService service;

    @Override
    protected int layoutRes() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = AppApplication.getInstance().getAppService();

        radioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
        ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
    }

    public void onSignUnClick(View view) {
        if(!Validator.isValidField(etName) || !Validator.isValidEmail(etEmail) || !Validator.isValidPhoneNumber(etPhone) || !Validator.isValidChangedPassword(etPassword, etConfirmPassword)){
            return;
        }

        callSignUp();
    }

    private void callSignUp() {
        User user = new User();
        user.setType((Integer) tvUserType.getTag());
        user.setEmail(etEmail.getTrimText());
        user.setNumber(etPhone.getTrimText());
        user.setPassword(etPassword.getTrimText());
        user.setBirthDay(0);
        user.setName(etName.getTrimText());
        user.setGender(0);

        service.getServer().signUp(user)
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<AppResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NotNull AppResponse response) {
                        switch (response.getCode()){
                            case 0:
                                onBackPressed();
                                break;
                            default:
                                showError(response.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private final RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup rg, int i) {
            String userType = null;
            switch (rg.getCheckedRadioButtonId()){
                case R.id.rb_admin:
                    tvUserType.setAnyText(UserType.ADMIN.getName());
                    tvUserType.setTag(UserType.ADMIN.getId());
                    break;
                case R.id.rb_consumer:
                    tvUserType.setAnyText(UserType.CONSUMER.getName());
                    tvUserType.setTag(UserType.CONSUMER.getId());
                    break;
                case R.id.rb_vendor:
                    tvUserType.setAnyText(UserType.VENDOR.getName());
                    tvUserType.setTag(UserType.VENDOR.getId());
                    break;
            }
        }
    };

    private void showError(String message){

        ChocoBar.builder().setBackgroundColor(Color.parseColor("#FFA61B1B"))
                .setTextSize(18)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(message)
                .setMaxLines(4)
                .centerText()
                .setActionText(getString(R.string.ok))
                .setActivity(this)
                .build()
                .show();
    }

}
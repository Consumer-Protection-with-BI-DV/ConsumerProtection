package com.jachdev.consumerprotection.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.commonlibs.validator.Validator;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.ui.SignUpActivity;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.et_password)
    CustomEditText etPassword;

    private AppService service;

    @Override
    protected int layoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        service = AppApplication.getInstance().getAppService();
    }

    public void onSignUpClick(View view) {
        activityToActivity(SignUpActivity.class);
    }

    public void onSignInClick(View view) {
        if(!Validator.isValidEmail(etEmail) || !Validator.isValidPassword(etPassword)){
            return;
        }

        callSignIn();
    }

    private void callSignIn() {
        User user = new User();
        user.setEmail(etEmail.getTrimText());
        user.setPassword(etPassword.getTrimText());

        service.getServer().login(user)
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
                                SessionManager.getInstance().setUser(response.getObjectToType(User.class));
                                activityToActivity(HomeActivity.class);
                                LoginActivity.this.finish();
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
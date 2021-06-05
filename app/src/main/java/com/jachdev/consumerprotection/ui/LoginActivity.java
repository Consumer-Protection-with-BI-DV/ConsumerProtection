package com.jachdev.consumerprotection.ui;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.view.View;

import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.commonlibs.validator.Validator;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.ui.SignUpActivity;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.et_password)
    CustomEditText etPassword;

    @Override
    protected int layoutRes() {
        return R.layout.activity_login;
    }

    public void onSignUpClick(View view) {
        activityToActivity(SignUpActivity.class);
    }

    public void onSignInClick(View view) {
        if(!Validator.isValidEmail(etEmail) || !Validator.isValidPassword(etPassword)){
            return;
        }

        activityToActivity(HomeActivity.class);
        LoginActivity.this.finish();
    }
}
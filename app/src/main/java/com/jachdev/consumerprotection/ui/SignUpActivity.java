package com.jachdev.consumerprotection.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.commonlibs.validator.Validator;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.enums.UserType;

public class SignUpActivity extends BaseActivity {

    @BindView(R.id.rg_user_type)
    RadioGroup radioGroup;

    @BindView(R.id.et_name)
    CustomEditText etName;
    @BindView(R.id.et_email)
    CustomEditText etEmail;
    @BindView(R.id.et_password)
    CustomEditText etPassword;
    @BindView(R.id.et_confirm_password)
    CustomEditText etConfirmPassword;

    @BindView(R.id.tv_user_type)
    CustomTextView tvUserType;

    @Override
    protected int layoutRes() {
        return R.layout.activity_sign_up;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
        radioGroup.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    public void onSignUnClick(View view) {
        if(!Validator.isValidField(etName) && !Validator.isValidEmail(etEmail) && !Validator.isValidChangedPassword(etPassword, etConfirmPassword)){
            return;
        }

        activityToActivity(HomeActivity.class);
        SignUpActivity.this.finish();
    }

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup rg, int i) {
            String userType = null;
            switch (rg.getCheckedRadioButtonId()){
                case R.id.rb_admin:
                    userType = UserType.ADMIN.getName();
                    break;
                case R.id.rb_consumer:
                    userType = UserType.CONSUMER.getName();
                    break;
                case R.id.rb_vendor:
                    userType = UserType.VENDOR.getName();
                    break;
            }

            tvUserType.setAnyText(userType);
        }
    };
}
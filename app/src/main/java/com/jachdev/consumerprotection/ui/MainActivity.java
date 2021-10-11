package com.jachdev.consumerprotection.ui;


import android.os.Bundle;

import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.util.SessionManager;

import androidx.annotation.Nullable;

public class MainActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isLoggedIn = SessionManager.getInstance().isLoggedIn();

        activityToActivity(isLoggedIn ? HomeActivity.class : LoginActivity.class);

        MainActivity.this.finish();
    }
}
package com.jachdev.consumerprotection.ui;


import android.os.Bundle;

import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.consumerprotection.R;

import androidx.annotation.Nullable;

public class MainActivity extends BaseActivity {

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityToActivity(LoginActivity.class);

        MainActivity.this.finish();
    }
}
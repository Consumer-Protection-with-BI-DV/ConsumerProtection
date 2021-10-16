package com.jachdev.consumerprotection.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.R;

import androidx.annotation.Nullable;

/**
 * Created by Asanka on 6/5/2021.
 */
public class HeaderView extends FrameLayout {
    private CustomTextView tvHeader;

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initViews();

        getHeaderText(attrs);
    }

    private void initViews(){
        View view = LayoutInflater.from(this.getContext())
                .inflate(R.layout.layout_header_view, null);

        tvHeader = view.findViewById(R.id.tv_header);

        this.addView(view);
    }

    private void getHeaderText(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderView);
        String text = a.getString(R.styleable.HeaderView_text);
        setHeaderText(text);
        a.recycle();
    }

    public void setHeaderText(String text){
        tvHeader.setAnyText(text);
    }
}

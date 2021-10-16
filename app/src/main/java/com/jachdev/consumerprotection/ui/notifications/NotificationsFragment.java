package com.jachdev.consumerprotection.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.Notification;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.ui.adapter.CommonAdaptor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class NotificationsFragment extends BaseFragment {

    private NotificationsViewModel notificationsViewModel;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.text_notifications)
    TextView textView;

    private CommonAdaptor<Notification> adaptor;
    private List<Notification> notifications = new ArrayList<>();

    @Override
    protected int layoutRes() {
        return R.layout.fragment_notifications;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        init();
    }

    private void init() {
        adaptor = new CommonAdaptor<>(getBaseActivity(), notifications, new CommonAdaptor.CommonAdaptorCallback() {
            @Override
            public void onView(int viewType, int position) {

            }
        });

        adaptor.setViewType(CommonAdaptor.VIEW_TYPE_NOTIFICATION);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseActivity(), LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(adaptor);

        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(List<Notification> notifications) {
                if(notifications == null){
                    textView.setText("No new notifications.");
                }else{

                    textView.setText("");
                }

                adaptor.setItems(notifications);

            }
        });
    }
}
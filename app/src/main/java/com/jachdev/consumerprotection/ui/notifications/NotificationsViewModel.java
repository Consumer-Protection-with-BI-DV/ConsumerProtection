package com.jachdev.consumerprotection.ui.notifications;

import android.util.Log;

import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Notification;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.data.enums.UserType;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NotificationsViewModel extends ViewModel {


    private static final String TAG = NotificationsViewModel.class.getSimpleName();
    private AppService service;
    private MutableLiveData<String> mText;
    private MutableLiveData<List<Notification>> data;

    public NotificationsViewModel() {
        service = AppApplication.getInstance().getAppService();
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<List<Notification>> getNotifications() {
        long id = 0;
        User user = SessionManager.getInstance().getUser();
        if (user.getUserType() != UserType.ADMIN){
            id = user.getId();
        }

        data = new MutableLiveData<>();

        service.getServer().getNotifications(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<AppResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NotNull AppResponse response) {
                        switch (response.getCode()){
                            case 0:
                                List<Notification> notifications = Arrays.asList(response.getObjectToType(Notification[].class).clone());
                                data.postValue(notifications);
                                break;
                            default:
                                data.postValue(null);
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {

                        data.postValue(null);
                    }
                });
        return data;
    }
}
package com.jachdev.consumerprotection.ui.home;

import android.util.Log;

import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.SessionManager;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeViewModel extends ViewModel {

    private static final String TAG = HomeViewModel.class.getSimpleName();
    private MutableLiveData<User> mUser;
    private MutableLiveData<Organization> mOrganization;

    private SessionManager sessionManager;
    private AppService service;

    public HomeViewModel() {
        sessionManager = SessionManager.getInstance();
        service = AppApplication.getInstance().getAppService();
        mUser = new MutableLiveData<User>();

        mUser.postValue(sessionManager.getUser());
    }

    public LiveData<User> getUser() {
        return mUser;
    }

    public LiveData<Organization> getOrganization() {

        mOrganization = new MutableLiveData<>();
        service.getServer().getOrganization(sessionManager.getUser().getId())
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
                                Organization organization = response.getObjectToType(Organization.class);
                                SessionManager.getInstance().setOrganization(organization);
                                mOrganization.postValue(organization);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        mOrganization.postValue(null);
                    }
                });

        return mOrganization;

    }
}
package com.jachdev.consumerprotection.ui.home;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.FirebasePredictionData;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.User;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
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
    private MutableLiveData<HashMap<String, FirebasePredictionData>> salesPredictionData;
    private MutableLiveData<HashMap<String, FirebasePredictionData>> pricePredictionData;
    private MutableLiveData<HashMap<String, FirebasePredictionData>> importPredictionData;
    private FirebaseDatabase database;

    private SessionManager sessionManager;
    private AppService service;

    public HomeViewModel() {
        sessionManager = SessionManager.getInstance();
        service = AppApplication.getInstance().getAppService();
        mUser = new MutableLiveData<User>();

        mUser.postValue(sessionManager.getUser());
        database = FirebaseDatabase.getInstance();
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
                                mOrganization.postValue(null);
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

    public LiveData<HashMap<String, FirebasePredictionData>> getSalesPredictionData() {
        DatabaseReference ref = database.getReference("sales");
        salesPredictionData = new MutableLiveData<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Gson gson = new Gson();
                String json = gson.toJson(snapshot.getValue());
                Type empMapType = new TypeToken<HashMap<String, FirebasePredictionData>>() {}.getType();
                HashMap<String, FirebasePredictionData> value = gson.fromJson(json, empMapType);
                salesPredictionData.postValue(value);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                salesPredictionData.postValue(null);
            }
        });

        return salesPredictionData;
    }

    public LiveData<HashMap<String, FirebasePredictionData>> getPricePredictionData() {
        DatabaseReference ref = database.getReference("price");
        pricePredictionData = new MutableLiveData<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Gson gson = new Gson();
                String json = gson.toJson(snapshot.getValue());
                Type empMapType = new TypeToken<HashMap<String, FirebasePredictionData>>() {}.getType();
                HashMap<String, FirebasePredictionData> value = gson.fromJson(json, empMapType);
                pricePredictionData.postValue(value);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                pricePredictionData.postValue(null);
            }
        });

        return pricePredictionData;
    }

    public LiveData<HashMap<String, FirebasePredictionData>> getImportPredictionData() {
        DatabaseReference ref = database.getReference("imports");
        importPredictionData = new MutableLiveData<>();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Gson gson = new Gson();
                String json = gson.toJson(snapshot.getValue());
                Type empMapType = new TypeToken<HashMap<String, FirebasePredictionData>>() {}.getType();
                HashMap<String, FirebasePredictionData> value = gson.fromJson(json, empMapType);
                importPredictionData.postValue(value);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                importPredictionData.postValue(null);
            }
        });

        return importPredictionData;
    }
}
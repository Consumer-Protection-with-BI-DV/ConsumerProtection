package com.jachdev.consumerprotection.ui.map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AllShopResponse;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.ui.adapter.CommonAdaptor;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    @BindView(R.id.bottom_sheet)
    LinearLayoutCompat layoutBottomSheet;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private BottomSheetBehavior sheetBehavior;
    private AppService service;
    private CommonAdaptor<AllShopResponse> adaptor;
    private List<AllShopResponse> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        service = AppApplication.getInstance().getAppService();
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        sheetBehavior.addBottomSheetCallback(mBottomSheetCallback);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null)
            mapFragment.getMapAsync(this);

        adaptor = new CommonAdaptor<>(MapsActivity.this, list, new CommonAdaptor.CommonAdaptorCallback() {
            @Override
            public void onView(int viewType, int position) {
                showBottomSheet();
                AllShopResponse response = list.get(position);
                focusCamera(false, response.getLatLong().getLat(), response.getLatLong().getLon());
            }
        });
        adaptor.setViewType(CommonAdaptor.VIEW_TYPE_SHOP_MAP);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MapsActivity.this, LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(adaptor);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        getAllShops();
    }

    @OnClick(R.id.btnBottomView)
    void btnShowBottomView(){
        showBottomSheet();
    }

    private void getAllShops() {
        service.getServer().getAllShops()
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
                                list = Arrays.asList(response.getObjectToType(AllShopResponse[].class).clone());
                                adaptor.setItems(list);

                                addMarker();
                                break;
                            default:
                                showError(response.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        showError(e.getMessage());
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private void addMarker() {
        for (int x = 0; x < list.size(); x++) {
            AllShopResponse response = list.get(x);
            LatLng latLng = new LatLng(response.getLatLong().getLat(), response.getLatLong().getLon());

            mMap.addMarker(new MarkerOptions().position(latLng).title(response.getName()).snippet(response.getAddress()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            if(x == list.size()-1){
                focusCamera(false, latLng.latitude, latLng.longitude);
            }
        }
    }

    private void showBottomSheet() {
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
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
                .setActivity(MapsActivity.this)
                .build()
                .show();
    }

    private void focusCamera(boolean animateCamera, double latitude, double longitude) {
        LatLng coordinate = new LatLng(latitude, longitude); //Store these lat lng values somewhere. These should be constant.
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15);
        mMap.animateCamera(location);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull @NotNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_HIDDEN:
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    break;
                case BottomSheetBehavior.STATE_COLLAPSED:
                    break;
                case BottomSheetBehavior.STATE_DRAGGING:
                    break;
                case BottomSheetBehavior.STATE_SETTLING:
                    break;
            }
        }

        @Override
        public void onSlide(@NonNull @NotNull View bottomSheet, float slideOffset) {

        }
    };
}
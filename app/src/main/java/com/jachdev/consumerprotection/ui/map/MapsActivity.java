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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AllShopResponse;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.ui.adapter.CommonAdaptor;
import com.jachdev.consumerprotection.util.Helper;
import com.jachdev.consumerprotection.util.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
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

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback locationCallback;
    private Location mLastKnownLocation;

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
    protected void onStop() {
        super.onStop();

        if(mFusedLocationProviderClient != null && locationCallback != null)
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        checkLocationPermission();
    }

    @OnClick(R.id.btnBottomView)
    void btnShowBottomView(){
        showBottomSheet();
    }

    private void checkLocationPermission() {
        Dexter.withActivity(MapsActivity.this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        showCurrentLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        showSettingAlert();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
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
                                addToAdapter(list);

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

    private void addToAdapter(List<AllShopResponse> list) {

        for (AllShopResponse response : list) {
            String distance = Helper.distance(mLastKnownLocation.getLatitude(),
                    mLastKnownLocation.getLongitude(), response.getLatLong().getLat(),
                    response.getLatLong().getLon());

            response.setDistance(distance);
        }

        adaptor.setItems(list);
    }

    private void addMarker() {
        for (int x = 0; x < list.size(); x++) {
            AllShopResponse response = list.get(x);
            LatLng latLng = new LatLng(response.getLatLong().getLat(), response.getLatLong().getLon());

            mMap.addMarker(new MarkerOptions().position(latLng).title(response.getName()).snippet(response.getAddress()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

    @SuppressLint("MissingPermission")
    private void showCurrentLocation() {


        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //check if gps is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(MapsActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MapsActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MapsActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MapsActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                focusCamera(false, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                                getAllShops();
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        focusCamera(false, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

                                        getAllShops();

                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                            }
                        } else {
                            Toast.makeText(MapsActivity.this, getString(R.string.unable_to_get_your_location), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showSettingAlert() {
        ChocoBar.builder().setBackgroundColor(Color.parseColor("#FFA61B1B"))
                .setTextSize(18)
                .setTextColor(Color.parseColor("#FFFFFF"))
                .setTextTypefaceStyle(Typeface.ITALIC)
                .setText(getString(R.string.allow_location_permissions))
                .setMaxLines(4)
                .centerText()
                .setActionText(getString(R.string.ok))
                .setActivity(MapsActivity.this)
                .build()
                .show();
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
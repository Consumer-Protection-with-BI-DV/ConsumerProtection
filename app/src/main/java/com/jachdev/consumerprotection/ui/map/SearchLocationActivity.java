package com.jachdev.consumerprotection.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.util.Helper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.pd.chocobar.ChocoBar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchLocationActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

    public static final String MY_LOCATION_DATA = "com.lankawe.ui.map.MY_LOCATION_DATA";

    private static final String TAG = SearchLocationActivity.class.getSimpleName();
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1001;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    private com.jachdev.consumerprotection.data.Address mAddress;

    @BindView(R.id.tvAddress)
    CustomTextView tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        ButterKnife.bind(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);

        checkLocationPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
        
        if(mFusedLocationProviderClient != null && locationCallback != null)
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        tvAddress.setAnyText(getString(R.string.loading));
    }

    @Override
    public void onCameraIdle() {
        if (mMap != null) {
            LatLng currentMarkerLocation = mMap.getCameraPosition().target;

            Geocoder geocoder = new Geocoder(SearchLocationActivity.this, Locale.getDefault());

            try {
                List<Address> location = geocoder.getFromLocation(currentMarkerLocation.latitude,
                        currentMarkerLocation.longitude, 1);

                if(location.isEmpty()){
                    tvAddress.setAnyText(getString(R.string.address));
                    return;
                }

                Address address = location.get(0);
                tvAddress.setAnyText(address.getAddressLine(0) == null ? ""
                        : address.getAddressLine(0));

                mAddress = new com.jachdev.consumerprotection.data.Address();

                String line1 = (address.getSubThoroughfare() == null ? "" :
                        address.getSubThoroughfare()) + " " + (address.getThoroughfare() == null ? "" :
                                address.getThoroughfare());
                String line2 = address.getSubLocality() != null ? address.getSubLocality() :
                        (address.getLocality() != null ? address.getLocality() : "");
                String line3 = address.getSubLocality() == null ?
                        (address.getLocality() != null ? address.getLocality() : "") : "";

                mAddress.setLine1(line1.trim().isEmpty() ? address.getFeatureName() : line1);
                mAddress.setLine2(line2);
                mAddress.setLine3(!line2.equalsIgnoreCase(line3) ? line3 : "");
                mAddress.setPostalCode(address.getPostalCode());
                mAddress.setCountry(address.getCountryName());

                mAddress.setLatitude(address.getLatitude());
                mAddress.setLongitude(address.getLongitude());

                Log.d(TAG, "address: " + mAddress.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick(R.id.btnDone)
    void onClickDone(){
        if(mAddress != null){

            Intent intent = new Intent();
            intent.putExtra(MY_LOCATION_DATA, mAddress);

            setResult(RESULT_OK, intent);

            SearchLocationActivity.this.finish();
        }else{
            Toast.makeText(SearchLocationActivity.this,
                    getString(R.string.error_no_address), Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.ivCurrentLocation)
    void onClickCurrentLocation(){
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        Dexter.withActivity(SearchLocationActivity.this)
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(SearchLocationActivity.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(SearchLocationActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(SearchLocationActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(SearchLocationActivity.this, 51);
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
                                
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        if (mLastKnownLocation != null)
                                            focusCamera(false, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                            
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                            }
                        } else {
                            Toast.makeText(SearchLocationActivity.this, getString(R.string.unable_to_get_your_location), Toast.LENGTH_SHORT).show();
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
                .setActivity(SearchLocationActivity.this)
                .build()
                .show();
    }

    private void showAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", SearchLocationActivity.this.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_ID_MULTIPLE_PERMISSIONS);
    }

    private void focusCamera(boolean animateCamera, double latitude, double longitude) {
        LatLng coordinate = new LatLng(latitude, longitude); //Store these lat lng values somewhere. These should be constant.
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 15);
        mMap.animateCamera(location);
    }
}

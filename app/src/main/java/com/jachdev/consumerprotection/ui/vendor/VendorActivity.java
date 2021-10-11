package com.jachdev.consumerprotection.ui.vendor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.consumerprotection.AppConstant;
import com.jachdev.consumerprotection.BuildConfig;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.Address;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.Shop;
import com.jachdev.consumerprotection.data.enums.PickerType;
import com.jachdev.consumerprotection.ui.HomeActivity;
import com.jachdev.consumerprotection.ui.map.SearchLocationActivity;
import com.jachdev.consumerprotection.util.Helper;
import com.jachdev.consumerprotection.util.SessionManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.List;

public class VendorActivity extends BaseActivity implements AddOrgFragment.FragmentEventListener,
        ViewOrgFragment.FragmentEventListener, AddShopFragment.FragmentEventListener {

    private static final int MAP_ACTIVITY_REQUEST = 101;

    @Override
    protected int layoutRes() {
        return R.layout.activity_vendor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Organization organization = SessionManager.getInstance().getOrganization();

        if(organization == null){

            setTitle(R.string.add_organization);
            startFragment(R.id.nav_host_fragment, AddOrgFragment.newInstance(), false);
        }else{

            setTitle(R.string.view_organization);
            startFragment(R.id.nav_host_fragment, ViewOrgFragment.newInstance(), false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkCameraPermissions();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);

            for (int x = 0; x < images.size(); x++) {

                Image image = images.get(x);
                try {
                    File file = Helper.compressToFile(VendorActivity.this, new File(image.getPath()));
                    if(getFragment() instanceof AddOrgFragment){
                        AddOrgFragment addOrgFragment = (AddOrgFragment) getFragment();
                        addOrgFragment.setLogo(file.getPath());
                    }
                } catch (Exception e) {
                    showMessage(getString(R.string.error_please_insert_a_valid_image));
                }
            }
        }

        if (requestCode == MAP_ACTIVITY_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getParcelableExtra(SearchLocationActivity.MY_LOCATION_DATA) != null) {
                Address address = (Address) data.getParcelableExtra(SearchLocationActivity.MY_LOCATION_DATA);

                if (getFragment() instanceof AddShopFragment) {
                    ((AddShopFragment) getFragment()).setShopAddress(address);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onImagePicker(PickerType type) {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                BuildConfig.APPLICATION_ID + "/.photo");
        switch (type){
            case GALLERY:

                ImagePicker.create(VendorActivity.this)
                        .imageFullDirectory(file.getPath())
                        .includeVideo(false)
                        .limit(AppConstant.IMAGE_LIMIT)
                        .showCamera(false)
                        .folderMode(true)
                        .start();
                break;
            case CAMERA:
                ImagePicker.cameraOnly()
                        .imageFullDirectory(file.getPath())
                        .start(VendorActivity.this);
                break;
        }
    }

    private void checkCameraPermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VendorActivity.this);
        builder.setTitle(getString(R.string.need_permissions));
        builder.setMessage(getString(R.string.need_permissions_to_use_this_feature));
        builder.setPositiveButton(getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onAddShopClick() {

        setTitle(R.string.add_shop);
        startFragment(R.id.nav_host_fragment, AddShopFragment.newInstance(), true);
    }

    @Override
    public void onAddressClick() {
        activityToActivity(new Intent(VendorActivity.this,
                SearchLocationActivity.class), MAP_ACTIVITY_REQUEST);
    }

    @Override
    public void onShopAdded(List<Shop> shops) {
        onBackPressed();
    }
}
package com.jachdev.consumerprotection.ui.vendor;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jachdev.commonlibs.base.BaseFragment;
import com.jachdev.commonlibs.widget.CircleImageView;
import com.jachdev.commonlibs.widget.CustomEditText;
import com.jachdev.commonlibs.widget.CustomTextView;
import com.jachdev.consumerprotection.AppApplication;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.AddOrgRequest;
import com.jachdev.consumerprotection.data.AppResponse;
import com.jachdev.consumerprotection.data.Category;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.enums.PickerType;
import com.jachdev.consumerprotection.network.AppService;
import com.jachdev.consumerprotection.util.Helper;
import com.jachdev.consumerprotection.util.SessionManager;
import com.pd.chocobar.ChocoBar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddOrgFragment extends BaseFragment {
    private static final String TAG = AddOrgFragment.class.getSimpleName();

    @BindView(R.id.et_organization_name)
    CustomEditText etOrgName;
    @BindView(R.id.et_description)
    CustomEditText etOrgDescription;
    @BindView(R.id.et_organization_type)
    CustomTextView etOrgType;
    @BindView(R.id.iv_logo)
    CircleImageView ivLogo;

    private FragmentEventListener listener;
    private Category currentCategory;
    private String logo;

    private AppService service;
    private List<Category> categories = new ArrayList<>();

    public static Fragment newInstance() {
        return new AddOrgFragment();
    }

    @Override
    protected int layoutRes() {
        return R.layout.fragment_add_org;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        service = AppApplication.getInstance().getAppService();

        getCategories();
    }

    @Override
    public void onAttach(Context context) {
        listener = (FragmentEventListener) context;
        super.onAttach(context);
    }

    @OnClick(R.id.et_organization_type)
    public void onCategoryClick(){
        String[] cats = new String[categories.size()];
        for (int x = 0; x < categories.size(); x++) {
            Category c = categories.get(x);
            cats[x] = c.getType();
        }

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Choose a category");
        builder.setItems(cats, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etOrgType.setAnyText(categories.get(which).getType());

                currentCategory = categories.get(which);
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.btn_add_logo)
    public void onAddLogoClick(){
        String[] cats = new String[]{getString(R.string.from_gallery), getString(R.string.from_camera)};

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle("Add logo");
        builder.setItems(cats, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(listener != null)
                    listener.onImagePicker(which == 0 ? PickerType.GALLERY : PickerType.CAMERA);
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitClick(){
        showWaiting();
        AddOrgRequest request = new AddOrgRequest();
        request.setName(etOrgName.getTrimText());
        request.setDescription(etOrgDescription.getTrimText());
        request.setShopTypeId(currentCategory.getId());
        request.setLogo(logo);
        request.setUserId(SessionManager.getInstance().getUser().getId());
        request.createDataPart();

        service.getServer().addOrganization(request.getMultiParts())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<AppResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NotNull AppResponse response) {
                        dismissWaiting();
                        switch (response.getCode()){
                            case 0:
                                Organization organization = response.getObjectToType(Organization.class);
                                SessionManager.getInstance().setOrganization(organization);
                                getBaseActivity().finish();
                                break;
                            default:
                                showError(response.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        dismissWaiting();
                        showError(e.getMessage());
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private void getCategories() {
        showWaiting();
        service.getServer().getCategories()
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<AppResponse>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(@NotNull AppResponse response) {
                        dismissWaiting();
                        switch (response.getCode()){
                            case 0:
                                categories = Arrays.asList(response.getObjectToType(Category[].class).clone());
                                break;
                            default:
                                showError(response.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        dismissWaiting();
                        showError(e.getMessage());
                        Log.d(TAG, "onError: " + e.getMessage());
                    }
                });
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
                .setActivity(getBaseActivity())
                .build()
                .show();
    }

    public void setLogo(String path) {
        this.logo = path;
        Helper.loadImageByFilePath(ivLogo, path);
    }

    public interface FragmentEventListener{
        void onImagePicker(PickerType type);
    }
}
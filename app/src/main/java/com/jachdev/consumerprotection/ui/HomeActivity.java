package com.jachdev.consumerprotection.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.enums.PredictionType;
import com.jachdev.consumerprotection.ui.dashboard.DashboardFragment;
import com.jachdev.consumerprotection.ui.home.HomeFragment;
import com.jachdev.consumerprotection.ui.map.MapsActivity;
import com.jachdev.consumerprotection.ui.prediction.ComplaintsFragment;
import com.jachdev.consumerprotection.ui.prediction.ContactUsFragment;
import com.jachdev.consumerprotection.ui.prediction.PredictionActivity;
import com.jachdev.consumerprotection.ui.prediction.PredictionFragment;
import com.jachdev.consumerprotection.ui.vendor.VendorActivity;
import com.jachdev.consumerprotection.util.SessionManager;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import butterknife.BindView;

public class HomeActivity extends BaseActivity implements HomeFragment.FragmentEventListener, DashboardFragment.FragmentEventListener{

    private static final String TAG = HomeActivity.class.getSimpleName();
    @BindView(R.id.nav_view)
    BottomNavigationView navigationView;

    private NavController navController;

    @Override
    protected int layoutRes() {
        return R.layout.activity_home;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(mOnDestinationChangedListener);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SessionManager.getInstance().logoutSession();

        activityToActivity(LoginActivity.class);
        HomeActivity.this.finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddOrganizationClick() {

        activityToActivity(VendorActivity.class);
    }

    @Override
    public void onViewOrganizationClick() {

        activityToActivity(VendorActivity.class);
    }

    private NavController.OnDestinationChangedListener mOnDestinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull @NotNull NavController controller, @NonNull @NotNull NavDestination destination, @Nullable @org.jetbrains.annotations.Nullable Bundle arguments) {
            switch (controller.getCurrentDestination().getId()){
                case R.id.navigation_home:
                    navigationView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    @Override
    public void onNearestStocksClick() {
        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
        intent.setAction(PredictionFragment.ACTION);
        activityToActivity(intent, 0);
    }

    @Override
    public void onNearestShopsClick() {
        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
        intent.setAction(PredictionFragment.ACTION);
        activityToActivity(intent, 0);
    }

    @Override
    public void onImportClick() {
        Intent intent = new Intent(HomeActivity.this, PredictionActivity.class);
        intent.putExtra(PredictionActivity.KEY_PREDICTION_TYPE, PredictionType.IMPORT.getId());
        intent.setAction(PredictionFragment.ACTION);
        activityToActivity(intent, 0);
    }

    @Override
    public void onPriceClick() {
        Intent intent = new Intent(HomeActivity.this, PredictionActivity.class);
        intent.putExtra(PredictionActivity.KEY_PREDICTION_TYPE, PredictionType.PRICE.getId());
        intent.setAction(PredictionFragment.ACTION);
        activityToActivity(intent, 0);
    }

    @Override
    public void onSaleClick() {
        Intent intent = new Intent(HomeActivity.this, PredictionActivity.class);
        intent.putExtra(PredictionActivity.KEY_PREDICTION_TYPE, PredictionType.SALES.getId());
        intent.setAction(PredictionFragment.ACTION);
        activityToActivity(intent, 0);
    }

    @Override
    public void onComplaintsClick() {
        Intent intent = new Intent(HomeActivity.this, PredictionActivity.class);
        intent.setAction(ComplaintsFragment.ACTION);
        activityToActivity(intent, 0);
    }

    @Override
    public void onContactUsClick() {
        Intent intent = new Intent(HomeActivity.this, PredictionActivity.class);
        intent.setAction(ContactUsFragment.ACTION);
        activityToActivity(intent, 0);
    }
}
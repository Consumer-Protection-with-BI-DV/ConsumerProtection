package com.jachdev.consumerprotection.ui.prediction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.jachdev.commonlibs.base.BaseActivity;
import com.jachdev.consumerprotection.R;
import com.jachdev.consumerprotection.data.enums.PredictionType;

public class PredictionActivity extends BaseActivity implements PredictionFragment.FragmentEventListener, ComplaintsFragment.FragmentEventListener {

    public static final String KEY_PREDICTION_TYPE = "KEY_PREDICTION_TYPE";
    private static final String TAG = PredictionActivity.class.getSimpleName();

    @Override
    protected int layoutRes() {
        return R.layout.activity_prediction;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String action = intent.getAction();
        Log.d(TAG, "Action: " + action);

        if(action.equalsIgnoreCase(PredictionFragment.ACTION)){
            startPredictionFragment(intent);
        } else if(action.equalsIgnoreCase(ComplaintsFragment.ACTION)){

            startFragment(R.id.nav_host_fragment, ComplaintsFragment.newInstance(), false);
            setTitle(R.string.complaints);
        } else if(action.equalsIgnoreCase(ContactUsFragment.ACTION)){

            startFragment(R.id.nav_host_fragment, ContactUsFragment.newInstance(), false);
            setTitle(R.string.contact_us);
        }
    }

    private void startPredictionFragment(Intent intent) {

        PredictionType type = PredictionType.getPredictionType(intent.getIntExtra(KEY_PREDICTION_TYPE, 0));

        switch (type){
            case IMPORT:
                startFragment(R.id.nav_host_fragment, PredictionFragment.newInstance(PredictionType.IMPORT), false);
                setTitle(R.string.import_prediction);
                break;
            case PRICE:
                startFragment(R.id.nav_host_fragment, PredictionFragment.newInstance(PredictionType.PRICE), false);
                setTitle(R.string.price_prediction);
                break;
            case SALES:
                startFragment(R.id.nav_host_fragment, PredictionFragment.newInstance(PredictionType.SALES), false);
                setTitle(R.string.sales_prediction);
                break;
        }
    }
}
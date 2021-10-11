package com.jachdev.consumerprotection;

import android.app.Application;

import com.jachdev.consumerprotection.network.AppService;

/**
 * Created by Charitha Ratnayake(charitha.r@eyepax.com) on 9/20/2021.
 */
public class AppApplication extends Application {

    public static volatile AppApplication mApplication;
    private AppService mAppService;

    @Override
    public void onCreate() {
        super.onCreate();

        mApplication = this;

        mAppService = AppService.getInstance();
    }

    /**
     * this is Singleton class
     * @return : application class
     */
    public static AppApplication getInstance() {
        //Double check locking pattern
        if (mApplication == null) { //Check for the first time
            synchronized (AppApplication.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (mApplication == null)
                    mApplication = new AppApplication();
            }
        }
        return mApplication;
    }

    public AppService getAppService() {
        return mAppService;
    }
}

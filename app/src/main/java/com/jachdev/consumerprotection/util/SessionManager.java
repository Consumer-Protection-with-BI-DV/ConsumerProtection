package com.jachdev.consumerprotection.util;

import android.util.Log;

import com.jachdev.commonlibs.utils.ContextManager;
import com.jachdev.commonlibs.utils.TinyDb;
import com.jachdev.consumerprotection.data.Organization;
import com.jachdev.consumerprotection.data.User;


/**
 * singleton java class
 * https://medium.com/@kevalpatel2106/how-to-make-the-perfect-singleton-de6b951dfdb0
 */
public class SessionManager {
    private static volatile SessionManager sessionManagerInstance;

    private static final String TAG = SessionManager.class.getName();

    private static final String PACKAGE_NAME = SessionManager.class.getPackage() + ".";
    private static final String KEY_USER = PACKAGE_NAME + "KEY_USER";
    private static final String KEY_ORG = PACKAGE_NAME + "KEY_ORG";


    private TinyDb mTinyDB;

    private SessionManager() {
        mTinyDB = new TinyDb(ContextManager.getInstance());
        //Prevent form the reflection api.
        if (sessionManagerInstance != null) {
            mTinyDB = new TinyDb(ContextManager.getInstance());
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SessionManager getInstance() {
        //Double check locking pattern
        if (sessionManagerInstance == null) { //Check for the first time
            synchronized (SessionManager.class) {   //Check for the second time.
                //if there is no instance available... create new one
                if (sessionManagerInstance == null)
                    sessionManagerInstance = new SessionManager();
            }
        }
        return sessionManagerInstance;
    }

    /**
     * Logout user
     */
    public void logoutSession() {
        Log.d(TAG, "logoutSession");

        try{
            mTinyDB.remove(KEY_USER);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setUser(User user) {
        mTinyDB.putObject(KEY_USER, user);
    }

    public User getUser() {
        return mTinyDB.getObject(KEY_USER, User.class);
    }

    public boolean isLoggedIn() {
        return getUser() != null;
    }

    public void setOrganization(Organization organization) {
        mTinyDB.putObject(KEY_ORG, organization);
    }

    public Organization getOrganization() {
        return mTinyDB.getObject(KEY_ORG, Organization.class);
    }
}
